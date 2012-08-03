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

import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.comphenix.xp.Debugger;
import com.comphenix.xp.extra.Permissions;

public class ExperienceEnhancementsListener implements Listener {
		
	private Debugger debugger;
	private ErrorReporting report = ErrorReporting.DEFAULT;
	
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
			Player player = event.getEnchanter();
			
			if (player != null) {
				handleItemEnchanting(event, player);
			}
		} catch (Exception e) {
			report.reportError(debugger, this, e, event);
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
		
		final double[] slotFactor = { 0.5, 0.66, 1 };  
		Validate.isTrue(slot > 0, "Slot # cannot be less than zero.");
		Validate.isTrue(slot < 3, "Slot # cannot be greater than 3.");
		
		return (int) ((1 + bookshelves / 2) * slotFactor[slot]);
	}
	
	public static int getMaxBonus(int bookshelves, int slot) {
		
		final double[] slotFactor = { 0.5, 0.66, 1 };  
		Validate.isTrue(slot > 0, "Slot # cannot be less than zero.");
		Validate.isTrue(slot < 3, "Slot # cannot be greater than 3.");
		
		return (int) ((5 + bookshelves * 1.5f) * slotFactor[slot]);
	}
	
	private boolean hasDebugger() {
		return debugger != null && debugger.isDebugEnabled();
	}
}
