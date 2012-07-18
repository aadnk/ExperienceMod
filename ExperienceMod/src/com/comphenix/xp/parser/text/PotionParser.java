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
import org.bukkit.Material;
import org.bukkit.potion.PotionType;

import com.comphenix.xp.lookup.PotionQuery;
import com.comphenix.xp.parser.TextParser;
import com.comphenix.xp.parser.ParsingException;
import com.comphenix.xp.parser.primitives.BooleanParser;
import com.comphenix.xp.parser.primitives.IntegerParser;

public class PotionParser extends TextParser<PotionQuery> {

	private ParameterParser<Integer> itemNameParser = new ParameterParser<Integer>(new ItemNameParser());
	private ParameterParser<PotionType> potionTypeParser = new ParameterParser<PotionType>(new PotionTypeParser());
	private ParameterParser<Integer> tierParser = new ParameterParser<Integer>(new IntegerParser());
	
	private BooleanParser extendedParser = new BooleanParser("extended");
	private BooleanParser splashParser = new BooleanParser("splash");

	// Special potion parser
	@Override
	public PotionQuery parse(String text) throws ParsingException {

		Queue<String> tokens = getParameterQueue(text);
		
		ParsingException reason = null;
		List<Integer> items = null;
		List<PotionType> types = null;
		List<Integer> tiers = null;
		
		try {
			items = itemNameParser.parse(tokens);
			types = potionTypeParser.parse(tokens);
			tiers = tierParser.parse(tokens);
			
		} catch (ParsingException ex) {
			// Wait, don't give up yet.
			reason = ex;
		}
	
		// Possibly a double check
		if (items.isEmpty() || !items.contains(Material.POTION.getId()))
			throw new ParsingException("Can only create potion queries from potion rules.");
		
		// Scan all unused parameters for these options first
		List<Boolean> extended = extendedParser.parseAny(tokens);
		List<Boolean> splash = splashParser.parseAny(tokens);
		
		Integer maxLevel = getMaxLevel(types);
		
		// Just assume level two is the highest
		if (maxLevel == null)
			maxLevel = 2;
		
		// Check tiers
		for (Integer tier : tiers) {
			if (tier > maxLevel) {
				throw ParsingException.fromFormat(
						"Potion level %d is too high.", tier);
			} else if (tier < 1) {
				throw ParsingException.fromFormat(
						"Potion level %d is too low.", tier);
			}
		}
		
		// If there are some tokens left, a problem occured
		if (!tokens.isEmpty()) {
			
			// Let the user know about the reason too
			if (reason != null)
				throw reason;
			else
				throw ParsingException.fromFormat("Unknown item tokens: ", StringUtils.join(tokens, ", "));
		}
		
		// Create the query
		return new PotionQuery(types, tiers, extended, splash);
	}

	private Integer getMaxLevel(List<PotionType> types) {
		
		Integer best = null;
		
		for (PotionType type : types) {
			if (best == null || best < type.getMaxLevel()) {
				best = type.getMaxLevel();
			}
		}
		
		return best;
	}
}
