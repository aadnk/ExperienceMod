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

package com.comphenix.xp.lookup;

import java.util.Collection;
import java.util.HashMap;

import com.comphenix.xp.Action;
import com.comphenix.xp.parser.Utility;
import com.comphenix.xp.parser.ParsingException;

public class PlayerRewards implements Multipliable<PlayerRewards> {

	// Quick lookup of reward types
	private static HashMap<String, Rewards> lookup = new HashMap<String, Rewards>();
	
	// Reward types and their range of experiences
	private HashMap<Rewards, Action> values;
	private double multiplier;

	public PlayerRewards(double multiplier) {
		this.multiplier = multiplier;
		this.values = new HashMap<Rewards, Action>();
	}
	
	// For cloning
	public PlayerRewards(PlayerRewards other, double newMultiplier) {
		
		if (other == null)
			throw new IllegalArgumentException("other");
		
		this.multiplier = newMultiplier;
		this.values = other.values;
	}

	@Override
	public PlayerRewards withMultiplier(double newMultiplier) {
		return new PlayerRewards(this, newMultiplier);
	}
	
	public enum Rewards {
		FISHING_SUCCESS("FISHING", "FISHING_SUCCESS", "CAUGHT_FISH"),
		FISHING_FAILURE("FISHING_FAILURE");
		
		private Rewards(String... names) {
			for (String name : names) {
				lookup.put(name, this);
			}
		}
		
		public static Rewards matchReward(String action) {
			return lookup.get(Utility.getEnumName(action));
		}
	}
	
	public void put(String key, Action value) throws ParsingException {
		
		Rewards rewardType = Rewards.matchReward(key);

		// Store this reward
		if (rewardType != null) {
			
			if (!values.containsKey(rewardType)) {
				
				// Handle inheritance automatically
				if (value.hasInheritance()) {
					Action previous = values.get(rewardType);
					
					if (previous != null) {
						value = value.inheritAction(previous);
					}
				}
				
				values.put(rewardType, value);
			} else {
				throw ParsingException.fromFormat("Duplicate player reward type detected: %s", key);
			}
			
		} else {
			throw ParsingException.fromFormat("Unrecognized player reward type: %s", key);
		}
	}
	
	public void putAll(PlayerRewards other) {
		
		// Copies all set values
		values.putAll(other.values);
	}
	
	public Action get(String key, Action defaultValue) {

		Rewards rewardType = Rewards.matchReward(key);
		
		// Get the reward, if it exists
		if (rewardType != null && values.containsKey(rewardType)) 
			return values.get(rewardType);
		else
			return defaultValue;
	}
	
	public Action get(Rewards key, Action defaultValue) {
		Action result = values.get(key);
	
		// Return result or default value
		return result != null ? result : defaultValue;
	}
	
	/**
	 * Retrieve every reward stored.
	 * @return Every reward.
	 */
	public Collection<Action> getValues() {
		return values.values();
	}

	public double getMultiplier() {
		return multiplier;
	}
	
	public Action getFishingSuccess() {
		return get(Rewards.FISHING_SUCCESS, Action.Default).multiply(multiplier);
	}

	public Action getFishingFailure() {
		return get(Rewards.FISHING_FAILURE, Action.Default).multiply(multiplier);
	}
}
