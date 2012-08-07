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
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.Validate;
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

public class ExperienceEnhancementsListener implements Listener {
		
	private Debugger debugger;
	private ErrorReporting report = ErrorReporting.DEFAULT;
	
	// Used by item enchant to swallow events
	private Set<String> ignoreEnchant = new HashSet<String>();
	
	// Reflection helpers
	private Field containerField;
	private Field costsField;
	private Field entityField;
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
				ignoreEnchant.remove(player.getName());
			}
			
		} catch (Exception e) {
			report.reportError(debugger, this, e, event);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEnchantItemEvent(EnchantItemEvent event) {
		
		InventoryView view = event.getView();
		Player player = event.getEnchanter();
		
		// Prevent infinite recursion
		if (ignoreEnchant.contains(player.getName())) {
			return;
		}
		
		try {
			// Read the container-field in CraftInventoryView
			if (containerField == null)
				containerField = FieldUtils.getField(view.getClass(), "container", true);
			Object result = FieldUtils.readField(containerField, view, true);
			
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
				if (entityField == null)
					entityField = FieldUtils.getField(player.getClass(), "entity", true);
				Object entity = FieldUtils.readField(entityField, player, true);
				
				if (cost instanceof int[]) {
					int[] ref = (int[]) cost;
				
					// Set the second slot
					ref[1] = 1;
					
					// We have to ignore the next enchant event
					ignoreEnchant.add(player.getName());
					
					// Run the method again
					if (enchantItemMethod == null)
						enchantItemMethod = containerEnchantTable.getMethod("a", entity.getClass(), int.class);
					enchantItemMethod.invoke(result, entity, event.whichButton());
					
					
					// OK, it's over
					ignoreEnchant.remove(player.getName());
				}
			}
			
			// A bunch or problems could occur
		} catch (NoSuchMethodException e) {
			debugger.printWarning(this, "Cannot modify enchanting table: %s", e.toString());
			e.printStackTrace();
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
