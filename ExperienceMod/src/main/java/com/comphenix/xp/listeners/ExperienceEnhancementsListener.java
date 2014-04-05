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

import java.util.Arrays;
import java.util.Random;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import com.comphenix.xp.Configuration;
import com.comphenix.xp.Debugger;
import com.comphenix.xp.Presets;
import com.comphenix.xp.extra.ConstantRandom;
import com.comphenix.xp.extra.PermissionSystem;
import com.comphenix.xp.rewards.items.RandomSampling;

public class ExperienceEnhancementsListener extends AbstractExperienceListener {	
	// Constants (unfortunate naming)	
	private Debugger debugger;
	private ErrorReporting report = ErrorReporting.DEFAULT;

	private AbstractSlotModifier slotModifier;
	
	public ExperienceEnhancementsListener(Debugger debugger, Presets presets, AbstractSlotModifier slotModifier) {
		this.debugger = debugger;
		this.presets = presets;
		this.slotModifier = slotModifier;
	}
		
	private int getUnmodifiedMaximumEnchant(Configuration config) {
		// Calculate the highest enchanting level possible
		return getMaxBonus(config.getMaximumBookcaseCount(), config.getMaximumBookcaseCount(), 2);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPrepareItemEnchantEvent(PrepareItemEnchantEvent event) {
		// Like above
		try {
			final Player player = event.getEnchanter();

			if (player != null) {
				handleItemEnchanting(event, player);
				slotModifier.onPreparedEnchanting(player);
			}
			
		} catch (Exception e) {
			report.reportError(debugger, this, e, event);
		}
	}
    
	private void handleItemEnchanting(PrepareItemEnchantEvent event, Player player) {
		Random rnd = RandomSampling.getThreadRandom();
		
		// Create a copy of the experience levels
		int[] output = event.getExpLevelCostsOffered();
    	int[] modified = output.clone();
    	int last = modified.length - 1;
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
    			modified[i] = getBonus(rnd, bonus, config.getMaximumBookcaseCount(), i);
    		}
    	}
    	
		// Permission check
        if(PermissionSystem.hasMaxEnchant(player)) {
    		modified[last] = getMaxBonus(bonus, config.getMaximumBookcaseCount(), last);

            if (hasDebugger())
        		debugger.printDebug(this, "Changed experience level costs for %s.", player.getName());	
        }
        
	    // No need to do this if we're just using the default maximum
	    if (maxEnchant != maxUnmodified) {
	    	double enchantFactor = (double)maxEnchant / (double)maxUnmodified;
	    	
	    	for (int i = 0; i < 3; i++) {
	    		modified[i] = (int) (modified[i] * enchantFactor);
	    	}
	    }
	    
        // Modify the displayed enchanting levels
	    slotModifier.modifyCostList(player, output, modified);
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
