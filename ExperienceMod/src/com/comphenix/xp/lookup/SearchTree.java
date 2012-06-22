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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class SearchTree<TKey, TValue> {

	protected HashMap<Integer, TValue> flatten = new HashMap<Integer, TValue>();
	protected HashMap<Integer, Integer> paramCount = new HashMap<Integer, Integer>();
	protected int currentID;
	
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
	
	private Integer getIndex(TKey element) {
		
		Set<Integer> candidates = getFromParameters(element);
		Integer bestID = null;
		Integer bestCount = -1;
		
		// No result? Better return null.
		if (candidates == null || candidates.size() == 0)
			return null;
		
		// Return the most specified element
		for (Integer candidate : candidates) {
			Integer count = paramCount.get(candidate);
			
			// If queries are equally specified, the most recently added query will take precedence.
			if (bestCount.compareTo(count) <= 0) {
				bestID = candidate;
				bestCount = count;
			}
		}
		
		return bestID;
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
	
	protected static class Parameter<TParam> {
		
		private HashMap<TParam, Set<Integer>> reverseLookup = new HashMap<TParam, Set<Integer>>();

		public int size() {
			return reverseLookup.size();
		}
		
		public void put(List<TParam> params, Integer id) {

			// Associate every parameter
			for (TParam param : params) {
				putSingle(param, id);
			}
		}

		private void putSingle(TParam param, Integer id) {
			
			Set<Integer> list = reverseLookup.get(param);
			
			// Initialize the list
			if (list == null) {
				list = new HashSet<Integer>();
				reverseLookup.put(param, list);
			}
			
			// Store the direct and reverse lookup
			if (!list.add(id))
				throw new IllegalArgumentException(
						String.format("Duplicate parameter %s at index %i", id, param));
		}
		
		public Set<Integer> getCopy(List<TParam> params) {
			
			Set<Integer> copy = new HashSet<Integer>();
			
			// Union of all parameter sets
			for (TParam param : params) {
				Set<Integer> result = reverseLookup.get(param);
				
				if (result != null)
					copy.addAll(result);
			}
			
			return copy;
		}
		
		public void retain(Set<Integer> current, List<TParam> params) {
			
			// Save some time
			if (current.size() == 0)
				return;
			
			// Set subtraction
			Set<TParam> blacklist = new HashSet<TParam>(reverseLookup.keySet());
			blacklist.removeAll(params);
			
			// Queries WITH this parameter will not be removed in this phase
			Set<Integer> whitelist = getWhitelist(current, params);
			
			// Remove everything but the items with the given parameter
			for (TParam key : blacklist) {
				current.removeAll(reverseLookup.get(key));

				if (current.size() == 0) {
					break;
				}
			}
			
			// Add back the whitelist
			if (whitelist.size() > 0) {
				current.addAll(whitelist);
			}
				
			// Optimize idea: Store the negative (all elements NOT in the given lookup), 
			//                instead of a loop.
		}
		
		private Set<Integer> getWhitelist(Set<Integer> current, List<TParam> params) {
			
			Set<Integer> whitelist = new HashSet<Integer>();
			
			// Add every query that qualify
			for (TParam param : params) {
				Set<Integer> result = reverseLookup.get(param);
				
				if (result != null)
					whitelist.addAll(result);
			}
			
			// But remove those previously eliminated
			whitelist.retainAll(current);
			return whitelist;
		}
	}
}
