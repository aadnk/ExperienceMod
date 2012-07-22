package com.comphenix.xp;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.comphenix.xp.parser.Utility;

public class ActionTypes {
	
	// Default actions
	public static String BLOCK = "BLOCK";
	public static String BONUS = "BONUS";
	public static String PLACE = "PLACE";
	public static String SMELTING = "SMELTING";
	public static String CRAFTING = "CRAFTING";
	public static String BREWING = "BREWING";
	
	// Quick lookup of action types
	private Map<String, Integer> lookup = new HashMap<String, Integer>();
	
	// Current unique ID
	private int currentID;
	
	/**
	 * Retrieves the default action registry.
	 * @return A default action registry.
	 */
	public static ActionTypes Default() {

		ActionTypes types = new ActionTypes();
		
		types.register(BLOCK, "BLOCK_SOURCE");
		types.register(BONUS, "BONUS_SOURCE");
		types.register(PLACE, "PLACING", "PLACING_RESULT");
		types.register(SMELTING, "SMELTING_RESULT");
		types.register(CRAFTING, "CRAFTING_RESULT");
		types.register(BREWING, "BREWING_RESULT");
		return types;
	}
	
	/**
	 * Retrieves a collection of every registered action type.
	 * @return Collection of every registered action type.
	 */
	public Collection<Integer> getTypes() {
		return lookup.values();
	}
	
	/**
	 * Retrieves a registered action type by name, or NULL if no such type could be found.
	 * @param name - name of the action type to find.
	 * @return Unique ID of the action type, or NULL if not found.
	 */
	public Integer getType(String name) {
		return lookup.get(Utility.getEnumName(name));
	}
	
	/**
	 * Registers an action type with a list of names. Names should use the ENUM naming convention.
	 * @param names - names of the given action type.
	 * @return Unique ID of the registered action type.
	 */
	public int register(String... names) {
		
		if (names == null || names.length == 0)
			throw new IllegalArgumentException("Names cannot be empty or null.");
		
		// Get and increment the next ID
		int id = currentID++;
		
		for (String name : names)
			lookup.put(name, id);
		return id;
	}
	
	/**
	 * Registers additional names for a given action type. Names should use the ENUM naming convention.
	 * @param id - unique action type id.
	 * @param names - additional list of synonyms/names for this action.
	 */
	public void register(int id, Iterable<String> names) {
		
		for (String name : names) {
			lookup.put(name, id);
		}
	}
	
	/**
	 * Unregisters a specified action type.
	 * @param id - id of the action type to remove.
	 */
	public void unregister(int id) {
		
		// Remove all elements with this ID
		for (Iterator<Entry<String, Integer>> it = lookup.entrySet().iterator(); it.hasNext(); ) {
			if (it.next().getValue() == id)
				it.remove();
		}
	}
}
