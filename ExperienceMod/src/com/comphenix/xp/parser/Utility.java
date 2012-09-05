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

package com.comphenix.xp.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.NullArgumentException;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.MemorySection;

import com.comphenix.xp.reflect.FieldUtils;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Parsing utility functions.
 * 
 * @author Kristian
 */
public class Utility {
	
	@SuppressWarnings("rawtypes")
	private static final List emptyList = new ArrayList();
	
    public static String getEnumName(String text) {
    	if (text == null)
    		return "";
    	
		String filtered = text.toUpperCase();
		return filtered.replaceAll("\\s+", "_").replaceAll("\\W", "");
	}
    
	public static String formatBoolean(String booleanName, List<Boolean> value) {
		// Mirror the query syntax
		if (value == null || value.isEmpty() || value.contains(null))
			return "";
		else 
			return value.get(0) ? booleanName : "!" + booleanName;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> getElementList(T value) {
		
		if (value == null) {
			return emptyList;
			
		} else {
			
			List<T> items = new ArrayList<T>();
			
			items.add(value);
			return items;
		}		
	}
	
	/**
	 * Aggregates every element in a list of lists into a single list.
	 * @param list - list of lists to aggregate.
	 * @return List containing the element of each list in the given list.
	 */
	public static <T> List<T> flatten(List<Set<T>> list) {
		return Lists.newArrayList(Iterables.concat(list));
	}
	
	/**
	 * Converts an array into a the equivalent string array by performing 
	 * toString() on every element.
	 * @param elements - the array to convert.
	 * @return The resulting string array.
	 */
	public static <T> String[] toStringArray(T[] elements) {
		if (elements == null)
			throw new NullArgumentException("elements");
		else if (elements.length == 0)
			return ArrayUtils.EMPTY_STRING_ARRAY;
		
		String[] copy = new String[elements.length];
	
		// Get the string representation
		for (int i = 0; i < copy.length; i++) {
			if (elements[i] == null)
				copy[i] = null;
			else
				copy[i] = elements[i].toString();
		}
		
		return copy;
	}
	
	/**
	 * Creates a shallow copy of the given section that is not referenced by the parent node.
	 * @param section - the section to copy.
	 * @return A copied configuration section.
	 */
	public static ConfigurationSection cloneSection(ConfigurationSection section) {
		
		MemorySection copy = null;
		
		// Is this the root node?
		if (section.getParent() == null) {
			if (!(section instanceof MemorySection)) {
				throw new IllegalArgumentException("Only memory configurations can be cloned.");
			}
			
			Configuration sourceConfig = (Configuration) section;
			Configuration copyConfig = new MemoryConfiguration(sourceConfig.getDefaults());
			
			try {
				// We're very unlikely to clone a root node in the first place, so who cares
				FieldUtils.writeField(copyConfig, "options", sourceConfig.options(), true);
			
			} catch (IllegalAccessException e) {
				throw new RuntimeException("Cannot modify options.", e);
			}
				
		} else {

			String path = Iterables.getLast(
							Splitter.on(section.getRoot().options().pathSeparator()).
					        split(section.getCurrentPath())
					      );
			copy = new ClonableMemorySection(section.getParent(), path);			
		}
		
		// Copy every stored value
		for (String key : section.getKeys(false)) {
			copy.set(key, section.get(key));
		}
		
		return copy;
	}
	
	/**
	 * Determines if a String can be ignored.
	 * @param param String to test.
	 * @return TRUE if the string is null, blank or equal to '?', FALSE otherwise.
	 */
	public static boolean isNullOrIgnoreable(String param) { 
	    return param == null || param.trim().length() == 0 || param.trim().equals("?"); 
	}
	
	private static class ClonableMemorySection extends MemorySection {
		// Used by the cloner to access the protected constructor without using reflection 
		public ClonableMemorySection(ConfigurationSection parent, String path) {
			super(parent, path);
		}
	}
}
