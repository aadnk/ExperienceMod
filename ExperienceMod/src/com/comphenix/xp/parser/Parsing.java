package com.comphenix.xp.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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

public class Parsing {
	
	public static Queue<String> getParameterQueue(String text) {
		
		String[] components = text.split("\\||:");
		
		// Clean up
		for (int i = 0; i < components.length; i++) 
			components[i] = components[i].trim().toLowerCase();
		
		return new LinkedList<String>(Arrays.asList(components));
	}
	
	public static Boolean hasElementPrefix(Collection<String> values, String element) {
		
		// See if this element exists
		for (String current : values) {
			boolean value = !current.startsWith("!"); // Negative prefix
	
			if (element.startsWith(current, value ? 0 : 1))
				return value;
		}
		
		// No preferense either way
		return null;
	}
	
    public static String getEnumName(String text) {
    	if (text == null)
    		return "";
    	
		String filtered = text.toUpperCase();
		return filtered.replaceAll("\\s+", "_").replaceAll("\\W", "");
	}
    
	public static String formatBoolean(String booleanName, List<Boolean> value) {
		// Mirror the query syntax
		if (value == null || value.isEmpty())
			return "";
		else 
			return value.get(0) ? booleanName : "!" + booleanName;
	}
	
	public static <T> List<T> getElementList(T value) {

		List<T> items = new ArrayList<T>();
		
		if (value != null)
			items.add(value);
		
		return items;
	}
	
	/**
	 * Determines if a String can be ignored.
	 * @param param String to test.
	 * @return TRUE if the string is null, blank or equal to '?', FALSE otherwise.
	 */
	public static boolean isNullOrIgnoreable(String param) { 
	    return param == null || param.trim().length() == 0 || param.trim().equals("?"); 
	}

	// Attempt to parse integer
	public static Integer tryParse(String input) {
		return tryParse(input, null);
	}
	
	// Attempt to parse integer
	public static Integer tryParse(String input, Integer defaultValue) {
		try { 
			if (!isNullOrIgnoreable(input)) {
				return Integer.parseInt(input);
			} else {
				return defaultValue;
			}
				
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
}
