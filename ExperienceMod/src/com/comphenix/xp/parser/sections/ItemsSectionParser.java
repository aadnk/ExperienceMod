package com.comphenix.xp.parser.sections;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.configuration.ConfigurationSection;

import com.comphenix.xp.Action;
import com.comphenix.xp.ActionTypes;
import com.comphenix.xp.expressions.ParameterProviderSet;
import com.comphenix.xp.lookup.ItemTree;
import com.comphenix.xp.lookup.PotionTree;
import com.comphenix.xp.lookup.Query;
import com.comphenix.xp.lookup.SearchTree;
import com.comphenix.xp.lookup.Query.Types;
import com.comphenix.xp.parser.ActionParser;
import com.comphenix.xp.parser.ParsingException;
import com.comphenix.xp.parser.text.ItemParser;

public class ItemsSectionParser extends SectionParser<ItemsSectionResult> {

	protected ItemParser itemParser;
	protected ActionParser actionParser;
	protected ActionTypes actionTypes;
	protected ParameterProviderSet parameterProviders;
	protected double multiplier;
	
	public ItemsSectionParser(ItemParser itemParser, ActionParser actionParser, ActionTypes types, 
							  ParameterProviderSet parameterProviders, double multiplier) {
		
		this.itemParser = itemParser;
		this.actionParser = actionParser;
		this.actionTypes = types;
		this.parameterProviders = parameterProviders;
		this.multiplier = multiplier;
	}

	@Override
	public ItemsSectionResult parse(ConfigurationSection input, String sectionName)
			throws ParsingException {
		
		if (input == null)
			throw new NullArgumentException("input");
		
		// Create parsers with the given named parameters
		String[] blockNames = parameterProviders.getBlockParameters().getParameterNames();
		String[] itemNames = parameterProviders.getItemParameters().getParameterNames();
		ActionParser actionBlockParser = actionParser.createView(blockNames);
		ActionParser actionItemParser = actionParser.createView(itemNames);
		
		Map<Integer, ItemTree> actionRewards = new HashMap<Integer, ItemTree>();
		Map<Integer, PotionTree> complexRewards = new HashMap<Integer, PotionTree>();
		ItemsSectionResult result = new ItemsSectionResult(actionRewards, complexRewards);
		
		// Initialize all the default rewards
		for (Integer types : actionTypes.getTypes()) {
			actionRewards.put(types, new ItemTree(multiplier));
			complexRewards.put(types, new PotionTree(multiplier));
		}
		
		// Null is handled as the root
		if (sectionName != null) {
			input = input.getConfigurationSection(sectionName);
			
			// No rewards found
			if (input == null)
				return result;
		}
		
		// Load keys
		for (String key : input.getKeys(false)) {
			try {
				Query item = itemParser.parse(key);
				ConfigurationSection itemSection = input.getConfigurationSection(key);
				Types queryType = item.getQueryType();
				
				// Read the different rewards
				for (String action : itemSection.getKeys(false)) {
					
					Integer type = actionTypes.getType(action);
					
					if (type == null) {
						// Catch some misunderstanding here
						if (action.equalsIgnoreCase("message") || action.equalsIgnoreCase("channels")) {
							throw ParsingException.fromFormat( 
								"Message and channel list must be inside an action (block, smelting, ect.).");
						} else {
							throw ParsingException.fromFormat( 
								"Unrecogized action %s.", action);
						}
					}
					
					// Select the correct parser for the job
					ActionParser parser = actionTypes.isItemAction(action) ? actionItemParser : actionBlockParser;

					// Handle the special case of potion queries
					switch (queryType) {
					case ITEMS:
						loadActionOnItem(parser, itemSection, action, item, result.getActionReward(type), queryType);
						break;
						
					case POTIONS:
						loadActionOnItem(parser, itemSection, action, item, result.getComplexReward(type), queryType);
						break;
						
					default:
						throw ParsingException.fromFormat("The query type %s cannot be used on items.", queryType);
					}
				}

			} catch (ParsingException ex) {
				if (isCollectExceptions()) {
					// For now, record it
					debugger.printWarning(this, "Cannot parse item %s - %s", key, ex.getMessage());
				} else {
					// Just invoke the error
					throw ex;
				}
			}
		}
		
		return result;
	}
	
	// I just wanted handle SearchTree<ItemQuery, Range> and SearchTree<PotionQuery, Range> with the same method, but
	// apparently you can't simply use SearchTree<Query, Range> or some derivation to match them both. Too bad.
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void loadActionOnItem(ActionParser parser, ConfigurationSection config, String key, Query item, 
								  SearchTree destination, Query.Types checkType) throws ParsingException  {
		
		Action range = parser.parse(config, key);
		
		// Check the query type
		if (item.getQueryType() != checkType)
			throw new IllegalArgumentException("Cannot load action " + key + " on this item matcher.");
		
		// Ignore this type
		if (range != null) {
			destination.put(item, range);
		} else {
			throw ParsingException.fromFormat("Unable to read range on %s.", key);
		}
	}
}
