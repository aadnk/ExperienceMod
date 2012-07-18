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

import org.bukkit.potion.Potion;

/**
 * Builds a potion with extra information.
 * 
 * @author Kristian
 */
public class PotionMarker {

	private static final int BIT_REWARDED = 0x1000;
	
	// The current stored damage value, representing the given potion
	private short damage;
	
	public PotionMarker(Potion potion) {
		this.damage = potion.toDamageValue();
	}
	
	public PotionMarker(short durability) {
		this.damage = durability;
	}
	
	/**
	 * Whether or not the potion has already rewarded a player with experience.
	 * @return TRUE if a reward has already been given, FALSE otherwise.
	 */
	public boolean hasBeenRewarded() {
		return hasBit(BIT_REWARDED);
	}
	
	/**
	 * Sets whether or not the potion has given a reward to the player.
	 * @param value TRUE if the reward has been given, FALSE otherwise.
	 */
	public void setBeenRewarded(boolean value) {
		setBit(BIT_REWARDED, value);
	}
	
	/**
	 * Resets all special potion markers.
	 */
	public void reset() {
		setBeenRewarded(false);
	}
	
	/**
	 * Constructs a potion from the original values together with the current markers.
	 * @return New potion with the correct values.
	 */
	public Potion toPotion() {
		return Potion.fromDamage(damage);
	}
	
	/**
	 * Returns the modified durability value.
	 * @return Modified durability value.
	 */
	public short toDurability() {
		return damage;
	}
	
	// Check for a bit
	private boolean hasBit(int bitMask) {
		return (damage & bitMask) != 0;
	}
	
	// Set a bit
	private void setBit(int bitMask, boolean value) {
		if (value) {
			damage |= bitMask;
		} else {
			damage &= ~bitMask;
		}
	}
}
