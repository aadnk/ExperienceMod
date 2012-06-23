package com.comphenix.xp.lookup;

/**
 *  ExperienceMod - Bukkit server plugin for modifying the experience system in Minecraft.
 *  Copyright (C) 2012 Kristian S. Stangeland
 *
 *  This program is free software; you can redistribute it and/or modify it under the terms of the 
 *  GNU General Public License as published by the Free Software Foundation; either version 2 of 
 *  the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with this program; 
 *  if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 
 *  02111-1307 USA
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public abstract class SearchTree<TKey, TValue> {

	protected HashMap<Integer, TValue> flatten = new HashMap<Integer, TValue>();
	protected HashMap<Integer, Integer> paramCount = new HashMap<Integer, Integer>();
	protected int currentID;
	
	protected ValueComparer comparer = new ValueComparer(); 
	
	public Integer put(TKey element, TValue value) {
		
		Integer id = getNextID();
		
		// Save value
		flatten.put(id, value);
		
		// Extract parameters
		Integer count = putFromParameters(element, id);
		
		paramCount.put(id, count);
		return id;
	}
	
	public TValue get(TKey element) {
		
		Integer index = getIndex(element);
		
		if (index != null)
			return flatten.get(index);
		else
			return null;
	}
	
	public List<TValue> getAllRanked(TKey element) {
		
		Set<Integer> candidates = getFromParameters(element);
		List<Integer> indexes = new ArrayList<Integer>(candidates);
		List<TValue> values = new ArrayList<TValue>();
		
		// Sort indexes by priority
		Collections.sort(indexes, comparer);
		
		// Get values
		for (Integer id : indexes) {
			if (id != null) {
				values.add(flatten.get(id));
			}
		}
		
		return values;
	}
	
	private Integer getIndex(TKey element) {
		
		Set<Integer> candidates = getFromParameters(element);

		// No result? Better return null.
		if (candidates == null || candidates.size() == 0)
			return null;
		
		// Return the most specified element
		return Collections.min(candidates, comparer);
	}
	
	public boolean containsKey(TKey element) {
		return getIndex(element) != null;
	}
	
	/**
	 * Returns a list of every stored value in this search tree.
	 * @return Every stored value.
	 */
	public Collection<TValue> getValues() {
		return flatten.values();
	}
	
	protected abstract Integer putFromParameters(TKey source, Integer id);
	protected abstract Set<Integer> getFromParameters(TKey source);
	
	private int getNextID() {
		return currentID++;
	}
	
	/**
	 * Compares values (referenced by ID) by priority. The the most specified, 
	 * and if identical, newest value is put at the beginning.
	 */
	protected class ValueComparer implements Comparator<Integer> {

		@Override
		public int compare(Integer a, Integer b) {

			Integer countA = paramCount.get(a);
			Integer countB = paramCount.get(b);
			int comparison = compareObjects(countB, countA, false);
			
			// Compare mainly by specificity
			if (comparison != 0)
				return comparison;
			else
				// Higher before lower
				return compareObjects(b, a, false);
		}
		
		// Taken from Apache Commons 2.6  (ObjectUtils.compare)
		public <T extends Comparable<T>> int compareObjects(T c1, T c2, boolean nullGreater) {
            if (c1 == c2) {
                return 0;
            } else if (c1 == null) {
                return (nullGreater ? 1 : -1);
            } else if (c2 == null) {
                return (nullGreater ? -1 : 1);
            }
            return c1.compareTo(c2);
        }
	}
}
