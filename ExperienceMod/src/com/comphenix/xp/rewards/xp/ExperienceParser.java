package com.comphenix.xp.rewards.xp;

import org.bukkit.configuration.ConfigurationSection;

import com.comphenix.xp.Range;
import com.comphenix.xp.parser.ParsingException;
import com.comphenix.xp.parser.RangeParser;
import com.comphenix.xp.rewards.ResourceFactory;
import com.comphenix.xp.rewards.ResourcesParser;

public class ExperienceParser extends ResourcesParser {

	protected RangeParser rangeParser = new RangeParser();
	
	@Override
	public ResourceFactory parse(ConfigurationSection input, String key) throws ParsingException {

		Range range = rangeParser.parse(input, key, null);
		
		// Handle the NULL case too
		if (range != null) {
			return new ExperienceFactory(range);
		} else {
			return null;
		}
	}
}
