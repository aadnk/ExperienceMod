package com.comphenix.xp.parser;

import org.bukkit.DyeColor;
import org.bukkit.GrassSpecies;
import org.bukkit.Material;
import org.bukkit.SandstoneType;
import org.bukkit.TreeSpecies;
import org.bukkit.material.MonsterEggs;
import org.bukkit.material.Step;

import com.comphenix.xp.extra.SmoothBrickType;

public class ItemDurabilityParser implements Parser<Integer> {

	// New blocks
	private static final int WOODEN_STEPS = 125;
	private static final int WOODEN_DOUBLE_STEPS = 126;
	
	private static final int COAL_NORMAL = 0;
	private static final int COAL_CHARCOAL = 1;

	private Integer itemID;
	
	public ItemDurabilityParser(Integer itemID) {
		this.itemID = itemID;
	}
	
	public Integer getItemID() {
		return itemID;
	}
	
	public void setItemID(Integer itemID) {
		this.itemID = itemID;
	}
	
	@Override
	public Integer Parse(String text) throws ParsingException {
		
		if (Parsing.isNullOrIgnoreable(text))
			throw new ParsingException("Text cannot be empty or null.");
		
		String filtered = Parsing.getEnumName(text);
		Integer durability = Parsing.tryParse(text);
		
		if (durability == null) {
			
			if (isItem(itemID, Material.POTION)) {
				// Let the caller figure this out. The token are to be processed further,
				// so we'll keep it in.
				return null; 
			}
			
			// ToDo: Make it possible to use named durabilities without the correct item ID
			if (itemID == null) {
				throw ParsingException.fromFormat(
						"Cannot parse %s - named durabilities only works with known item ids.", text);
			}
			
			// Special cases
			if (itemID == Material.WOOD.getId() || itemID == Material.LEAVES.getId() || 
				itemID == WOODEN_STEPS || itemID == WOODEN_DOUBLE_STEPS) {
				
				return getTreeSpecies(text, filtered);

			} else if (itemID == Material.WOOL.getId() || itemID == Material.INK_SACK.getId()) {
				
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
				
				return getMonsterEgg(text);
				
			} else if (itemID == Material.COAL.getId()) {
				
				return getCoalData(text, filtered);
				
			} 
			
			// Cannot parse durability
			throw ParsingException.fromFormat("Invalid durability value %s.", text);
		}
		
		return durability;
	}
	
	private int getCoalData(String text, String filtered) throws ParsingException {
		
		// No corresponding constants in Bukkit
		if (filtered.equals("NORMAL") || filtered.equals("COAL_NORMAL"))
			return COAL_NORMAL;
		else if (filtered.equals("CHARCOAL") || filtered.equals("CHAR_COAL"))
			return COAL_CHARCOAL;
		else
			throw ParsingException.fromFormat("Unable to find coal type %s", text);
	}
	
	private int getTreeSpecies(String text, String filtered) throws ParsingException {

		try {
			// Hard coded names
			if (filtered.equals("OAK"))
				return 0;
			else
				return (int) TreeSpecies.valueOf(filtered).getData();

		} catch (IllegalArgumentException e) {
			throw ParsingException.fromFormat("Unable to find tree species %s.", text);
		}
	}
	
	private int getStepMaterial(String text) throws ParsingException {
		
		Material material = getMaterial(text);
		Step step = new Step();
		
		// Get texture durability value
		if (step.getTextures().contains(material)) {
			return (int) new Step(material).getData();	
		} else {
			throw ParsingException.fromFormat("Unable to parse texture material %s.", text);
		}
	}
	
	private int getSandstoneType(String text, String filtered) throws ParsingException {
		
		// Extra types
		if (filtered.equals("NORMAL"))
			return 0;
		if (filtered.equals("CHISELED"))
			return 1;

		// Note: Like many enums, this doesn't correspond to MinecraftWiki
		try {
			return (int) SandstoneType.valueOf(filtered).getData();
		} catch (IllegalArgumentException e) {
			throw ParsingException.fromFormat("Unable to find sandstone type %s.", text);
		}
	}
	
	private int getMonsterEgg(String text) throws ParsingException {
		
		Material material = getMaterial(text);
		MonsterEggs monsterEgg = new MonsterEggs();
		
		// Get texture durability value
		if (monsterEgg.getTextures().contains(material)) {
			return (int) new MonsterEggs(material).getData();	
		} else {
			throw ParsingException.fromFormat("Unable to parse texture material %s", text);
		}
	}
	
	private int getSmoothstoneType(String text, String filtered) throws ParsingException {
		
		// Alternatives
		if (filtered.equals("CHISELED"))
			return 3;
		
		try {
			return (int) SmoothBrickType.valueOf(filtered).getData();
		} catch (IllegalArgumentException e) {
			throw ParsingException.fromFormat("Unable to find smoothstone type %s.", text);
		}
	}
	
	private int getDyeColor(String text, String filtered) throws ParsingException {
		
		try {
			// Look up value directly
			return (int) DyeColor.valueOf(filtered).getData();
			
		} catch (IllegalArgumentException e) {
			throw ParsingException.fromFormat("Unable to find dye color %s.", text);
		}
	}
	
	private int getGrassSpecies(String text, String filtered) throws ParsingException {
		
		try {
			return (int) GrassSpecies.valueOf(filtered).getData();
			
		} catch (IllegalArgumentException e) {
			throw ParsingException.fromFormat("Unable to grass species %s.", text);
		}
	}
	
	private boolean isItem(Integer itemID, Material material) {
		
		// Check for equality
		if (itemID == null)
			return false;
		else
			return itemID == material.getId();
	}
	
	private Material getMaterial(String text) throws ParsingException {
		
		try {
		
			ItemNameParser parser = new ItemNameParser();
			Integer attempt = parser.Parse(text);
			
			// See if it succeeded
			if (attempt != null) 
				return Material.getMaterial(attempt);
			else 
				return null;
		
		} catch (ParsingException e) {
			return null;
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
}
