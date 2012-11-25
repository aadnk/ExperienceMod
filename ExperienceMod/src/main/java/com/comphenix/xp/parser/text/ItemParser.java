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
import java.util.Set;
import java.util.Queue;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import com.comphenix.xp.lookup.ItemQuery;
import com.comphenix.xp.lookup.PotionQuery;
import com.comphenix.xp.lookup.Query;
import com.comphenix.xp.parser.TextParser;
import com.comphenix.xp.parser.ParsingException;
import com.comphenix.xp.parser.Utility;
import com.comphenix.xp.parser.primitives.BooleanParser;

public class ItemParser extends TextParser<Query> {
	
	private ParameterParser<Set<Integer>> itemNameParser;
	
	private ItemDurabilityParser elementDurability = new ItemDurabilityParser();
	private ParameterParser<Integer> durabilityParser = new ParameterParser<Integer>(elementDurability);
	
	// Parse options
	private BooleanParser playerParser = new BooleanParser("player");
	
	// Our potion parser
	private PotionParser potionParser;
	
	public ItemParser(ItemNameParser nameParser) {
		potionParser = new PotionParser(nameParser, new PotionTypeParser());
		itemNameParser = new ParameterParser<Set<Integer>>(nameParser);
	}
	
	@Override
	public Query parse(String text) throws ParsingException {
		
		if (text.length() == 0)
			// Empty names are not legal in YAML, so this shouldn't be possible 
			throw new IllegalArgumentException("Key must have some characters.");
		
		Queue<String> tokens = getParameterQueue(text);
		
		List<Integer> itemIDs = Utility.getElementList((Integer) null);;
		List<Integer> durabilities = Utility.getElementList((Integer) null);;
		
		ParsingException errorReason = null;
		Integer first = null;
		
		boolean isPotion = false;
		boolean sameCategory = false;
		
		try {
			// Get item IDs
			itemIDs = Utility.flatten(itemNameParser.parse(tokens));
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
			
		} catch (ParsingException ex) {

			// Potion? Try again.
			if (isPotion) 
				return parseAsPotion(text);
			
			// Check for named categories
			if (!sameCategory && elementDurability.isUsedName())
				throw new ParsingException("Named durabilities with different data categories.");
			else
				// Try more
				errorReason = ex;
		}
			
		// Scan for the "player creation" option
		List<Boolean> playerCreation = playerParser.parseAny(tokens);
		
		// Still more tokens? Something is wrong.
		if (!tokens.isEmpty()) {
			if (isPotion) 
				return parseAsPotion(text);
			else if (errorReason == null)
				throw ParsingException.fromFormat("Unknown item tokens: ", StringUtils.join(tokens, ", "));
			else
				throw errorReason;
		}
		
		// Return universal potion query
		if (isPotion && durabilities.isEmpty()) {
			return PotionQuery.fromAny();
		}
		
		// At this point we have all we need to know
		return new ItemQuery(itemIDs, durabilities, playerCreation);
	}
	
	public ParameterParser<Set<Integer>> getItemNameParser() {
		return itemNameParser;
	}

	public void setItemNameParser(ParameterParser<Set<Integer>> itemNameParser) {
		this.itemNameParser = itemNameParser;
	}

	public ItemDurabilityParser getElementDurability() {
		return elementDurability;
	}

	public void setElementDurability(ItemDurabilityParser elementDurability) {
		this.elementDurability = elementDurability;
	}

	public ParameterParser<Integer> getDurabilityParser() {
		return durabilityParser;
	}

	public void setDurabilityParser(ParameterParser<Integer> durabilityParser) {
		this.durabilityParser = durabilityParser;
	}

	public PotionParser getPotionParser() {
		return potionParser;
	}

	public void setPotionParser(PotionParser potionParser) {
		this.potionParser = potionParser;
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
		return potionParser.parse(text);
	}
}
