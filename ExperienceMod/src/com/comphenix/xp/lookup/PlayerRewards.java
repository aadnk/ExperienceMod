package com.comphenix.xp.lookup;

import java.util.Collection;
import java.util.HashMap;


import com.comphenix.xp.Range;
import com.comphenix.xp.parser.Utility;
import com.comphenix.xp.parser.ParsingException;

public class PlayerRewards implements Multipliable<PlayerRewards> {

	// Quick lookup of reward types
	private static HashMap<String, Rewards> lookup = new HashMap<String, Rewards>();
	
	// Reward types and their range of experiences
	private HashMap<Rewards, Range> values;
	private double multiplier;

	public PlayerRewards(double multiplier) {
		this.multiplier = multiplier;
		this.values = new HashMap<Rewards, Range>();
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
	
	public void put(String key, Range value) throws ParsingException {
		
		Rewards rewardType = Rewards.matchReward(key);
		
		// Store this reward
		if (rewardType != null) {
			if (!values.containsKey(rewardType))
				values.put(rewardType, value);
			else
				throw ParsingException.fromFormat("Duplicate player reward type detected: %s", key);
		} else {
			throw ParsingException.fromFormat("Unrecognized player reward type: %s", key);
		}
	}
	
	public Range get(Rewards key, Range defaultValue) {
		Range result = values.get(key);
	
		// Return result or default value
		return result != null ? result : defaultValue;
	}
	
	/**
	 * Retrieve every reward stored.
	 * @return Every reward.
	 */
	public Collection<Range> getValues() {
		return values.values();
	}

	public double getMultiplier() {
		return multiplier;
	}
	
	public Range getFishingSuccess() {
		return get(Rewards.FISHING_SUCCESS, Range.Default).multiply(multiplier);
	}

	public Range getFishingFailure() {
		return get(Rewards.FISHING_FAILURE, Range.Default).multiply(multiplier);
	}
}
