package com.comphenix.xp.rewards;

import java.util.Random;

import com.comphenix.xp.lookup.Multipliable;

/**
 * Represents a resource holder factory.
 * 
 * @author Kristian
 */
public interface ResourceFactory extends Multipliable<ResourceFactory> {

	/**
	 * Retrieve a resource holder, using the given random number generator to determine
	 * the amount of the given resource to return.
	 * 
	 * @param rnd - random number generator to use.
	 * @return A resource holder.
	 */
	public ResourceHolder getResource(Random rnd);
	
	/**
	 * Calculates the minimum amount of resources that may be awarded.
	 * @param count - resource amount multiplier.
	 * @return A resource holder.
	 */
	public ResourceHolder getMinimum(int count);
	
	/**
	 * Calculates the maximum amount of resources that may be awarded.
	 * @param count - resource amount multiplier.
	 * @return A resource holder.
	 */
	public ResourceHolder getMaximum(int count);
	
	/**
	 * Retrieve a resource holder, using the given random number generator to determine
	 * the amount of the given resource to return.
	 * 
	 * @param rnd - random number generator to use.
	 * @param count - resource amount multiplier.
	 * @return A resource holder.
	 */
	public ResourceHolder getResource(Random rnd, int count);
}
