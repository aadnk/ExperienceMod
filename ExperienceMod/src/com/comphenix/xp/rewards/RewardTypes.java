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

