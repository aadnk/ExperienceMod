/*
 *  ExperienceMod - Bukkit server plugin for modifying the experience system in Minecraft.
 *  Copyright (C) 2012 Kristian S. Stangeland
 *
 *  This program is free software; you can redistribute it and/or modify it under the terms of the 
 *  GNU General Public License as published by the Free Software Foundation; either version 2 of 
 *  the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with this program; 
 *  if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 
 *  02111-1307 USA
 */

package com.comphenix.xp.listeners;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.inventory.InventoryView;

import com.comphenix.xp.Configuration;
import com.comphenix.xp.Debugger;
import com.comphenix.xp.Presets;
import com.comphenix.xp.extra.ConstantRandom;
import com.comphenix.xp.extra.Permissions;
import com.comphenix.xp.reflect.FieldUtils;
import com.comphenix.xp.reflect.MethodUtils;
import com.comphenix.xp.rewards.items.RandomSampling;

public class ExperienceEnhancementsListener extends AbstractExperienceListener {
	
	// Constants (unfortunate naming)	
	private Debugger debugger;
	private ErrorReporting report = ErrorReporting.DEFAULT;
	
	// Used by item enchant to swallow events
	private Map<String, Integer> overrideEnchant = new HashMap<String, Integer>();
	
	// If we encounter any problem at all with our reflection trickery, we'll disable the maximum
	// enchant level at once. This could happen if CraftBukkit changes, or we're installed on a server
	// that is Bukkit-compatible only.
	private boolean disableEnchantingTrickery;
	
	// Reflection helpers
	private Field costsField;
	private Method containerHandle;
	private Method entityMethod;
	private Method enchantItemMethod;
	
	public ExperienceEnhancementsListener(Debugger debugger, Presets presets) {
		this.debugger = debugger;
		this.presets = presets;
	}
		
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEnchantItemEvent(EnchantItemEvent event) {

		int maxEnchant = 0;
		
		try {
			InventoryView view = event.getView();
			Integer slot = event.whichButton();
			
			Player player = event.getEnchanter();
			String name = player.getName();
			
			// Prevent infinite recursion and revert the temporary cost change
			if (overrideEnchant.containsKey(name)) {
				event.setExpLevelCost(overrideEnchant.get(name));
				return;
				
			} else if (disableEnchantingTrickery) {
				// Prevent too many errors from occurring
				return;
			}
			
			Configuration config = getConfiguration(player);
			maxEnchant = config.getMaximumEnchantLevel();
	
			double reverseFactor = (double)Configuration.DEFAULT_MAXIMUM_ENCHANT_LEVEL / (double)maxEnchant;
			 
			// Don't do anything if we're at the default enchanting level
			if (maxEnchant == Configuration.DEFAULT_MAXIMUM_ENCHANT_LEVEL) {
				return;
			}

			// Read the container-field in CraftInventoryView
			if (containerHandle == null)
				containerHandle = MethodUtils.getAccessibleMethod(view.getClass(), "getHandle", null);
			Object result = containerHandle.invoke(view);
					
			// Container should be of type net.minecraft.server.ContainerEnchantTable
			if (result != null) {
				// Cancel the original event
				event.setCancelled(true);
				
				Class<? extends Object> containerEnchantTable = result.getClass();
				
				// Read the cost-table
				if (costsField == null)
					costsField = FieldUtils.getField(containerEnchantTable, "costs");
				Object cost = FieldUtils.readField(costsField, result);
				
				// Get the real Minecraft player entity
				if (entityMethod == null)
					entityMethod = MethodUtils.getAccessibleMethod(player.getClass(), "getHandle", null);
				
				Object entity = entityMethod.invoke(player);
				
				if (cost instanceof int[]) {
					int[] ref = (int[]) cost;
					int oldCost = ref[slot];
					
					// Change the cost at the last second
					ref[slot] = (int) (ref[slot] * reverseFactor);
					
					if (hasDebugger()) {
						debugger.printDebug(this, "Modified slot %s from %s to %s.", slot, oldCost, ref[slot]);
					}
					
					// We have to ignore the next enchant event
					overrideEnchant.put(name, oldCost);
					
					// Run the method again
					if (enchantItemMethod == null) {
						enchantItemMethod = getEnchantMethod(result, entity, "a");
					}
						
					// Attempt to call this method
					if (enchantItemMethod != null) {
						enchantItemMethod.invoke(result, entity, slot);
					} else {
						debugger.printWarning(this, "Unable to modify slot %s cost. Reflection failed.", slot);
					}
					
					// OK, it's over
					overrideEnchant.remove(name);
				}
			}
			
			// A bunch or problems could occur
		} catch (Exception e) {
			ErrorReporting.DEFAULT.reportError(debugger, this, e, event, maxEnchant);
			disableEnchantingTrickery = true;
		}
	}
	
