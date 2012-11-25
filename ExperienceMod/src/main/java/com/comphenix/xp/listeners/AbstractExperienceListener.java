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

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.comphenix.xp.Configuration;
import com.comphenix.xp.Presets;
import com.comphenix.xp.parser.ParsingException;

/**
 * Represents a listener with a reference to a map of configurations (presets).
 * @author Kristian
 */
public abstract class AbstractExperienceListener implements Listener {

	protected Presets presets;
	
	/**
	 * Retrieves the current presents.
	 * @return Current presets.
	 */
	public Presets getPresets() {
		return presets;
	}

	/**
	 * Sets the current presets.
	 * @param presets - the new presets.
	 */
	public void setPresets(Presets presets) {
		this.presets = presets;
	}
	
	/**
	 * Load the correct configuration for a given player.
	 * @param player - the given player.
	 * @return The most relevant configuration, or NULL if none were found.
	 */
	public Configuration getConfiguration(Player player) {
		try {
			return presets.getConfiguration(player);
			
		} catch (ParsingException e) {
			// We most likely have complained about this already
			return null;
		}
	}
	
	/**
	 * Load the correct configuration for general world events not associated with any player.
	 * @param world - the world to look for.
	 * @return The most relevant configuration, or NULL if none were found.
	 */
	public Configuration getConfiguration(World world) {
		try {
			return presets.getConfiguration(null, world.getName());
			
		} catch (ParsingException e) {
			//debugger.printDebug(this, "Preset error: %s", e.getMessage());
			return null;
		}
	}
}
