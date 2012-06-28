package com.comphenix.xp.parser.text;

import java.util.HashMap;

import org.bukkit.Material;

import com.comphenix.xp.parser.TextParser;
import com.comphenix.xp.parser.ParsingException;
import com.comphenix.xp.parser.Utility;

public class ItemNameParser extends TextParser<Integer> {

	private static HashMap<String, Material> lookupMaterial = new HashMap<String, Material>();
	
	static {
		lookupMaterial.put("WOOLDYE", Material.INK_SACK);
		lookupMaterial.put("WOOLDYES", Material.INK_SACK);
		lookupMaterial.put("SLAB", Material.STEP);
		lookupMaterial.put("DOUBLESLAB", Material.DOUBLE_STEP);
		lookupMaterial.put("STONEBRICK", Material.SMOOTH_BRICK);
		lookupMaterial.put("STONEBRICKSTAIRS", Material.SMOOTH_STAIRS);
		lookupMaterial.put("HUGEBROWNMUSHROOM", Material.HUGE_MUSHROOM_1);
		lookupMaterial.put("HUGEREDMUSHROOM", Material.HUGE_MUSHROOM_2);
		lookupMaterial.put("SILVERFISHBLOCK", Material.MONSTER_EGGS);
		lookupMaterial.put("RECORD1", Material.GOLD_RECORD);
		lookupMaterial.put("RECORD2", Material.GREEN_RECORD);
		lookupMaterial.put("BOTTLEOENCHANTING", Material.EXP_BOTTLE);
		
		// Add every other material with no spaces
		for (Material material : Material.values()) {
			lookupMaterial.put(material.name().replace("_", ""), material);
		}
	}
	
	/**
	 * Determines the item, either by ID or name, of the given string of characters.
	 * @param text String of characters.
	 * @return ID of the item parsed.
	 * @throws ParsingException Invoked when an unrecognized item name is given.
	 */
	@Override
	public Integer parse(String text) throws ParsingException {

		if (Utility.isNullOrIgnoreable(text))
			throw new ParsingException("Text cannot be empty or null.");
		
		// Try both integers and named values
		Integer itemID = tryParse(text);
		String filtered = Utility.getEnumName(text).replace("_", "");

		// Use the lookup table
		if (lookupMaterial.containsKey(filtered))
			return lookupMaterial.get(filtered).getId();
		else if (itemID == null)
			throw ParsingException.fromFormat("Unable to find item %s.", text);

		return itemID;
	}
}
