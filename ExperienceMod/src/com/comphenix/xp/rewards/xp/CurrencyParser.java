package com.comphenix.xp.rewards.xp;

import org.bukkit.configuration.ConfigurationSection;

import com.comphenix.xp.SampleRange;
import com.comphenix.xp.parser.ParsingException;
import com.comphenix.xp.parser.RangeParser;
import com.comphenix.xp.rewards.ResourceFactory;
import com.comphenix.xp.rewards.ResourcesParser;

public class CurrencyParser extends ResourcesParser {

	protected RangeParser rangeParser = new RangeParser();
	
	@Override
	public ResourceFactory parse(ConfigurationSection input, String key) throws ParsingException {

		SampleRange range = rangeParser.parse(input, key, null);
		
		// Handle the NULL case too
		if (range != null) {
			return new CurrencyFactory(range);
		} else {
			return null;
		}
	}
}
