package com.comphenix.xp;

import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Allows users to modify the amount of experience that is needed to level up a level.
 * 
 * @author Kristian
 */
public class LevelingRate {

	private enum State {
		OPEN,
		CLOSE,
		BOTH
	}
	
	private static class EndPoint {
		
		// Whether or not the end-point is opening a range, closing a range or both.
		public State state;
		
		// The amount of experience in this level range.
		public int experience;

		public EndPoint(State state, int experience) {
			this.state = state;
			this.experience = experience;
		}	
	}
	
	// To quickly look up ranges we'll index them by endpoints
	private NavigableMap<Integer, EndPoint> bounds = new TreeMap<Integer, EndPoint>();
	
	/**
	 * Removes every interval that intersects with the given range.
	 * @param lowerBound - lowest value to remove.
	 * @param upperBound - highest value to remove.
	 */
	public void remove(int lowerBound, int upperBound) {
		SortedMap<Integer, EndPoint> range = bounds.subMap(lowerBound, true, upperBound, true);
		Integer first = range.firstKey();
		Integer last = range.lastKey();
		
		if (range.get(first).state == State.CLOSE) {
			// Remove the previous element too. A close end-point must be preceded by an OPEN end-point.
			removeIfNonNull(bounds.floorKey(first));
		}
		
		if (range.get(last).state == State.OPEN) {
			// Get the closing element too.
			removeIfNonNull(bounds.ceilingKey(last));
		}
		
		// Remove the range as well
		range.clear();
	}
	
	// Helper
	private void removeIfNonNull(Object key) {
		if (key != null) {
			bounds.remove(key);
		}
	}
	
	// Adds a given end point
	private void addEndPoint(int key, int experience, State state) {
		if (bounds.containsKey(key)) {
			bounds.get(key).state = State.BOTH;
		} else {
			bounds.put(key, new EndPoint(state, experience));
		}
	}
	
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
	public void put(int lowerBound, int upperBound, int experience) {
		
		// While we don't permit overlapping intervals, we'll still allow overwriting existing intervals. 
		// First, remove everything within the given bounds:
		bounds.subMap(lowerBound, true, upperBound, true).clear();
		
		// Then see if there's anything we need to fix
		Integer left = bounds.floorKey(lowerBound);
		Integer right = bounds.ceilingKey(upperBound);
		
		// Close the range to the left
		if (left != null && bounds.get(left).state == State.OPEN) {
			addEndPoint(lowerBound - 1, bounds.get(left).experience, State.CLOSE);
		}
		
		// And to the right
		if (right != null && bounds.get(right).state == State.CLOSE) {
			addEndPoint(upperBound + 1, bounds.get(right).experience, State.OPEN);
		}
		
		// OK. Add the end points now
		addEndPoint(lowerBound, experience, State.OPEN);
		addEndPoint(upperBound, experience, State.CLOSE);
	}
	
	/**
	 * Determines if the given level has a specified amount of experience.
	 * @param level - level to check.
	 * @return TRUE if the given level has a custom amount of experience, FALSE otherwise.
	 */
	public boolean containsLevel(int level) {
		return get(level) != null;
	}
	
	/**
	 * Retrieves the amount of experience for a given level, or NULL if not found.
	 * @param level - the level to read for.
	 * @return The correct amount of experience, or NULL if nothing was recorded.
	 */
	public Integer get(int level) {
		
		EndPoint ends = bounds.get(level);
		
		if (ends != null) {
			// This is a piece of cake
			return ends.experience;
		} else {
			
			// We need to determine if the point intersects with a range
			Integer left = bounds.floorKey(level);
			
			// We only need to check to the left
			if (left != null && bounds.get(left).state == State.OPEN) {
				return bounds.get(left).experience;
			} else {
				return null;
			}
		}
	}
}
