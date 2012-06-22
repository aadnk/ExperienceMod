package com.comphenix.xp.parser;

import java.util.HashMap;

import org.bukkit.Material;

public class ItemNameParser extends Parser<Integer> {

	private static HashMap<String, Material> alternativeNames = new HashMap<String, Material>();
	
	static {
		alternativeNames.put("WOOL_DYE", Material.INK_SACK);
		alternativeNames.put("WOOL_DYES", Material.INK_SACK);
		alternativeNames.put("SLAB", Material.STEP);
		alternativeNames.put("DOUBLE_SLAB", Material.DOUBLE_STEP);
		alternativeNames.put("STONE_BRICK", Material.SMOOTH_BRICK);
		alternativeNames.put("STONE_BRICK_STAIRS", Material.SMOOTH_STAIRS);
		alternativeNames.put("HUGE_BROWN_MUSHROOM", Material.HUGE_MUSHROOM_1);
		alternativeNames.put("HUGE_RED_MUSHROOM", Material.HUGE_MUSHROOM_2);
		alternativeNames.put("SILVERFISH_BLOCK", Material.MONSTER_EGGS);
		alternativeNames.put("RECORD_1", Material.GOLD_RECORD);
		alternativeNames.put("RECORD_2", Material.GREEN_RECORD);
		alternativeNames.put("BOTTLE_O_ENCHANTING", Material.EXP_BOTTLE);
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
		
		Material material = Material.matchMaterial(text);
		Integer itemID = tryParse(text);
		
		// Is this an item?
		if (itemID == null) {
			
			if (material != null) {
				itemID = material.getId();

			} else {
				// Try some additional values
				String filtered = Utility.getEnumName(text);

				if (alternativeNames.containsKey(filtered))
					itemID = alternativeNames.get(filtered).getId();
				else
					throw ParsingException.fromFormat("Unable to find item %s.", text);
			}
		}

		return itemID;
	}
}
