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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.InventoryView;

import com.comphenix.xp.Debugger;
import com.comphenix.xp.extra.Permissions;
import com.comphenix.xp.reflect.FieldUtils;
import com.comphenix.xp.reflect.MethodUtils;

public class ExperienceEnhancementsListener implements Listener {
		
	private Debugger debugger;
	private ErrorReporting report = ErrorReporting.DEFAULT;
	
	// Used by item enchant to swallow events
	private Map<String, Integer> overrideEnchant = new HashMap<String, Integer>();
	
	// Reflection helpers
	private Field costsField;
	private Method containerHandle;
	private Method entityMethod;
	private Method enchantItemMethod;
	
	public ExperienceEnhancementsListener(Debugger debugger) {
		this.debugger = debugger;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerDeathEvent(PlayerDeathEvent event) {
		
		// Handle exceptions too
		try {
			Player player = event.getEntity();
			
			if (player != null) {
				handlePlayerDeath(event, player);
			}
		} catch (Exception e) {
			report.reportError(debugger, this, e, event);
		}
	}
	
	private void handlePlayerDeath(PlayerDeathEvent event, Player player) {

		// Permission check
        if(Permissions.hasKeepExp(player)) {

            event.setDroppedExp(0);
            event.setKeepLevel(true);
            
            if (hasDebugger())
        		debugger.printDebug(this, "Prevented experience loss for %s.", player.getName());
            
        } else {
        	event.setKeepLevel(false);
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

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEnchantItemEvent(EnchantItemEvent event) {
		
		InventoryView view = event.getView();
		Integer slot = event.whichButton();
		
		HumanEntity player = (HumanEntity) event.getEnchanter();
		String name = player.getName();
		
		// Prevent infinite recursion and revert the temporary cost change
		if (overrideEnchant.containsKey(name)) {
			event.setExpLevelCost(overrideEnchant.get(name));
			return;
		}
		
		try {
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
					ref[slot] = 1;
					
					// We have to ignore the next enchant event
					overrideEnchant.put(name, oldCost);
					
					// Run the method again
					if (enchantItemMethod == null) {
						enchantItemMethod = getEnchantMethod(result, entity, "a");
					}
						
					// Attempt to call this method
					if (enchantItemMethod != null) {
						enchantItemMethod.invoke(result, entity, slot);
					}
					
					// OK, it's over
					overrideEnchant.remove(name);
				}
			}
			
			// A bunch or problems could occur
		} catch (IllegalAccessException e) {
			debugger.printWarning(this, "Cannot modify enchanting table: %s", e.toString());
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			debugger.printWarning(this, "Cannot modify enchanting table: %s", e.toString());
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			debugger.printWarning(this, "Cannot modify enchanting table: %s", e.toString());
			e.printStackTrace();
		}
	}
	
	private Method getEnchantMethod(Object container, Object entity, String methodName) {
		
		Method guess = MethodUtils.getMatchingAccessibleMethod(
						container.getClass(), methodName, new Class[] { entity.getClass(), int.class });
		
		if (guess != null) {
			// Great, got it on the first try
			return guess;
		} else {
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
	
	private void handleItemEnchanting(PrepareItemEnchantEvent event, Player player) {
		
		// Permission check
        if(Permissions.hasMaxEnchant(player)) {
        	
        	int[] costs = event.getExpLevelCostsOffered();
        	int index = costs.length - 1;
        	
        	if (index >= 0) {
        		costs[index] = getMaxBonus(event.getEnchantmentBonus(), index);

	            if (hasDebugger())
	        		debugger.printDebug(this, "Changed experience level costs for %s.", player.getName());
        		
        	} else if (hasDebugger())
        		debugger.printDebug(this, "Got empty list of experience costs.");
        }
	}
	
	public static int getMinBonus(int bookshelves, int slot) {
		
		Validate.isTrue(slot > 0, "Slot # cannot be less than zero.");
		Validate.isTrue(slot < 3, "Slot # cannot be greater than 3.");

		if (bookshelves > 15)
			bookshelves = 15;
		
		return getBonus(1 + bookshelves / 2, slot, bookshelves);
	}
	
	public static int getMaxBonus(int bookshelves, int slot) {
		
		Validate.isTrue(slot > 0, "Slot # cannot be less than zero.");
		Validate.isTrue(slot < 3, "Slot # cannot be greater than 3.");

		if (bookshelves > 15)
			bookshelves = 15;
		
		return getBonus(8 + bookshelves + bookshelves / 2, slot, bookshelves);
	}
	
	private static int getBonus(int input, int slot, int bookshelves) {
		// Helper function
		switch (slot) {
		case 0: 
			return Math.max(input / 3, 1); 
		case 1:
			return (input * 2) / 3 + 1;
		case 2:
			return Math.max(input, bookshelves * 2);
		default:
			throw new IllegalArgumentException("Unknown slot number " + slot);
		}
	}

	private boolean hasDebugger() {
		return debugger != null && debugger.isDebugEnabled();
	}
}
