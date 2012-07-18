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

package com.comphenix.xp.rewards;

/**
 * Represents the different built-in reward types.
 * 
 * @author Kristian
 */
public enum RewardTypes {
	/**
	 * Reference to the default reward manager.
	 */
	DEFAULT(true),
	
	/**
	 * Rewards players with experience orbs.
	 */
	EXPERIENCE(false),
	
	/**
	 * Rewards players with experience directly by simply adding the experience to their experience bar.
	 */
	VIRTUAL(false),
	
	/**
	 * Rewards players with currency.
	 */
	ECONOMY(false),
	
	/**
	 * A custom reward manager. 
	 */
	CUSTOM(true);
	
	private boolean specialMarker;
	
	private RewardTypes(boolean isSpecialMarker) {
		this.specialMarker = isSpecialMarker;
	}
	
	/**
	 * Whether or not the reward type is an ID for a real reward manager.
	 * @return TRUE if this reward type is a unique reference to a reward manager, FALSE otherwise.
	 */
	public boolean isSpecialMarker() {
		return specialMarker;
	}
}

