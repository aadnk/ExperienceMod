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

import java.util.HashMap;

import org.bukkit.DyeColor;
import org.bukkit.GrassSpecies;
import org.bukkit.Material;
import org.bukkit.SandstoneType;
import org.bukkit.TreeSpecies;
import org.bukkit.material.MonsterEggs;
import org.bukkit.material.Step;
import org.bukkit.potion.PotionType;

import com.comphenix.extra.SmoothBrickType;

public class ItemParser {
	
	private static HashMap<String, Material> alternativeNames = new HashMap<String, Material>();

	// New blocks
	private static final int WOODEN_STEPS = 125;
	private static final int WOODEN_DOUBLE_STEPS = 126;
	
	private static final int COAL_NORMAL = 0;
	private static final int COAL_CHARCOAL = 1;
	
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
	
	public Query fromItemString(String text) throws ParsingException {
		
		if (text.length() == 0)
			// Empty names are not legal in YAML, so this shouldn't be possible 
			throw new IllegalArgumentException("Key must have some characters.");
		
		String[] components = Parsing.getParameterArray(text);

		Material material = Material.matchMaterial(components[0]);
		Integer itemID = Parsing.tryParse(components, 0);
		Integer durability = null;
		
		// Is this an item?
		if (itemID == null && !Parsing.isNullOrIgnoreable(components[0])) {
			if (material != null) {
				itemID = material.getId();

			} else {
				// Try some additional values
				String filtered = Parsing.getEnumName(components[0]);

				if (alternativeNames.containsKey(filtered))
					itemID = alternativeNames.get(filtered).getId();
				else
					throw new ParsingException("Unable to find item.");
			}
		}
		
		// Empty means DON'T CARE
		if (components.length > 1 && !Parsing.isNullOrIgnoreable(components[1])) {
			durability = parseDurability(itemID, components[1]);
			
			// Special parsing rules
			if (itemID == Material.POTION.getId() && durability == null) {
				return parsePotion(components, 1);
			}
		}
		
		return (Query) new ItemQuery(itemID, durability);
	}
	
	private Integer parseDurability(Integer itemID, String text) throws ParsingException {
		
		// Get the durability value
		Integer durability = Parsing.tryParse(text);
		String filtered = Parsing.getEnumName(text);
		
		if (durability == null) {
			
			// Special cases
			if (itemID == Material.WOOD.getId() || itemID == Material.LEAVES.getId() || 
				itemID == WOODEN_STEPS || itemID == WOODEN_DOUBLE_STEPS) {
				
				return getTreeSpecies(text, filtered);

			} else if (itemID == Material.WOOL.getId()) {
				
				// Convert color values
				return getDyeColor(text, filtered);
				
			} else if (itemID == Material.GRASS.getId()) { 
				
				// Grass types
				return getGrassSpecies(text, filtered);

			} else if (itemID == Material.STEP.getId() || itemID == Material.DOUBLE_STEP.getId()) {
				
				return getStepMaterial(text);
				
			} else if (itemID == Material.SANDSTONE.getId()) {
		
				return getSandstoneType(text, filtered);
			
			} else if (itemID == Material.SMOOTH_BRICK.getId() || itemID == Material.SMOOTH_STAIRS.getId()) {
				
				return getSmoothstoneType(text, filtered);
				
			} else if (itemID == Material.MONSTER_EGGS.getId()) {
				
				return getMonsterEgg(text, filtered);
				
			} else if (itemID == Material.COAL.getId()) {
				
				// No corresponding constants in Bukkit
				if (filtered.equals("NORMAL") || filtered.equals("COAL_NORMAL"))
					return COAL_NORMAL;
				else if (filtered.equals("CHARCOAL") || filtered.equals("CHAR_COAL"))
					return COAL_CHARCOAL;
				else
					ParsingException.fromFormat("Unable to find coal type %s", text);
				
			} else if (itemID == Material.POTION.getId()) {
				// Let the caller figure this out
				return null; 
			}
			
			// Cannot parse durability
			throw ParsingException.fromFormat("Invalid durability value %s.", text);
		}
		
		return durability;
	}

