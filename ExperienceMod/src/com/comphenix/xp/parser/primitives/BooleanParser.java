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

package com.comphenix.xp.parser.primitives;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import com.comphenix.xp.parser.TextParser;
import com.comphenix.xp.parser.ParsingException;

public class BooleanParser extends TextParser<List<Boolean>> {

	private static List<Boolean> emptyList = new ArrayList<Boolean>();
	
	private String parameterName;

	public BooleanParser(String parameterName) {
		this.parameterName = parameterName;
	}
	
	public String getParameterName() {
		return parameterName;
	}

	/**
	 * Parses the given parameter as a boolean. 
	 * @param text - parameter to parse.
	 * @return Boolean value if parsing succeeded, or NULL otherwise.
	 */
	@Override
	public List<Boolean> parse(String text) throws ParsingException {
		
		if (text == null)
			return null;
		
		String[] elements = text.split(",");
		List<Boolean> values = new ArrayList<Boolean>();
		
		for (String element : elements) {
			
			boolean currentValue = !element.startsWith("!"); // Negative prefix
			String currentName = element.substring(currentValue ? 0 : 1);
			
			// Permit prefixing 
			if (parameterName.startsWith(currentName))  {
				
				// Be careful to handle the case !parameterName, parameterName
				if (!values.contains(currentValue))
					values.add(currentValue);
				else
					throw ParsingException.fromFormat("Duplicate value detected: %", element);
				
			} else  {
				return null; 
			}
		}

		// Null indicates any value
		if (values.isEmpty())
			return null;
		else
			return values;
	}
	
	/**
	 * Transforms and returns the first non-null element from the left into an object. That element is removed.
	 * @param tokens - queue of items.
	 * @return List containing the removed object, OR an empty list if no object was removed.
	 */
	public List<Boolean> parseAny(Queue<String> tokens) throws ParsingException {

		String toRemove = null;
		List<Boolean> result = null;
				
		for (String current : tokens ){
			result = parse(current);
			
			// We have a solution
			if (result != null) {
				toRemove = current;
				break;
			}
		}
		
		// Return and remove token
	    if (result != null) {
	    	tokens.remove(toRemove);
	    	
	    	if (result.size() == 1)
	    		return result;
	    	else
	    		// Match true and false at the same time
	    		return emptyList;
	    	
	    } else {
	    	
	    	// Speed things up a bit
	    	return emptyList;
	    }
	}
}
