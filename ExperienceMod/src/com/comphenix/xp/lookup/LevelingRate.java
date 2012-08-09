package com.comphenix.xp.lookup;

import com.comphenix.xp.extra.IntervalTree;

import de.congrace.exp4j.Calculable;

/**
 * Allows users to modify the amount of experience that is needed to level up a level.
 * 
 * @author Kristian
 */
public class LevelingRate extends IntervalTree<Integer, Integer> {

	// Store the expressions too
	protected IntervalTree<Integer, Calculable> expressions = new IntervalTree<Integer, Calculable>() {
		protected Integer decrementKey(Integer key) { return key - 1; }
		protected Integer incrementKey(Integer key) { return key + 1; }
	};
	
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
	 * Associates a given interval of levels with a certain amount of experience using an expression. Any previous
	 * association will be overwritten in the given range. 
	 * <p>
	 * Overlapping intervals are not permitted. A key can only be associated with a single value.
	 * 
	 * @param lowerBound - the minimum level (inclusive).
	 * @param upperBound - the maximum level (inclusive).
	 * @param experience - the amount of experience.
	 */
	public void put(Integer lowerBound, Integer upperBound, Calculable experience) {
		// Clear the "integer" cache
		super.put(lowerBound, upperBound, null);
		
		// Insert it into the expression tree
		expressions.put(lowerBound, upperBound, experience);
	}
	
	/**
	 * Retrieves the value of the integer or expression range that contains this level.
	 * @param level - the level to find.
	 * @return The value of that range.
	 */
	@Override
	public Integer get(Integer level) {

		// The integer cache first
		Integer cached = super.get(level);
		
		if (cached == null) {
			
			Calculable computed = expressions.get(level);
			
			if (computed != null) {
				
				int value = (int) computed.calculate(level);
				
				// Cache this result and return
				super.put(level, level, value);
				return value;
				
			} else {
				return null;
			}
			
		} else {
			return cached;
		}
	}
	
	/**
	 * Determines if the given level has a specified amount of experience.
	 * @param level - level to check.
	 * @return TRUE if the given level has a custom amount of experience, FALSE otherwise.
	 */
	@Override
	public boolean containsKey(Integer level) {
		return expressions.containsKey(level) || super.containsKey(level);
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
