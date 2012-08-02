package com.comphenix.xp.rewards;

import java.util.Random;

/**
 * Represents a resource holder factory.
 * 
 * @author Kristian
 */
public interface ResourceFactory {

	/**
	 * Retrieve a resource holder, using the given random number generator to determine
	 * the amount of the given resource to return.
	 * 
	 * @param rnd - random number generator to use.
	 * @return A resource holder.
	 */
	public ResourceHolder getResource(Random rnd);
	
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
