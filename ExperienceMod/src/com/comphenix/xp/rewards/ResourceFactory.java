package com.comphenix.xp.rewards;

import java.util.Collection;
import java.util.Random;

import com.comphenix.xp.expressions.NamedParameter;
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
	 * @param params - named parameters that may be used to compute the amount of resources.
	 * @param rnd - random number generator to use.
	 * @return A resource holder.
	 */
	public ResourceHolder getResource(Collection<NamedParameter> params, Random rnd);
	
	/**
	 * Retrieve a resource holder, using the given random number generator to determine
	 * the amount of the given resource to return.
	 * 
	 * @param params - named parameters that may be used to compute the amount of resources.
	 * @param rnd - random number generator to use.
	 * @param count - resource amount multiplier.
	 * @return A resource holder.
	 */
	public ResourceHolder getResource(Collection<NamedParameter> params, Random rnd, int count);
	
	/**
	 * Calculates the minimum amount of resources that may be awarded.
	 * @param params - named parameters that may be used to compute the amount of resources.
	 * @param count - resource amount multiplier.
	 * @return A resource holder.
	 */
	public ResourceHolder getMinimum(Collection<NamedParameter> params, int count);
	
	/**
	 * Calculates the maximum amount of resources that may be awarded.
	 * @param params - named parameters that may be used to compute the amount of resources.
	 * @param count - resource amount multiplier.
	 * @return A resource holder.
	 */
	public ResourceHolder getMaximum(Collection<NamedParameter> params, int count);
}
