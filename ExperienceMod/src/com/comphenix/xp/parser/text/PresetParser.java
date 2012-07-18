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

import java.util.List;
import java.util.Queue;

import org.apache.commons.lang.StringUtils;

import com.comphenix.xp.lookup.PresetQuery;
import com.comphenix.xp.parser.TextParser;
import com.comphenix.xp.parser.ParsingException;
import com.comphenix.xp.parser.primitives.StringParser;

public class PresetParser extends TextParser<PresetQuery> {

	private ParameterParser<String> textParsing = new ParameterParser<String>(new StringParser());
	
	@Override
	public PresetQuery parse(String text) throws ParsingException {

		if (text.length() == 0)
			// Empty names are not legal in YAML, so this shouldn't be possible 
			throw new IllegalArgumentException("Key must have some characters.");
		
		Queue<String> tokens = getParameterQueue(text);
		
		List<String> presetNames = textParsing.parse(tokens);
		List<String> worldNames = textParsing.parse(tokens);
		
		if (!tokens.isEmpty())
			throw ParsingException.fromFormat("Unknown preset tokens: ", 
					StringUtils.join(tokens, ", "));

		return new PresetQuery(presetNames, worldNames);
	}
}
