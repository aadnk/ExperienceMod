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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public abstract class SearchTree<TKey, TValue> {

	/**
	 * Contains an entry in the search tree.
	 * 
	 * @author Kristian
	 */
	protected class SearchEntry {
		public TKey key;
		public TValue value;
		public int paramCount;
		
		// Simple constructor
		public SearchEntry(TKey key, TValue value) {
			this.key = key;
			this.value = value;
		}
		
		public SearchEntry(SearchEntry other) {
			this.key = other.key;
			this.value = other.value;
			this.paramCount = other.paramCount;
		}
	}
	
	protected Map<Integer, SearchEntry> flatten = new HashMap<Integer, SearchEntry>();
		
	protected int currentID;
	protected ValueComparer comparer = new ValueComparer(); 
	
	public Integer put(TKey element, TValue value) {
		
		Integer id = getNextID();
		SearchEntry entry = new SearchEntry(element, value);
		
		// Save value
		flatten.put(id, entry);
		
		// Extract parameters
		entry.paramCount = putFromParameters(element, id);
		return id;
	}
	
	public void putAll(SearchTree<TKey, TValue> other) {

		int offset = currentID;
		int highest = offset;
		
		// Insert everything from flatten
		for (Integer id : other.flatten.keySet()) {
			SearchEntry entry = new SearchEntry(other.flatten.get(id));
			
			flatten.put(id + offset, entry);
			highest = Math.max(highest, id + offset);
		}
		
		// Make sure the parameters are updated too
		putAllParameters(other, offset);
		currentID = highest + 1;
	}
	
	/**
	 * Retrieves an element by a query.
	 * @param element - the query to use.
	 * @return The retrieved element.
	 */
	public TValue get(TKey element) {
		
		Integer id = getID(element);
		
		return get(id);
	}
	
	/**
	 * Retrieves an element by ID.
	 * @param id - the ID of the element to retrieve.
	 * @return The retrieved element.
	 */
	public TValue get(Integer id) {
		
		if (id != null && flatten.containsKey(id))
			return flatten.get(id).value;
		else
			return null;
	}
	
	/**
	 * Retrieves the number of specified parameters by this element.
	 * @param id - the element's ID.
	 * @return The number of parameters.
	 * @throws IllegalArgumentException - if no element by that ID can be found.
	 */
	public int getParamCount(Integer id) {
		SearchEntry entry = flatten.get(id);
		
		if (entry != null)
			return entry.paramCount;
		else
			throw new IllegalArgumentException("Cannot find ID " + id);
	}
	
	public List<TValue> getAllRanked(TKey element) {
		
		// YES! LINQ, only slightly more painful.
		return Lists.transform(getAllRankedID(element), new Function<Integer, TValue>() {
			public TValue apply(Integer id) {
				return get(id);
			}
		});
	}
	
	/**
	 * Retrieves every possible matching action in ID form.
	 * @param element - the query to match with.
	 * @return List of IDs.
	 */
	public List<Integer> getAllRankedID(TKey element) {
		
		Set<Integer> candidates = getFromParameters(element);
		List<Integer> indexes = new ArrayList<Integer>(candidates.size());

		for (Integer candidate : candidates) {
			if (candidate != null) {
				indexes.add(candidate);
			}
		}
		
		// Sort list and return
		Collections.sort(indexes, comparer);
		return indexes;
	}
	
	protected Integer getID(TKey element) {
		
		Set<Integer> candidates = getFromParameters(element);

		// No result? Better return null.
		if (candidates == null || candidates.size() == 0)
			return null;
		
		// Return the most recent element
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
		List<TValue> values = new ArrayList<TValue>();
		
		// Simple enumeration
		for (SearchEntry entry : flatten.values()) {
			values.add(entry.value);
		}
		
		return values;
	}
	
	/**
	 * Returns a list of every stored key in this search tree.
	 * @return Every stored value.
	 */
	public Collection<TKey> getKeys() {
		List<TKey> values = new ArrayList<TKey>();
		
		for (SearchEntry entry : flatten.values()) {
			values.add(entry.key);
		}
		
		return values;
	}
	
	protected abstract void putAllParameters(SearchTree<TKey, TValue> other, Integer offset);
	protected abstract Integer putFromParameters(TKey source, Integer id);
	protected abstract Set<Integer> getFromParameters(TKey source);
	
	private int getNextID() {
		return currentID++;
	}
	
	/**
	 * Compares values (referenced by ID) by priority. The newest elements are put first.
	 */
	protected class ValueComparer implements Comparator<Integer> {

		@Override
		public int compare(Integer a, Integer b) {

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