	private int getUnmodifiedMaximumEnchant(Configuration config) {
		
		// Calculate the highest enchanting level possible
		return getMaxBonus(config.getMaximumBookcaseCount(), config.getMaximumBookcaseCount(), 2);
	}
	
	private Method getEnchantMethod(Object container, Object entity, String methodName) {
		
		Method guess = MethodUtils.getMatchingAccessibleMethod(
						container.getClass(), methodName, new Class[] { entity.getClass(), int.class });
		
		if (guess != null) {
			// Great, got it on the first try
			return guess;
		} else {
			if (hasDebugger())
				debugger.printDebug(this, "Using fallback method to detect correct Minecraft method.");
			
			// Damn, something's wrong. The method name must have changed. Try again.
			methodName = lastMinecraftMethod();
			guess = MethodUtils.getMatchingAccessibleMethod(
						container.getClass(), methodName, new Class[] { entity.getClass(), int.class });
			
			if (guess != null)
				return guess;
			else
				debugger.printWarning(this, "Could not find method '%s' in ContainerEnchantTable.", methodName);
			return null;
		}
	}
	
	/**
	 * Determine the name of the last calling Minecraft method in the call stack.
	 * <p>
	 * A Minecraft method is any method in a class found in net.minecraft.* and below.
	 * @return The name of this method, or NULL if not found.
	 */
    private static String lastMinecraftMethod() {
        try {
            throw new Exception();
        } catch (Exception e) {
            // Determine who called us
            StackTraceElement[] elements = e.getStackTrace();
            
            for (StackTraceElement element : elements) {
            	if (element.getClassName().startsWith("net.minecraft")) {
            		return element.getMethodName();
            	}
            }
            
            // If none is found (very unlikely though)
            return null;
        }
    }
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPrepareItemEnchantEvent(PrepareItemEnchantEvent event) {

		// Like above
		try {
			final Player player = event.getEnchanter();

			if (player != null) {
				handleItemEnchanting(event, player);

				// Just in case this hasn't already been done
				overrideEnchant.remove(player.getName());
			}
			
		} catch (Exception e) {
			report.reportError(debugger, this, e, event);
		}
	}
    
	private void handleItemEnchanting(PrepareItemEnchantEvent event, Player player) {

		Random rnd = RandomSampling.getThreadRandom();
		
    	int[] costs = event.getExpLevelCostsOffered();
    	int last = costs.length - 1;
    	int bonus = event.getEnchantmentBonus();
    	
    	Configuration config = getConfiguration(player);
		int maxEnchant = config.getMaximumEnchantLevel();
		int maxUnmodified = getUnmodifiedMaximumEnchant(config);
       
    	// Probably won't occur, but we'll check it anyway
    	if (last == 0) {
    		if (hasDebugger())
    			debugger.printDebug(this, "Got empty list of experience costs.");
    		return;
    	}
    	
    	if (maxUnmodified != Configuration.DEFAULT_MAXIMUM_BOOKCASE_COUNT) {
    		if (config.getMaximumBookcaseCount() > Configuration.DEFAULT_MAXIMUM_BOOKCASE_COUNT) {
    			// Count blocks higher up
    			bonus = getCustomBookshelfCount(event.getEnchantBlock(), config.getMaximumBookcaseCount(), 0);
    			
        		if (hasDebugger())
        			debugger.printDebug(this, "Bookshelf count: %s", bonus);
    		}
    		
    		// We'll have to recreate the costs
    		for (int i = 0; i < 3; i++) {
    			costs[i] = getBonus(rnd, bonus, config.getMaximumBookcaseCount(), i);
    		}
    	}
    	
		// Permission check
        if(Permissions.hasMaxEnchant(player)) {
    		costs[last] = getMaxBonus(bonus, config.getMaximumBookcaseCount(), last);

            if (hasDebugger())
        		debugger.printDebug(this, "Changed experience level costs for %s.", player.getName());	
        }
        
        // Modify the displayed enchanting levels
        modifyCostList(maxEnchant, maxUnmodified, costs);
	}
	
