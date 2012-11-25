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

package com.comphenix.xp.parser.text;

import java.util.ArrayList;
import java.util.List;

import com.comphenix.xp.parser.TextParser;
import com.comphenix.xp.parser.ParsingException;
import com.comphenix.xp.parser.Utility;

/**
 * Reads a comma-delimited list of parsable values.
 * 
 * @author Kristian
 * @param <TItem> Type of each value.
 */
public class ParameterParser<TItem> extends TextParser<List<TItem>>{

	private static final String VALUE_DIVIDER = ",";
	private TextParser<TItem> elementParser;

	public ParameterParser(TextParser<TItem> elementParser) {
		this.elementParser = elementParser;
	}

	@Override
	public List<TItem> parse(String text) throws ParsingException {

		List<TItem> elements = new ArrayList<TItem>();
		
		// First things first. Is this an empty sequence?
		if (Utility.isNullOrIgnoreable(text))
			// If so, return an empty list
			return elements; 
		
		String[] tokens = text.split(VALUE_DIVIDER);
		
		// Now the interesting thing happens
		for (String token : tokens) {
			
			// Check validity and so on
			if (Utility.isNullOrIgnoreable(token))
				throw ParsingException.fromFormat(
						"Universal matcher (%s) cannot be part of a list of values.", token);
			
			// Exceptions will bubble up the chain
			elements.add(elementParser.parse(token.trim()));
		}
		
		return elements;
	}
	
	public List<TItem> parseExact(String text) throws ParsingException {
		List<TItem> result = parse(text);
		
		// Represent nothing with NULL
		if (result.size() == 0)
			result.add(null);
		
		return result;
	}
	
	public TextParser<TItem> getElementParser() {
		return elementParser;
	}
}
