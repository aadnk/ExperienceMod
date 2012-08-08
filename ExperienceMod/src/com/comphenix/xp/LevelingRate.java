package com.comphenix.xp;

import com.comphenix.xp.extra.IntervalTree;

/**
 * Allows users to modify the amount of experience that is needed to level up a level.
 * 
 * @author Kristian
 */
public class LevelingRate extends IntervalTree<Integer, Integer> {

	/**
	 * Associates a given interval of levels with a certain amount of experience. Any previous
	 * association will be overwritten in the given range. 
	 * <p>
	 * Overlapping intervals are not permitted. A key can only be associated with a single value.
	 * 
	 * @param lowerBound - the minimum level (inclusive).
	 * @param upperBound - the maximum level (inclusive).
	 * @param experience - the amount of experience.
	 */
	@Override
	public void put(Integer lowerBound, Integer upperBound, Integer experience) {
		super.put(lowerBound, upperBound, experience);
	}

	/**
	 * Determines if the given level has a specified amount of experience.
	 * @param level - level to check.
	 * @return TRUE if the given level has a custom amount of experience, FALSE otherwise.
	 */
	@Override
	public boolean containsKey(Integer level) {
		return super.containsKey(level);
	}
	
	@Override
	protected Integer decrementKey(Integer key) {
		return key - 1;
	}

	@Override
	protected Integer incrementKey(Integer key) {
		return key + 1;
	}
}
