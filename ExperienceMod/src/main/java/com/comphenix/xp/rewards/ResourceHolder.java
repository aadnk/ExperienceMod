package com.comphenix.xp.rewards;

/**
 * Represents a given amount of an arbitrary resource.
 * 
 * @author Kristian
 */
public interface ResourceHolder {

	/**
	 * The given amount of this resource. 
	 * <p>
	 * Implementations that use a different data structure should attempt to convert to
	 * the integer value. 
	 * @return Amount of this resource.
	 */
	public int getAmount();
	
	/**
	 * A unique ENUM name representing this resource.
	 * @return Unique ENUM name representing this resource.
	 */
	public String getName();
	
	/**
	 * Adds the amount in this and the given resource holder together, returning a new holder
	 * with the result.
	 * 
	 * @param other - resource holder to add.
	 * @return The sum of this resource and the given resource.
	 */
	public ResourceHolder add(ResourceHolder other);
}
