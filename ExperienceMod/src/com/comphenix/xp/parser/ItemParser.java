package com.comphenix.xp.parser;

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

import java.util.List;
import java.util.Queue;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import com.comphenix.xp.lookup.ItemQuery;
import com.comphenix.xp.lookup.PotionQuery;
import com.comphenix.xp.lookup.Query;

public class ItemParser {
	
	private ParameterParser<Integer> itemNameParser = new ParameterParser<Integer>(new ItemNameParser());
	
	private ItemDurabilityParser elementDurability = new ItemDurabilityParser();
	private ParameterParser<Integer> durabilityParser = new ParameterParser<Integer>(elementDurability);
	
	// Our potion parser
	private PotionParser potionParser = new PotionParser();
	
	public Query parseItemQuery(String text) throws ParsingException {
		
		if (text.length() == 0)
			// Empty names are not legal in YAML, so this shouldn't be possible 
			throw new IllegalArgumentException("Key must have some characters.");
		
		Queue<String> tokens = Parsing.getParameterQueue(text);
		
		List<Integer> itemIDs = null;
		List<Integer> durabilities = null;
		
		Integer first = null;
		
		boolean isPotion = false;
		boolean sameCategory = false;
		
		try {
			// Get item IDs
			itemIDs = itemNameParser.parse(tokens);
			first = null;
			
			// Get the first element
			if (!itemIDs.isEmpty()) {
				first = itemIDs.get(0);
				isPotion = itemIDs.contains(Material.POTION.getId());
			}
			
			/* We may have a slight problem here. Durabilities may require the item ID to properly decode the name,
			 * but if we have multiple item IDs that may conflict with each other. 
			 *
			 * Therefore, only items of the same category may have multiple durabilities.
			 */
			sameCategory = ItemDurabilityParser.inSameCategory(itemIDs);
			
			// Set the first item or null
			elementDurability.setItemID(first);
			elementDurability.setUsedName(false);
			
			// Get list of durabilities
			durabilities = durabilityParser.parse(tokens);
			
			// Check for multiple items and named durabilities
			if (!sameCategory && elementDurability.isUsedName())
				throw ParsingException.fromFormat(
						"Cannot use named durabilities (%s) with items of different data categories.",
						StringUtils.join(durabilities, ", "));
			
			// Negative items or durabilities are not legal
			if (hasNegativeIntegers(itemIDs) || hasNegativeIntegers(durabilities)) 
				throw new ParsingException("Item ID or durability cannot contain negative numbers");
			
			// Still more tokens? Something is wrong.
			if (!tokens.isEmpty()) {
				if (isPotion) 
					return parseAsPotion(text);
				else
					throw ParsingException.fromFormat("Unknown item tokens: ", StringUtils.join(tokens, ", "));
			}
			
			// Return universal potion query
			if (isPotion && durabilities.isEmpty()) {
				return new PotionQuery();
			}
			
			// At this point we have all we need to know
			return new ItemQuery(itemIDs, durabilities);
			
		} catch (ParsingException ex) {

			// Potion? Try again.
			if (isPotion) 
				return parseAsPotion(text);
			
			// Check for named categories
			if (!sameCategory && elementDurability.isUsedName())
				throw new ParsingException("Named durabilities with different data categories.");
			else
				throw ex;
		}
	}
	
	private boolean hasNegativeIntegers(List<Integer> values) {
		
		// See if any of the values are negative
		for (Integer value : values) {
			if (value != null && value < 0) {
				return true;
			}
		}
		
		return false;
	}
	
	private PotionQuery parseAsPotion(String text) throws ParsingException {
		
		// Delegate this task to the potion parser
		return potionParser.parsePotion(text);
	}
}
