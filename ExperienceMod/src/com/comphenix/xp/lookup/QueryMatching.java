package com.comphenix.xp.lookup;

import java.util.List;

public class QueryMatching {

	/**
	 * Matches two queries safely. 
	 * <p>
	 * If a query is null, it will only match if the other query is also null.
	 * @param a - first query to test.
	 * @param b - second query to test.
	 * @return TRUE if they are both null OR matches, FALSE otherwise.
	 */
	public static boolean match(Query a, Query b) {
		
		// Check for null queries
		if (a == null || b == null)
			return a == null && b == null;
		
		return a.match(b);
	}
	
	/**
	 * Determines if the two sets intersects.
	 * <p>
	 * Note that the empty set is represented with [null] (list with a null element). 
	 * The universe is represented with an empty list: [].
	 * @param first - first query.
	 * @param second - second query.
	 * @return TRUE if the two queries intersects, FALSE otherwise.
	 */
	public static <TParam> boolean matchParameter(List<TParam> first, List<TParam> second) {
	
		// Empty list represents the universe. They always intersect.
		if (emptyList(first) || emptyList(second))
			return true;

		// Make sure we match against the biggest set
		if (first.size() < second.size())
			return matchParameter(second, second);
		
		// See if we have at least one item in common
		for (TParam item : second) {
			if (first.contains(item))
				return true;
		}
		
		// No intersection
		return false;
	}
	
	/**
	 * Determines if a list is empty or null.
	 * @param list - list of test.
	 * @return TRUE if the list is null or empty, FALSE otherwise.
	 */
	private static <TParam> boolean emptyList(List<TParam> list) {
		return list == null || list.size() == 0;
	}
}
