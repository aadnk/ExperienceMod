package com.comphenix.xp.lookup;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Parameter<TParam> {
	
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
	
	/**
	 * Insert every parameter key and its corresponding ID list from the given parameter.
	 * @param other Given parameter.
	 * @param offsetID The amount to increment every ID in the given parameter.
	 */
	public void putAll(Parameter<TParam> other, Integer offsetID) {
		
		// Associate every parameter
		for (TParam param : other.reverseLookup.keySet()) {
			
			Set<Integer> list = reverseLookup.get(param);
			
			// Initialize the list
			if (list == null) {
				list = new HashSet<Integer>();
				reverseLookup.put(param, list);
			}
			
			// Add every ID
			for (Integer id : other.reverseLookup.get(param)) {
				list.add(offsetID + id);
			}
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
	
	/**
	 * Retrieves every stored parameter.
	 * @return Every stored parameter.
	 */
	public Collection<TParam> getKeys() {
		return reverseLookup.keySet();
	}
}