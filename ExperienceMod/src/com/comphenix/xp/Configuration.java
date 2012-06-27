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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import com.comphenix.xp.lookup.*;
import com.comphenix.xp.lookup.Query.Types;
import com.comphenix.xp.parser.ActionParser;
import com.comphenix.xp.parser.Utility;
import com.comphenix.xp.parser.ParsingException;
import com.comphenix.xp.parser.text.ItemParser;
import com.comphenix.xp.parser.text.MobParser;
import com.comphenix.xp.rewards.RewardProvider;
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
	
	private static final String economyDropsSetting = "economy drop";
	private static final String economyWorthSetting = "economy drop worth";
	private static final String virtualScanRadiusSetting = "virtual scan radius";
	
	private static final double defaultScanRadius = 20;
	
	private Debugger logger;
	
	private double multiplier;
	private boolean defaultRewardsDisabled;
	private boolean preset;
	
	private ItemStack economyDropItem;
	private Integer economyItemWorth;
	private double scanRadiusSetting;
	
	private RewardProvider rewardProvider;

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
	private ActionParser actionParser;
	
	public Configuration(Debugger debugger) {
		this.logger = debugger;
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
		this.economyItemWorth = other.economyItemWorth;
		this.economyDropItem = other.economyDropItem;
		this.scanRadiusSetting = other.scanRadiusSetting;
		
		if (other.rewardProvider != null) {
			this.rewardProvider = other.rewardProvider.createView(this);
		}
		
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
	
	public Configuration(ConfigurationSection config, Debugger debugger, RewardProvider provider) {
		this.logger = debugger;
		this.rewardProvider = provider;
		this.actionParser = new ActionParser(provider);
		loadFromConfig(config);
	}
	
	/**
	 * Merge a list of configurations into a new configuration.
	 * 
	 * @param configurations - list of configurations.
	 * @param debugger - debugger instance.
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
			copy.rewardProvider = config.rewardProvider;
			copy.economyItemWorth = config.economyItemWorth;
			copy.economyDropItem = config.economyDropItem;
			copy.scanRadiusSetting = config.scanRadiusSetting;
			
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
		scanRadiusSetting = readDouble(config, virtualScanRadiusSetting, defaultScanRadius);
		
		economyItemWorth = config.getInt(economyWorthSetting, 1);
		economyDropItem = null;
		
		// Economy drop item
		try {
			String text = config.getString(economyDropsSetting, null);
			Query drop = text != null ? itemParser.parse(text) : null;
			
			if (drop != null && drop instanceof ItemQuery) {
				economyDropItem = ((ItemQuery) drop).toItemStack(1);
			}
			
		} catch (ParsingException e) {
			logger.printWarning(this, "Cannot load economy drop type: %s", e.getMessage());
		}
		
		// Load reward type
		String defaultReward = loadReward(config.getString(rewardTypeSetting, null));
		
		// Use default type if nothing has been set
		if (defaultReward != null)
			setDefaultRewardName(defaultReward);
		
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
	
	public Collection<Action> getActions() { 
		
		List<Action> actions = new ArrayList<Action>();
		
		// Copy the content of every single collection
		actions.addAll(experienceDrop.getValues());
		actions.addAll(simpleBlockReward.getValues());
		actions.addAll(simpleBonusReward.getValues());
		actions.addAll(simplePlacingReward.getValues());
		actions.addAll(simpleSmeltingReward.getValues());
		actions.addAll(simpleCraftingReward.getValues());
		actions.addAll(simpleBrewingReward.getValues());
		actions.addAll(complexBrewingReward.getValues());
		actions.addAll(playerRewards.getValues());
		return actions;
	}
	
	private void checkRewards() {
		
		Collection<Action> actions = getActions();
		
		// Are any rewards negative
		if (hasNegativeRewards(actions)) {
			logger.printWarning(this, 
					"Cannot use negative rewards with the experience reward type.");
		}
		
		// Economy rewards when no economy is registered
		if (rewardProvider.getByEnum(RewardTypes.ECONOMY) == null &&
			hasEconomyReward(actions)) {
			logger.printWarning(this, "VAULT was not found. Cannot use economy.");
		}
	}

	private boolean hasEconomyReward(Collection<Action> values) {
		
		// See if we have an economy reward set
		for (Action action : values) {
			if (action.getReward("ECONOMY") != null)
				return true;
		}
		
		return false;
	}
	
	private boolean hasNegativeRewards(Collection<Action> values) {
		
		// Check every range
		for (Action action : values) {
			
			Range range = action.getReward("EXPERIENCE");
				
			if (range != null && (range.getStart() < 0 || range.getEnd() < 0))
				return true;
		}
		
		return false;
	}
	
	private String loadReward(String text) {
	
		String parsing = Utility.getEnumName(text);
			
		// Load reward name
		if (text != null && rewardProvider.containsService(parsing)) {
			return parsing;
		} else {
			return null;
		}
	}
	
	private void loadMobs(ConfigurationSection config) {
		// Guard against null
		if (config == null)
			return;
		
		for (String key : config.getKeys(false)) {
			try {				
				Action value = actionParser.parse(config, key);
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
				Action value = actionParser.parse(config, key);
				
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
	private void loadActionOnItem(ConfigurationSection config, String key, Query item, SearchTree destination, Query.Types checkType) throws ParsingException  {
		
		Action range = actionParser.parse(config, key);
		
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

	/**
	 * Reads a double or integer from the configuration section.
	 * @param config - configuration section to read from.
	 * @param key - the key to read.
	 * @return The double, or NULL if none were found.
	 */
	private double readDouble(ConfigurationSection config, String key, double defaultValue) {
		
		if (config.isDouble(key))
			return config.getDouble(key);
		else if (config.isInt(key)) 
			return (double) config.getInt(key);
		else 
			return defaultValue;
	}
	
	/**
	 * Whether or not this configuration is associated with a specified preset.
	 * @return TRUE if it is, FALSE otherwise. 
	 */
	public boolean hasPreset() {
		return preset;
	}
	
	/**
	 * Whether or not this configuration is associated with a specified preset.
	 * @param value - new value.
	 */
	public void setPreset(boolean value) {
		preset = value;
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
	
	public RewardProvider getRewardProvider() {
		return rewardProvider;
	}
	
	public void setRewardManager(RewardProvider rewardProvider) {
		this.rewardProvider = rewardProvider;
	}
	
	public ItemStack getEconomyDropItem() {
		return economyDropItem;
	}

	public Integer getEconomyItemWorth() {
		return economyItemWorth;
	}
	
	public String getDefaultRewardName() {
		if (rewardProvider == null)
			return null;
		else
			return rewardProvider.getDefaultService();
	}
	
	public void setDefaultRewardName(String rewardName) {
		if (rewardProvider != null) {
			rewardProvider.setDefaultService(rewardName);
		}
	}
}
