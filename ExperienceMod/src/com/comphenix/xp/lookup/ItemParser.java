package com.comphenix.xp.lookup;

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
	
	public Query fromItemString(String text) {
		
		if (text.length() == 0)
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
					throw new IllegalArgumentException("Unable to find item.");
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
	
	private Integer parseDurability(Integer itemID, String text) {
		
		// Get the durability value
		Integer durability = Parsing.tryParse(text);
		String filtered = Parsing.getEnumName(text);
		
		if (durability == null) {
			
			// Special cases
			if (itemID == Material.WOOD.getId() || itemID == Material.LEAVES.getId() || 
				itemID == WOODEN_STEPS || itemID == WOODEN_DOUBLE_STEPS) {
				
				if (filtered.equals("OAK"))
					return (int) 0;
				else
					// Will throw IllegalArgumentException
					return (int) TreeSpecies.valueOf(filtered).getData();
				
			} else if (itemID == Material.WOOL.getId()) {
				
				// Convert color values
				return (int) DyeColor.valueOf(filtered).getData();
				
			} else if (itemID == Material.GRASS.getId()) { 
				
				// Grass types
				return (int) GrassSpecies.valueOf(filtered).getData();

			} else if (itemID == Material.STEP.getId() || itemID == Material.DOUBLE_STEP.getId()) {
				
				Material material = getMaterial(text);
				Step step = new Step();
				
				// Get texture durability value
				if (step.getTextures().contains(material)) {
					return (int) new Step(material).getData();	
				} else {
					throw new IllegalArgumentException("Unable to parse texture material.");
				}

			} else if (itemID == Material.SANDSTONE.getId()) {
		
				// Extra types
				if (filtered.equals("NORMAL"))
					return 0;
				if (filtered.equals("CHISELED"))
					return 1;
		
				// Note: Like many enums, this doesn't correspond to MinecraftWiki
				return (int) SandstoneType.valueOf(filtered).getData();
			
			} else if (itemID == Material.SMOOTH_BRICK.getId() || itemID == Material.SMOOTH_STAIRS.getId()) {
				
				// Alternatives
				if (filtered.equals("CHISELED"))
					return 3;
				
				return (int) SmoothBrickType.valueOf(filtered).getData();
				
			} else if (itemID == Material.MONSTER_EGGS.getId()) {
				
				Material material = getMaterial(text);
				MonsterEggs monsterEgg = new MonsterEggs();
				
				// Get texture durability value
				if (monsterEgg.getTextures().contains(material)) {
					return (int) new MonsterEggs(material).getData();	
				} else {
					throw new IllegalArgumentException("Unable to parse texture material.");
				}
				
			} else if (itemID == Material.POTION.getId()) {
				// Let the caller figure this out
				return null; 
			}
			
			// Cannot parse durability
			throw new IllegalArgumentException("Invalid durability value.");
		}
		
		return durability;
	}

	// Special potion parser
	private PotionQuery parsePotion(String[] components, int offset) {
		
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
			throw new IllegalArgumentException("Unable to parse potion type.");
		if (tier > potionType.getMaxLevel())
			throw new IllegalArgumentException("Potion level is too high.");
		else if (tier < 1)
			throw new IllegalArgumentException("Potion level is too low.");
		
		// Create the query
		return new PotionQuery(potionType, tier, extended, splash);
	}
	
	private Material getMaterial(String text) {
		
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
