package com.comphenix.xp.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.DyeColor;
import org.bukkit.GrassSpecies;
import org.bukkit.Material;
import org.bukkit.SandstoneType;
import org.bukkit.TreeSpecies;
import org.bukkit.material.MonsterEggs;
import org.bukkit.material.Step;

import com.comphenix.xp.extra.SmoothBrickType;

public class ItemDurabilityParser extends Parser<Integer> {

	// Quick lookup of items
	private static Map<Integer, ItemCategory> lookup = new HashMap<Integer, ItemCategory>();
	
	// Every item type
	public enum ItemCategory {
		TREE_BLOCKS(Material.WOOD.getId(), Material.LEAVES.getId(), WOODEN_STEPS, WOODEN_DOUBLE_STEPS),
		DYED_BLOCKS(Material.WOOL.getId(), Material.INK_SACK.getId()),
		GRASS_BLOCKS(Material.GRASS.getId()),
		STEP_BLOCKS(Material.STEP.getId(), Material.DOUBLE_STEP.getId()),
		SANDSTONE_BLOCKS(Material.SANDSTONE.getId()),
		SMOOTH_BRICK_BLOCKS(Material.SMOOTH_BRICK.getId(), Material.SMOOTH_STAIRS.getId()),
		MONSTER_EGGS_BLOCKS(Material.MONSTER_EGGS.getId()),
		COAL_ITEMS(Material.COAL.getId());
		
		private ItemCategory(Integer... items) {
			for (Integer item : items)
				lookup.put(item, this);
		}
		
		public static ItemCategory matchItem(Integer itemID) {
			return lookup.get(itemID);
		}
	}
	
	// New blocks
	private static final int WOODEN_STEPS = 125;
	private static final int WOODEN_DOUBLE_STEPS = 126;
	
	private static final int COAL_NORMAL = 0;
	private static final int COAL_CHARCOAL = 1;

	private Integer itemID;
	private boolean usedName;
	
	public ItemDurabilityParser() {
		this.itemID = null;
	}
	
	public ItemDurabilityParser(Integer itemID) {
		this.itemID = itemID;
	}
	
	public Integer getItemID() {
		return itemID;
	}
	
	public void setItemID(Integer itemID) {
		this.itemID = itemID;
	}
	
	public boolean isUsedName() {
		return usedName;
	}

	public void setUsedName(boolean usedName) {
		this.usedName = usedName;
	}

	@Override
	public Integer parse(String text) throws ParsingException {
		
		if (Parsing.isNullOrIgnoreable(text))
			throw new ParsingException("Text cannot be empty or null.");
		
		String filtered = Parsing.getEnumName(text);
		Integer durability = tryParse(text);
		
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
			
			// Quickly find the correct durability list to use
			switch (ItemCategory.matchItem(itemID)) {
			case TREE_BLOCKS:
				durability = getTreeSpecies(text, filtered);
				break;
			case DYED_BLOCKS:
				// Convert color values
				durability = getDyeColor(text, filtered);
				break;
			case GRASS_BLOCKS:
				// Grass types
				durability = getGrassSpecies(text, filtered);
				break;
			case STEP_BLOCKS:
				durability = getStepMaterial(text);
				break;
			case SANDSTONE_BLOCKS:
				durability = getSandstoneType(text, filtered);
				break;
			case SMOOTH_BRICK_BLOCKS:
				durability = getSmoothstoneType(text, filtered);
				break;
			case MONSTER_EGGS_BLOCKS:
				durability = getMonsterEgg(text);
				break;
			case COAL_ITEMS:
				durability = getCoalData(text, filtered);
				break;
				
			default:
				// Cannot parse durability
				throw ParsingException.fromFormat("Invalid durability value %s.", text);
			}

			// We used a name!
			setUsedName(true);
		}
		
		return durability;
	}
	
	public static boolean inSameCategory(List<Integer> listItemID) {
		
		Integer first = !listItemID.isEmpty() ?  listItemID.get(0) : null;
		ItemCategory category = ItemCategory.matchItem(first);
		
		// Make sure every item is in the same category
		for (Integer item : listItemID) {
			if (ItemCategory.matchItem(item) != category)
				return false;
		}
		
		return true;
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
			Integer attempt = parser.parse(text);
			
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
