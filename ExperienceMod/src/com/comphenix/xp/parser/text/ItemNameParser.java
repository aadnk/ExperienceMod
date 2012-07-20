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

import java.util.HashMap;

import org.bukkit.Material;

import com.comphenix.xp.parser.TextParser;
import com.comphenix.xp.parser.ParsingException;
import com.comphenix.xp.parser.Utility;

public class ItemNameParser extends TextParser<Integer> {

	protected HashMap<String, Integer> lookupMaterial = new HashMap<String, Integer>();

	public ItemNameParser() {
		loadDefaultList();
	}
	
	protected void loadDefaultList() {
		// Default list of extra materials
		register("WOOLDYE", Material.INK_SACK);
		register("WOOLDYES", Material.INK_SACK);
		register("SLAB", Material.STEP);
		register("DOUBLESLAB", Material.DOUBLE_STEP);
		register("STONEBRICK", Material.SMOOTH_BRICK);
		register("STONEBRICKSTAIRS", Material.SMOOTH_STAIRS);
		register("HUGEBROWNMUSHROOM", Material.HUGE_MUSHROOM_1);
		register("HUGEREDMUSHROOM", Material.HUGE_MUSHROOM_2);
		register("SILVERFISHBLOCK", Material.MONSTER_EGGS);
		register("RECORD1", Material.GOLD_RECORD);
		register("RECORD2", Material.GREEN_RECORD);
		register("BOTTLEOENCHANTING", Material.EXP_BOTTLE);
		
		// Add every other material with no spaces
		for (Material material : Material.values()) {
			register(material.name().replace("_", ""), material);
		}
	}
	
	/**
	 * Registers a new material for the item parser. 
	 * @param name - name of the material. Only capital letters and no underscores/spaces.
	 * @param material - the associated material.
	 */
	public void register(String name, Material material) {
		register(name, material.getId());
	}
	
	/**
	 * Registers a new material for the item parser. 
	 * @param name - name of the material. Only capital letters and no underscores/spaces.
	 * @param material - the associated material id.
	 */
	public void register(String name, Integer id) {
		lookupMaterial.put(name, id);
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
			return lookupMaterial.get(filtered);
		else if (itemID == null)
			throw ParsingException.fromFormat("Unable to find item %s.", text);

		return itemID;
	}
}