	private int getTreeSpecies(String originalText, String filtered) throws ParsingException {

		try {
			if (filtered.equals("OAK"))
				return (int) 0;
			else
				return (int) TreeSpecies.valueOf(filtered).getData();
			
		} catch (IllegalArgumentException e) {
			throw ParsingException.fromFormat("Unable to find tree species %s.", originalText);
		}
	}
	
	private int getStepMaterial(String originalText) throws ParsingException {
		
		Material material = getMaterial(originalText);
		Step step = new Step();
		
		// Get texture durability value
		if (step.getTextures().contains(material)) {
			return (int) new Step(material).getData();	
		} else {
			throw ParsingException.fromFormat("Unable to parse texture material %s.", originalText);
		}
	}
	
	private int getSandstoneType(String originalText, String filtered) throws ParsingException {
		
		// Extra types
		if (filtered.equals("NORMAL"))
			return 0;
		if (filtered.equals("CHISELED"))
			return 1;

		// Note: Like many enums, this doesn't correspond to MinecraftWiki
		try {
			return (int) SandstoneType.valueOf(filtered).getData();
		} catch (IllegalArgumentException e) {
			throw ParsingException.fromFormat("Unable to find sandstone type %s.", originalText);
		}
	}
	
	private int getMonsterEgg(String originalText, String filtered) throws ParsingException {
		
		Material material = getMaterial(originalText);
		MonsterEggs monsterEgg = new MonsterEggs();
		
		// Get texture durability value
		if (monsterEgg.getTextures().contains(material)) {
			return (int) new MonsterEggs(material).getData();	
		} else {
			throw ParsingException.fromFormat("Unable to parse texture material %s", originalText);
		}
	}
	
	private int getSmoothstoneType(String originalText, String filtered) throws ParsingException {
		
		// Alternatives
		if (filtered.equals("CHISELED"))
			return 3;
		
		try {
			return (int) SmoothBrickType.valueOf(filtered).getData();
		} catch (IllegalArgumentException e) {
			throw ParsingException.fromFormat("Unable to find smoothstone type %s.", originalText);
		}
	}
	
	private int getDyeColor(String originalText, String filtered) throws ParsingException {
		
		try {
			// Look up value directly
			return (int) DyeColor.valueOf(filtered).getData();
		} catch (IllegalArgumentException e) {
			throw ParsingException.fromFormat("Unable to find dye color %s.", originalText);
		}
	}
	
	private int getGrassSpecies(String originalText, String filtered) throws ParsingException {
		
		try {
			return (int) GrassSpecies.valueOf(filtered).getData();
		} catch (IllegalArgumentException e) {
			throw ParsingException.fromFormat("Unable to grass species %s.", originalText);
		}
	}
	
	// Special potion parser
	private PotionQuery parsePotion(String[] components, int offset) throws ParsingException {
		
		Integer potionID = Parsing.tryParse(components, offset);
		Integer tier = Parsing.tryParse(components, offset + 1, 1);
		
		// Increment (consider using a stack instead)
		if (tier != null)
			offset += 2;
		else if (potionID != null)
			offset += 1;
		
		// Scan all unused parameters for these options
		Boolean extended = Parsing.hasElementPrefix(components, offset, "extended");
		Boolean splash = Parsing.hasElementPrefix(components, offset, "splash");
		PotionType potionType = null;
		
		// Parse the potion type
		if (potionID != null) {
			potionType = PotionType.getByDamageValue(potionID);
		} else {
			potionType = PotionType.valueOf(Parsing.getEnumName(components[1]));
		}
		
		if (potionType == null)
			throw new ParsingException("Unable to parse potion type.");
		if (tier > potionType.getMaxLevel())
			throw ParsingException.fromFormat("Potion level %d is too high.", tier);
		else if (tier < 1)
			throw ParsingException.fromFormat("Potion level %d is too low.", tier);
		
		// Create the query
		return new PotionQuery(potionType, tier, extended, splash);
	}
	
	private Material getMaterial(String text) throws ParsingException {
		
		Object attempt = fromItemString(text);

		// See if it succeeded
		if (attempt instanceof ItemQuery) {
			ItemQuery query = (ItemQuery) attempt;
			Material material = Material.getMaterial(query.getItemID());
			return material;
		} else 
			return null;
	}
}
