package com.comphenix.xp.rewards.xp;

import org.bukkit.configuration.ConfigurationSection;

import com.comphenix.xp.expressions.VariableFunction;
import com.comphenix.xp.parser.ParsingException;
import com.comphenix.xp.parser.RangeParser;
import com.comphenix.xp.parser.text.ExpressionParser;
import com.comphenix.xp.rewards.ResourceFactory;
import com.comphenix.xp.rewards.ResourcesParser;

public class CurrencyParser extends ResourcesParser {

	protected RangeParser rangeParser;
	
	public CurrencyParser(String[] namedParameters) {
		rangeParser = new RangeParser(new ExpressionParser(namedParameters));
	}
	
	@Override
	public ResourceFactory parse(ConfigurationSection input, String key) throws ParsingException {

		VariableFunction range = rangeParser.parse(input, key);
		
		// Handle the NULL case too
		if (range != null) {
			return new CurrencyFactory(range);
		} else {
			return null;
		}
	}

	@Override
	public ResourcesParser withParameters(String[] namedParameters) {
		return new CurrencyParser(namedParameters);
	}
}
