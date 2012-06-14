package com.comphenix.xp;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import com.comphenix.xp.lookup.ItemParser;
import com.comphenix.xp.lookup.ItemTree;
import com.comphenix.xp.lookup.MobParser;
import com.comphenix.xp.lookup.MobQuery;
import com.comphenix.xp.lookup.MobTree;
import com.comphenix.xp.lookup.Parsing;
import com.comphenix.xp.lookup.PotionTree;
import com.comphenix.xp.lookup.Query;
import com.comphenix.xp.lookup.Query.Types;
import com.comphenix.xp.lookup.SearchTree;

public class Configuration {

	// Quick lookup of action types
	private static HashMap<String, ActionTypes> lookup = 
			new HashMap<String, ActionTypes>();
	
	private enum ActionTypes {
		BLOCK("BLOCK", "BLOCK_SOURCE"),
		BONUS("BONUS", "BONUS_SOURCE"),
		PLACE("PLACE", "PLACING", "PLACING_RESULT"),
		SMELTING("SMELTING", "SMELTING_RESULT"),
		CRAFTING("CRAFTING", "CRAFTING_RESULT"),
		BREWING("BREWING", "BREWING_RESULT");
		
		private ActionTypes(String... names) {
			for (String name : names) {
				lookup.put(name, this);
			}
		}
		
		public static ActionTypes matchAction(String action) {
			return lookup.get(Parsing.getEnumName(action));
		}
	}
	
	private static final String multiplierSetting = "multiplier";
	private static final String defaultRewardsSetting = "default rewards disabled";
	
	private Logger logger;
	
	private double multiplier;
	private boolean defaultRewardsDisabled;
	
	private MobTree experienceDrop;
	private ItemTree simpleBlockReward;
	private ItemTree simpleBonusReward;
	private ItemTree simplePlacingReward;
	private ItemTree simpleSmeltingReward;
	private ItemTree simpleCraftingReward;
	private ItemTree simpleBrewingReward;
	private PotionTree complexBrewingReward;
	
	private ItemParser itemParser = new ItemParser();
	private MobParser mobParser = new MobParser();
	
	public Configuration(FileConfiguration config, Logger logger) {
		this.logger = logger;
		loadFromConfig(config);
	}
	
	private void loadFromConfig(FileConfiguration config) {
		
		// Clear previous values
		experienceDrop = new MobTree();
		simpleBlockReward = new ItemTree();
		simpleBonusReward = new ItemTree();
		simplePlacingReward = new ItemTree();
		simpleSmeltingReward = new ItemTree();
		simpleCraftingReward = new ItemTree();
		simpleBrewingReward = new ItemTree();
		complexBrewingReward = new PotionTree();
		
		// Load scalar values
		if (config.isDouble(multiplierSetting))
			multiplier = config.getDouble(multiplierSetting, 1);
		else
			multiplier = config.getInt(multiplierSetting, 1);
		
		defaultRewardsDisabled = config.getBoolean(defaultRewardsSetting, true);
		
		// Load mob experience
		loadMobs(config.getConfigurationSection("mobs"));
		loadItemActions(config.getConfigurationSection("items"));
	}
	
	private void loadMobs(ConfigurationSection config) {
		// Guard against null
		if (config == null)
			return;
		
		for (String key : config.getKeys(false)) {
			try {				
				Range value = readRange(config, key, null);
				MobQuery query = mobParser.fromString(key);
				
				if (value != null)
					experienceDrop.put(query, value.multiply(multiplier));
				else
					logger.warning(String.format("Unable to parse range/value on entity %s.", key));
				
			} catch (IllegalArgumentException ex) {
				logger.warning(String.format("Cannot parse mob %s: %s", key, ex.getMessage()));
			}
		}
	}
	
