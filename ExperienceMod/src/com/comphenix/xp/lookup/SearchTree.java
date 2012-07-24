/*
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

package com.comphenix.xp.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public abstract class SearchTree<TKey, TValue> {

	protected Map<Integer, TValue> flatten = new ConcurrentHashMap<Integer, TValue>();
	protected Map<Integer, Integer> paramCount = new ConcurrentHashMap<Integer, Integer>();
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
	
	public void putAll(SearchTree<TKey, TValue> other) {

		int offset = currentID;
		int highest = offset;
		
		// Insert everything from flatten
		for (Map.Entry<Integer, TValue> entry : other.flatten.entrySet()) {
			flatten.put(entry.getKey() + offset, entry.getValue());
			highest = Math.max(highest, entry.getKey() + offset);
		}
		
		// And from parameter count
		for (Map.Entry<Integer, Integer> entry : other.paramCount.entrySet())
			paramCount.put(entry.getKey() + offset, entry.getValue());
		
		// Make sure the parameters are updated too
		putAllParameters(other, offset);
		currentID = highest + 1;
	}
	
	public TValue get(TKey element) {
		
		Integer id = getID(element);
		
		return get(id);
	}
	
	public TValue get(Integer id) {
		
		if (id != null)
			return flatten.get(id);
		else
			return null;
	}
	
	public List<TValue> getAllRanked(TKey element) {
		
		// YES! LINQ, only slightly more painful.
		return Lists.transform(getAllRankedID(element), new Function<Integer, TValue>() {
			public TValue apply(Integer id) {
				return flatten.get(id);
			}
		});
	}
	
	public List<Integer> getAllRankedID(TKey element) {
		
		Set<Integer> candidates = getFromParameters(element);
		List<Integer> indexes = new ArrayList<Integer>(candidates);

		// Filter out nulls after sorting
		Collections.sort(indexes, comparer);
		return Lists.newArrayList(Iterables.filter(indexes, Predicates.notNull()));
	}
	
	private Integer getID(TKey element) {
		
		Set<Integer> candidates = getFromParameters(element);

		// No result? Better return null.
		if (candidates == null || candidates.size() == 0)
			return null;
		
		// Return the most specified element
		return Collections.min(candidates, comparer);
	}
	
	public boolean containsKey(TKey element) {
		return getID(element) != null;
	}
	
	/**
	 * Returns a list of every stored value in this search tree.
	 * @return Every stored value.
	 */
	public Collection<TValue> getValues() {
		return flatten.values();
	}
	
	protected abstract void putAllParameters(SearchTree<TKey, TValue> other, Integer offset);
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
