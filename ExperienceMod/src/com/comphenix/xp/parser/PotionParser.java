package com.comphenix.xp.parser;

import java.util.List;
import java.util.Queue;

import org.apache.commons.lang.StringUtils;
import org.bukkit.potion.PotionType;

import com.comphenix.xp.lookup.PotionQuery;

public class PotionParser {

	private ParameterParser<Integer> itemNameParser = new ParameterParser<Integer>(new ItemNameParser());
	private ParameterParser<PotionType> potionTypeParser = new ParameterParser<PotionType>(new PotionTypeParser());
	private ParameterParser<Integer> tierParser = new ParameterParser<Integer>(new IntegerParser());
	
	private BooleanParser extendedParser = new BooleanParser("extended");
	private BooleanParser splashParser = new BooleanParser("splash");

	// Special potion parser
	public PotionQuery parsePotion(String text) throws ParsingException {

		Queue<String> tokens = Parsing.getParameterQueue(text);
		
		ParsingException reason = null;
		List<Integer> items = null;
		List<PotionType> types = null;
		List<Integer> tiers = null;
		
		try {
			items = itemNameParser.parse(tokens);
			types = potionTypeParser.parse(tokens);
			tiers = tierParser.parse(tokens);
			
		} catch (ParsingException ex) {
			// Wait, don't give up yet.
			reason = ex;
		}
	
		// Scan all unused parameters for these options first
		Boolean extended = extendedParser.parseAny(tokens);
		Boolean splash = splashParser.parseAny(tokens);
		
		Integer maxLevel = getMaxLevel(types);
		
		// Just assume level two is the highest
		if (maxLevel == null)
			maxLevel = 2;
		
		// Check tiers
		for (Integer tier : tiers) {
			if (tier > maxLevel) {
				throw ParsingException.fromFormat(
						"Potion level %d is too high.", tier);
			} else if (tier < 1) {
				throw ParsingException.fromFormat(
						"Potion level %d is too low.", tier);
			}
		}
		
		// If there are some tokens left, a problem occured
		if (!tokens.isEmpty()) {
			
			// Let the user know about the reason too
			if (reason != null)
				throw reason;
			else
				throw ParsingException.fromFormat("Unknown item tokens: ", StringUtils.join(tokens, ", "));
		}
		
		// Create the query
		return new PotionQuery(types, tiers, extended, splash);
	}

	private Integer getMaxLevel(List<PotionType> types) {
		
		Integer best = null;
		
		for (PotionType type : types) {
			if (best == null || best < type.getMaxLevel()) {
				best = type.getMaxLevel();
			}
		}
		
		return best;
	}
}