	private void loadItemActions(ConfigurationSection config) {
		// Guard against null
		if (config == null)
			return;
		
		for (String key : config.getKeys(false)) {
			try {
				Query item = itemParser.fromItemString(key);
				ConfigurationSection itemSection = config.getConfigurationSection(key);
				boolean isItemType = item.getQueryType() == Types.Items;
				
				// Read the different rewards
				for (String action : itemSection.getKeys(false)) {
					
					switch (ActionTypes.matchAction(action)) {
					case BLOCK:
						loadActionOnItem(itemSection, action, item, simpleBlockReward, Query.Types.Items);
						break;
					case BONUS:
						loadActionOnItem(itemSection, action, item, simpleBonusReward, Query.Types.Items);
						break;
					case PLACE:
						loadActionOnItem(itemSection, action, item, simplePlacingReward, Query.Types.Items);
						break;
					case SMELTING:
						loadActionOnItem(itemSection, action, item, simpleSmeltingReward, Query.Types.Items);
						break;
					case CRAFTING:
						loadActionOnItem(itemSection, action, item, simpleCraftingReward, Query.Types.Items);
						break;
					case BREWING:
						loadActionOnItem(itemSection, action, item, 
							isItemType ? simpleBrewingReward : complexBrewingReward,
							isItemType ? Query.Types.Items : Query.Types.Potions);
						break;
					default:
						logger.warning("Unrecogized action " + action + " under item " + key);
					}
				}
				
			} catch (IllegalArgumentException ex) {
				logger.warning(String.format("Cannot parse item %s: %s", key, ex.getMessage()));
			}
		}
	}

	// I just wanted handle SearchTree<ItemQuery, Range> and SearchTree<PotionQuery, Range> with the same method, but
	// apparently you can't simply use SearchTree<Query, Range> or some derivation to match them both. Too bad.
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void loadActionOnItem(ConfigurationSection config, String key, Query item, SearchTree destination, Query.Types checkType)  {
		
		Range range = readRange(config, key, null);
		
		// Check the query type
		if (item.getQueryType() != checkType)
			throw new IllegalArgumentException("Cannot load action " + key + " on this item matcher.");
		
		// Ignore this type
		if (range != null) {
			destination.put(item, range.multiply(multiplier));
		} else {
			logger.warning(String.format("Unable to read range on %s.", key));
		}
	}

	private Range readRange(ConfigurationSection config, String key, Range defaultValue) {
		
		String start = key + ".first";
		String end = key + ".last";
		
		if (config.isDouble(key)) {
	
			return new Range(config.getDouble(key));
		
		} else if (config.isInt(key)) {
			
			return new Range((double) config.getInt(key));
			
		} else if (config.contains(start) && config.contains(end)) {
			
			return new Range(config.getDouble(start), config.getDouble(end));
	
		} else if (config.isList(key)) {
			
			// Try to get a double list
			List<Double> attempt = config.getDoubleList(key);

			if (attempt != null && attempt.size() == 2)
				return new Range(attempt.get(0), attempt.get(1));
			else if (attempt != null && attempt.size() == 1)
				return new Range(attempt.get(0));
			else
				return defaultValue;
			
		} else {
			// Default value
			return defaultValue;
		}
	}
	
	public double getMultiplier() {
		return multiplier;
	}

	public MobTree getExperienceDrop() {
		return experienceDrop;
	}

	public ItemTree getSimpleBlockReward() {
		return simpleBlockReward;
	}

	public ItemTree getSimpleBonusReward() {
		return simpleBonusReward;
	}

	public ItemTree getSimpleCraftingReward() {
		return simpleCraftingReward;
	}

	public ItemTree getSimpleSmeltingReward() {
		return simpleSmeltingReward;
	}

	public ItemTree getSimpleBrewingReward() {
		return simpleBrewingReward;
	}

	public PotionTree getComplexBrewingReward() {
		return complexBrewingReward;
	}

	public ItemTree getSimplePlacingReward() {
		return simplePlacingReward;
	}

	public boolean isDefaultRewardsDisabled() {
		return defaultRewardsDisabled;
	}
}