	private void modifyCostList(int maxEnchant, int maxUnmodified, int[] costs) {
        
        // No need to do this if we're just using the default maximum
        if (maxEnchant != maxUnmodified) {
        	double enchantFactor = (double)maxEnchant / (double)maxUnmodified;
        	
        	for (int i = 0; i < 3; i++) {
        		costs[i] = (int) (costs[i] * enchantFactor);
        	}
        }
	}
	
	private int getCustomBookshelfCount(Block table, int maxBookshelfCount, int yOffset) {
		
		final int bookID = Material.BOOKSHELF.getId();
		final World world = table.getWorld();
		
		// Usually 15 bookshelves per vertical stack
		int height = (int) Math.ceil(maxBookshelfCount / 15.0);
		
		int x = table.getX();
		int y = table.getY();
		int z = table.getZ();
		int count = 0;
		
		for (int i = yOffset; i < height; i++) 
	    for (int j = -1; j <= 1; j++) 
	    for (int k = -1; k <= 1; k++) {
	            	
        	// We check every block in 2x2, except the middle, for air blocks. 
        	//  (seen from above)
        	//    A A A
        	//    A t A
        	//    A A A
        	//
        	// t is at origin, where j = 0 and k = 0.
        	//
        	// Legend: A = air, t = table, # = bookcase.
            if ((j != 0 || k != 0) && 
            		world.getBlockTypeIdAt(x + k, y + i, z + j) == 0) {
            	
            	// Next, we count the bookcases in a star shape around the air blocks and the enchanting table.
            	//   #   #   # 
            	//     A A A
            	//   # A t A #
            	//     A A A
            	//   #   #   #
                if (world.getBlockTypeIdAt(x + k * 2, y + i, z + j * 2) == bookID) {
                	count++;
                }

                // Count the two left over blocks in the corners:
            	//     #   #  
            	//   # A x A #
            	//     x t x 
            	//   # A x A #
            	//     #   # 
                // Legend: x = ignored blocks 
                if (k != 0 && j != 0) {
                    if (world.getBlockTypeIdAt(x + k * 2, y + i, z + j) == bookID) {
                    	count++;
                    }

                    if (world.getBlockTypeIdAt(x + k, y + i, z + j * 2) == bookID) {
                    	count++;
                    }
                }
            }
        }

		return count;
	}

	/**
	 * Determines the minimum level cost in a particular enchanting table slot.
	 * @param bookshelves - the number of bookshelves around the enchanting table.
	 * @param maxBookselfCount - the maximum number of bookshelves.
	 * @param slot - the slot number.
	 * @return The minimum level cost for this slot.
	 */
	public int getMinBonus(int bookshelves, int maxBookselfCount, int slot) {
		
		Validate.isTrue(slot > 0, "Slot # cannot be less than zero.");
		Validate.isTrue(slot < 3, "Slot # cannot be greater than 3.");

		return getBonus(ConstantRandom.MINIMUM, maxBookselfCount, bookshelves, slot);
	}
	
	/**
	 * Determines the maximum level cost in a particular enchanting table slot.
	 * @param bookshelves - the number of bookshelves around the enchanting table.
	 * @param slot - the slot number.
	 * @return The maximum level cost for this slot.
	 */
	public int getMaxBonus(int bookshelves, int maxBookselfCount, int slot) {
		
		Validate.isTrue(slot > 0, "Slot # cannot be less than zero.");
		Validate.isTrue(slot < 3, "Slot # cannot be greater than 3.");

		return getBonus(ConstantRandom.MAXIMUM, maxBookselfCount, bookshelves, slot);
	}
	
	/**
	 * Samples a level cost for a particular enchanting table, given the slot and number
	 * of bookshelves surrounding it.
	 * @param rnd - a random number generator.
	 * @param bookshelves - the number of bookshelves around the enchanting table.
	 * @param maxBookselfCount - the maximum number of bookshelves.
	 * @param slot - the slot number.
	 * @return The level cost in this slot.
	 */
	public int getBonus(Random rnd, int bookshelves, int maxBookselfCount, int slot) {
		// Clamp bookshelves
		if (bookshelves > maxBookselfCount) {
			bookshelves = maxBookselfCount;
		}

		int j = rnd.nextInt(8) + 1 + (bookshelves >> 1) + rnd.nextInt(bookshelves + 1);

		// Handle different slot factors
		switch (slot) {
		case 0: 
			return Math.max(j / 3, 1);
		case 1:
			return j * 2 / 3 + 1;
		case 2:
			return Math.max(j, bookshelves * 2);
		default:
			throw new IllegalArgumentException("Unknown slot number " + slot);
		}
	}

	private boolean hasDebugger() {
		return debugger != null && debugger.isDebugEnabled();
	}
}
