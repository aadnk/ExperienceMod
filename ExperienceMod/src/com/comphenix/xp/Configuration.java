package com.comphenix.xp;

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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import com.comphenix.xp.lookup.*;
import com.comphenix.xp.lookup.Query.Types;
import com.comphenix.xp.parser.ItemParser;
import com.comphenix.xp.parser.MobParser;
import com.comphenix.xp.parser.Utility;
import com.comphenix.xp.parser.ParsingException;
import com.comphenix.xp.rewards.Rewardable;
import com.comphenix.xp.rewards.RewardTypes;

public class Configuration implements Multipliable<Configuration> {
	
	// Quick lookup of action types
	private static HashMap<String, ActionTypes> lookup = 
			new HashMap<String, ActionTypes>();
	
	public enum ActionTypes {
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
			return lookup.get(Utility.getEnumName(action));
		}
	}
		
	private static final String multiplierSetting = "multiplier";
	private static final String defaultRewardsSetting = "default rewards disabled";
	private static final String rewardTypeSetting = "reward type";
	
	private Debugger logger;
	
	private double multiplier;
	private boolean defaultRewardsDisabled;
	
	private RewardTypes rewardType;
	private Rewardable rewardManager;

	private MobTree experienceDrop;
	private ItemTree simpleBlockReward;
	private ItemTree simpleBonusReward;
	private ItemTree simplePlacingReward;
	private ItemTree simpleSmeltingReward;
	private ItemTree simpleCraftingReward;
	private ItemTree simpleBrewingReward;
	private PotionTree complexBrewingReward;
	private PlayerRewards playerRewards;
	
	private ItemParser itemParser = new ItemParser();
	private MobParser mobParser = new MobParser();
	
	public Configuration(Debugger debugger) {
		this.logger = debugger;
		this.rewardType = RewardTypes.EXPERIENCE;
		this.defaultRewardsDisabled = false;
		initialize(1);
	}
	
	public Configuration(Configuration other, double newMultiplier) {
		if (other == null)
			throw new IllegalArgumentException("other");
		
		// Copy (and change) scalars
		this.multiplier = newMultiplier;
		this.logger = other.logger;
		this.defaultRewardsDisabled = other.defaultRewardsDisabled;
		this.rewardType = other.rewardType;
		
		// Copy (shallow) trees
		this.experienceDrop = other.experienceDrop.withMultiplier(newMultiplier);
		this.simpleBlockReward = other.simpleBlockReward.withMultiplier(newMultiplier);
		this.simpleBonusReward = other.simpleBonusReward.withMultiplier(newMultiplier);
		this.simplePlacingReward = other.simplePlacingReward.withMultiplier(newMultiplier);
		this.simpleSmeltingReward = other.simpleSmeltingReward.withMultiplier(newMultiplier);
		this.simpleCraftingReward = other.simpleCraftingReward.withMultiplier(newMultiplier);
		this.simpleBrewingReward = other.simpleBrewingReward.withMultiplier(newMultiplier);
		this.complexBrewingReward = other.complexBrewingReward.withMultiplier(newMultiplier);
		this.playerRewards = other.playerRewards.withMultiplier(newMultiplier);
		this.checkRewards();
	}
	
	public Configuration(ConfigurationSection config, Debugger debugger) {
		this.logger = debugger;
		loadFromConfig(config);
	}
	
	/**
	 * Merge a list of configurations into a new configuration.
	 * 
	 * @param configurations List of configurations.
	 * @param debugger Debugger instance.
	 * @return Merged configuration.
	 */
	public static Configuration fromMultiple(List<Configuration> configurations, Debugger debugger) {
		
		if (configurations == null)
			return null;
		else if (configurations.size() == 0)
			return null;
		else if (configurations.size() == 1)
			return configurations.get(0);
		
		Configuration copy = new Configuration(debugger);
		
		// Merge everything in order
		for (Configuration config : configurations) {
			copy.complexBrewingReward.putAll(config.complexBrewingReward);
			copy.experienceDrop.putAll(config.experienceDrop);
			copy.playerRewards.putAll(config.playerRewards);
			copy.simpleBlockReward.putAll(config.simpleBlockReward);
			copy.simpleBonusReward.putAll(config.simpleBonusReward);
			copy.simpleBrewingReward.putAll(config.simpleBrewingReward);
			copy.simpleCraftingReward.putAll(config.simpleCraftingReward);
			copy.simplePlacingReward.putAll(config.simplePlacingReward);
			copy.simpleSmeltingReward.putAll(config.simpleSmeltingReward);
			
			// This will be the last set value
			copy.defaultRewardsDisabled = config.defaultRewardsDisabled;
			copy.rewardType = config.rewardType;
			
			// Multiply all multipliers
			copy.multiplier *= config.multiplier; 
		}
		
		// Update multiplier
		return new Configuration(copy, copy.multiplier);
	}
	
	private void loadFromConfig(ConfigurationSection config) {
		
		// Load scalar values
		if (config.isDouble(multiplierSetting))
			multiplier = config.getDouble(multiplierSetting, 1);
		else
			multiplier = config.getInt(multiplierSetting, 1);

		// Whether or not to remove all default XP drops
		defaultRewardsDisabled = config.getBoolean(defaultRewardsSetting, true);
		
		// Load reward type
		rewardType = loadReward(config.getString(rewardTypeSetting, "experience"));
		initialize(multiplier);

		// Load mob experience
		loadMobs(config.getConfigurationSection("mobs"));
		loadItemActions(config.getConfigurationSection("items"));
		loadGenericRewards(config.getConfigurationSection("player"));
		checkRewards();
	}
	
	private void initialize(double multiplier) {
		
		// Clear previous values
		experienceDrop = new MobTree(multiplier);
		simpleBlockReward = new ItemTree(multiplier);
		simpleBonusReward = new ItemTree(multiplier);
		simplePlacingReward = new ItemTree(multiplier);
		simpleSmeltingReward = new ItemTree(multiplier);
		simpleCraftingReward = new ItemTree(multiplier);
		simpleBrewingReward = new ItemTree(multiplier);
		complexBrewingReward = new PotionTree(multiplier);
		playerRewards = new PlayerRewards(multiplier);
		this.multiplier = multiplier;
	}
	
	private void checkRewards() {
		// Are any rewards negative
		if (rewardType == RewardTypes.EXPERIENCE && hasNegativeRewards()) {
			logger.printWarning(this, 
					"Cannot use negative rewards with the experience reward type.");
		}
	}
	
	private boolean hasNegativeRewards() {
		
		return hasNegativeRewards(experienceDrop.getValues()) ||
				hasNegativeRewards(simpleBlockReward.getValues()) ||
				hasNegativeRewards(simpleBonusReward.getValues()) ||
				hasNegativeRewards(simplePlacingReward.getValues()) ||
				hasNegativeRewards(simpleSmeltingReward.getValues()) ||
				hasNegativeRewards(simpleCraftingReward.getValues()) ||
				hasNegativeRewards(simpleBrewingReward.getValues()) ||
				hasNegativeRewards(complexBrewingReward.getValues()) ||
				hasNegativeRewards(playerRewards.getValues());
	}
	
	private boolean hasNegativeRewards(Collection<Range> values) {
		
		// Check every range
		for (Range range : values) {
			if (range.getStart() < 0 || range.getEnd() < 0)
				return true;
		}
		
		return false;
	}
	
	private RewardTypes loadReward(String text) {
	
		try {
			return RewardTypes.valueOf(Utility.getEnumName(text));

		} catch (IllegalArgumentException e) {
			logger.printWarning(this, "Cannot parse reward type: %s", text);
			return RewardTypes.EXPERIENCE;
		}
	}
	
	private void loadMobs(ConfigurationSection config) {
		// Guard against null
		if (config == null)
			return;
		
		for (String key : config.getKeys(false)) {
			try {				
				Range value = readRange(config, key, null);
				MobQuery query = mobParser.parse(key);
				
				if (value != null)
					experienceDrop.put(query, value);
				else
					logger.printWarning(this, "Unable to parse range/value on entity %s.", key);
				
			} catch (ParsingException ex) {
				logger.printWarning(this, "Parsing error - %s", ex.getMessage());
			}
		}
	}
	
	private void loadItemActions(ConfigurationSection config) {
		// Guard against null
		if (config == null)
			return;
		
		for (String key : config.getKeys(false)) {
			try {
				Query item = itemParser.parse(key);
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
						logger.printWarning(this, "Unrecogized action %s under item %s.", action, key);
					}
				}

			} catch (ParsingException ex) {
				logger.printWarning(this, "Cannot parse item %s - %s", key, ex.getMessage());
			}
		}
	}
	
	private void loadGenericRewards(ConfigurationSection config) {
		// Guard against null
		if (config == null)
			return;
		
		for (String key : config.getKeys(false)) {
			
			try {
				Range value = readRange(config, key, null);
				
				if (value != null)
					playerRewards.put(key, value);
				else
					logger.printWarning(this, "Unable to parse range on player reward %s.", key);
				
				
			} catch (ParsingException ex) {
				logger.printWarning(this, "Parsing error - %s", key, ex.getMessage());
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
			destination.put(item, range);
		} else {
			logger.printWarning(this, "Unable to read range on %s.", key);
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
	
	@Override
	public Configuration withMultiplier(double newMultiplier) {
		return new Configuration(this, newMultiplier);
	}

	@Override
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
	
	public PlayerRewards getPlayerRewards() {
		return playerRewards;
	}
	
	public RewardTypes getRewardType() {
		return rewardType;
	}
	
	public void setRewardType(RewardTypes rewardType) {
		this.rewardType = rewardType;
	}
	
	public Rewardable getRewardManager() {
		return rewardManager;
	}

	public void setRewardManager(Rewardable rewardManager) {
		this.rewardManager = rewardManager;
	}
}
