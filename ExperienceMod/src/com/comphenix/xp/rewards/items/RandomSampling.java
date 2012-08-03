package com.comphenix.xp.rewards.items;

import java.util.List;
import java.util.Random;

import org.apache.commons.lang.NullArgumentException;

/**
 * Utility methods for randomly sampling elements from a list.
 * 
 * @author Kristian
 */
public class RandomSampling {

	// Thread local random variable
    private static final ThreadLocal<Random> safeRandom = new ThreadLocal<Random>() {
        @Override 
        protected Random initialValue() {
            return new Random();
        }
	};
	
	/**
	 * Retrieves a thread-local random number generator.
	 * @return Thread-local random number generator.
	 */
	public static Random getThreadRandom() {
		return safeRandom.get();
	}
	
	/**
	 * Samples a random element from the given list with a uniform distribution of probabilities.
	 * <p>
	 * This method is thread safe.
	 * 
	 * @param list - list to sample from.
	 * @return The random element.
	 * @throws NullArgumentException - if the list is null.
	 * @throws IndexOutOfBoundsException - if the list is empty.
	 */
	public static <T> T getRandomElement(List<T> list) {
		if (list == null)
			throw new NullArgumentException("list");
		if (list.size() == 0)
			throw new IndexOutOfBoundsException("Cannot get a random element from an empty list.");

		return getRandomElement(list, null);
	}
	
	/**
	 * Samples a random element from the given list with a uniform distribution of probabilities.
	 * <p>
	 * This method is thread safe.
	 * 
	 * @param list - list to sample from.
	 * @param defaultValue - value to return if the list is empty or null.
	 * @return The random element.
	 */
	public static <T> T getRandomElement(List<T> list, T defaultValue) {
		
		if (list == null || list.size() == 0)
			return defaultValue;
		else
			return list.get(safeRandom.get().nextInt(list.size()));
	}
}
