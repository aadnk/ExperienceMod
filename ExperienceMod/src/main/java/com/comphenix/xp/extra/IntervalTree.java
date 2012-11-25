package com.comphenix.xp.extra;

import java.util.HashSet;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang.NotImplementedException;

import com.google.common.collect.Range;
import com.google.common.collect.Ranges;

/**
 * Represents a generic store of intervals and associated values. No two intervals
 * can overlap in this representation.
 * 
 * @author Kristian
 *
 * @param <TKey> - type of the key. Must implement Comparable.
 * @param <TValue> - type of the value to associate.
 */
public abstract class IntervalTree<TKey extends Comparable<TKey>, TValue> {
	
	protected enum State {
		OPEN,
		CLOSE,
		BOTH
	}
	
	/**
	 * Represents a range and a value in this interval tree.
	 */
	public class Entry implements Map.Entry<Range<TKey>, TValue> {
		private Range<TKey> key;
		private TValue value;
		
		public Entry(Range<TKey> key, TValue value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public Range<TKey> getKey() {
			return key;
		}

		@Override
		public TValue getValue() {
			return value;
		}

		@Override
		public TValue setValue(TValue value) {
			throw new NotImplementedException();
		}
	}
	
	/**
	 * Represents a single end point (open, close or both) of a range.
	 */
	protected class EndPoint {
		
		// Whether or not the end-point is opening a range, closing a range or both.
		public State state;
		
		// The value this range contains
		public TValue value;

		public EndPoint(State state, TValue value) {
			this.state = state;
			this.value = value;
		}	
	}
	
	// To quickly look up ranges we'll index them by endpoints
	protected NavigableMap<TKey, EndPoint> bounds = new TreeMap<TKey, EndPoint>();
	
	/**
	 * Removes every interval that intersects with the given range.
	 * @param lowerBound - lowest value to remove.
	 * @param upperBound - highest value to remove.
	 */
	public void remove(TKey lowerBound, TKey upperBound) {
		SortedMap<TKey, EndPoint> range = bounds.subMap(lowerBound, true, upperBound, true);
		TKey first = range.firstKey();
		TKey last = range.lastKey();
		
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
	protected void removeIfNonNull(Object key) {
		if (key != null) {
			bounds.remove(key);
		}
	}
	
	// Adds a given end point
	protected void addEndPoint(TKey key, TValue value, State state) {
		if (bounds.containsKey(key)) {
			bounds.get(key).state = State.BOTH;
		} else {
			bounds.put(key, new EndPoint(state, value));
		}
	}
	
	/**
	 * Associates a given interval of keys with a certain value. Any previous
	 * association will be overwritten in the given interval. 
	 * <p>
	 * Overlapping intervals are not permitted. A key can only be associated with a single value.
	 * 
	 * @param lowerBound - the minimum key (inclusive).
	 * @param upperBound - the maximum key (inclusive).
	 * @param value - the value, or NULL to reset this range.
	 */
	public void put(TKey lowerBound, TKey upperBound, TValue value) {
		
		// While we don't permit overlapping intervals, we'll still allow overwriting existing intervals. 
		// First, remove everything within the given bounds:
		bounds.subMap(lowerBound, true, upperBound, true).clear();
		
		// Then see if there's anything we need to fix
		TKey left = bounds.floorKey(lowerBound);
		TKey right = bounds.ceilingKey(upperBound);
		
		// Close the range to the left
		if (left != null && bounds.get(left).state == State.OPEN) {
			addEndPoint(decrementKey(lowerBound), bounds.get(left).value, State.CLOSE);
		}
		
		// And to the right
		if (right != null && bounds.get(right).state == State.CLOSE) {
			addEndPoint(incrementKey(upperBound), bounds.get(right).value, State.OPEN);
		}
		
		// OK. Add the end points now
		if (value != null) {
			addEndPoint(lowerBound, value, State.OPEN);
			addEndPoint(upperBound, value, State.CLOSE);
		}
	}
	
	/**
	 * Determines if the given key is within an interval.
	 * @param key - key to check.
	 * @return TRUE if the given key is within an interval in this tree, FALSE otherwise.
	 */
	public boolean containsKey(TKey key) {
		return getEndPoint(key) != null;
	}
	
	/**
	 * Enumerates over every range in this interval tree.
	 * @return Number of ranges.
	 */
	public Set<Entry> entrySet() {

		// Don't mind the Java noise
		Set<Entry> result = new HashSet<Entry>();
		Map.Entry<TKey, EndPoint> last = null;
		
		for (Map.Entry<TKey, EndPoint> entry : bounds.entrySet()) {
			switch (entry.getValue().state) {
			case BOTH:
				result.add(new Entry(Ranges.singleton(entry.getKey()), entry.getValue().value));
				break;
			case CLOSE:
				Range<TKey> range = Ranges.closed(last.getKey(), entry.getKey());
				result.add(new Entry(range, entry.getValue().value));
				break;
			case OPEN:
				// We don't know the full range yet
				last = entry;
				break;
			default:
				throw new IllegalStateException("Illegal open/close state detected.");
			}
		}
		
		return result;
	}
	
	/**
	 * Inserts every range from the given tree into the current tree.
	 * @param other - the other tree to read from.
	 */
	public void putAll(IntervalTree<TKey, TValue> other) {
		// Naively copy every range.
		for (Entry entry : other.entrySet()) {
			put(entry.key.lowerEndpoint(), entry.key.upperEndpoint(), entry.value);
		}
	}
	
	/**
	 * Retrieves the value of the range that matches the given key, or NULL if nothing was found.
	 * @param key - the level to read for.
	 * @return The correct amount of experience, or NULL if nothing was recorded.
	 */
	public TValue get(TKey key) {
		EndPoint point = getEndPoint(key);

		if (point != null)
			return point.value;
		else
			return null;
	}
	
	/**
	 * Get the end-point composite associated with this key.
	 * @param key - key to search for.
	 * @return The end point found, or NULL.
	 */
	protected EndPoint getEndPoint(TKey key) {
		EndPoint ends = bounds.get(key);
		
		if (ends != null) {
			// This is a piece of cake
			return ends;
		} else {
			
			// We need to determine if the point intersects with a range
			TKey left = bounds.floorKey(key);
			
			// We only need to check to the left
			if (left != null && bounds.get(left).state == State.OPEN) {
				return bounds.get(left);
			} else {
				return null;
			}
		}
	}
	
	// Helpers for decrementing or incrementing key values
	protected abstract TKey decrementKey(TKey key);
	protected abstract TKey incrementKey(TKey key);
}
