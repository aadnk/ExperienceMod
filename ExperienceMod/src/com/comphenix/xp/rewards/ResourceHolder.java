package com.comphenix.xp.rewards;

/**
 * Represents a given amount of an arbitrary resource.
 * 
 * @author Kristian
 */
public interface ResourceHolder {

	/**
	 * The given amount of this resource.
	 * @return Amount of this resource.
	 */
	public int getAmount();
	
	/**
	 * A unique ENUM name representing this resource.
	 * @return Unique ENUM name representing this resource.
	 */
	public String getName();
}
