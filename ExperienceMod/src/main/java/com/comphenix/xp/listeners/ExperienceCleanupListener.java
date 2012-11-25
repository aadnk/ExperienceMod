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

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class ExperienceCleanupListener implements Listener {
	
	private List<PlayerCleanupListener> listeners = new ArrayList<PlayerCleanupListener>();
	private ErrorReporting report = ErrorReporting.DEFAULT;
	
	public ExperienceCleanupListener(PlayerCleanupListener... newListeners) {
		setPlayerCleanupListeners(newListeners);
	}
	
	/**
	 * Adds a player quit listener.
	 * @param listener - the player quit listener to add.
	 */
	public void addPlayerCleanupListener(PlayerCleanupListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Removes the given player quit listener.
	 * @param listener - the player quit listener to remove.
	 */
	public void removePlayerCleanupListener(PlayerCleanupListener listener) {
		listeners.remove(listener);
	}
	
	/**
	 * Replaces the current list of player listeners with the given list.
	 * @param newListeners - new list of player listeners.
	 */
	public void setPlayerCleanupListeners(PlayerCleanupListener... newListeners) {
		// Add every referenced listener
		for (PlayerCleanupListener listen : newListeners) {
			addPlayerCleanupListener(listen);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		
		try {
			Player player = event.getPlayer();
			
			if (player != null) {
				// Cleanup after the player is removed
				for (PlayerCleanupListener listen : listeners) {
					listen.removePlayerCache(player);
				}
			}
		
		} catch (Exception e) {
			// This probably won't happen anyway, but we'll handle it.
			report.reportError(null, this, e, event);
		}
	}
}
