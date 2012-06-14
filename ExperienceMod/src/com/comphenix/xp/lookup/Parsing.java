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

public class Parsing {
	
	public static String[] getParameterArray(String text) {
		
		String[] components = text.split("\\||:");
		
		// Clean up
		for (int i = 0; i < components.length; i++) 
			components[i] = components[i].trim().toLowerCase();
		return components;
	}
	
	public static Boolean hasElementPrefix(String[] values, int startIndex, String element) {
		
		// See if this element exists
		for (int i = startIndex; i < values.length; i++) {
			boolean value = !values[i].startsWith("!"); // Negative prefix
	
			if (element.startsWith(values[i], value ? 0 : 1))
				return value;
		}
		
		// No preferense either way
		return null;
	}
	
    public static String getEnumName(String text) {
		String filtered = text.toUpperCase();
		return filtered.replaceAll("\\s+", "_").replaceAll("\\W", "");
	}
    
	public static String formatBoolean(String booleanName, Boolean value) {
		// Mirror the query syntax
		if (value == null)
			return "";
		else 
			return value ? booleanName : "!" + booleanName;
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
	public static Integer tryParse(String[] values, int index) {
		return tryParse(values, index, null);
	}
	
	public static Integer tryParse(String value) {
		return tryParse(new String[] { value }, 0);
	}
	
	// Attempt to parse integer
	public static Integer tryParse(String[] values, int index, Integer defaultValue) {
		try { 
			if (index < values.length && !isNullOrIgnoreable(values[index]))
				return Integer.parseInt(values[index]);
			else
				return defaultValue;
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
}
