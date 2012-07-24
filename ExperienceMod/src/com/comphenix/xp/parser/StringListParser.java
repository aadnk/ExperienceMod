package com.comphenix.xp.parser;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

/**
 * Converts a configuration object into a list of strings.
 * 
 * @author Kristian
 */
public class StringListParser extends ConfigurationParser<List<String>> {

	@Override
	public List<String> parse(ConfigurationSection config, String key) throws ParsingException {
		
		List<String> result = parseSafe(config, key);
		
		// Check for no results
		if (result != null)
			return result;
		else
			throw ParsingException.fromFormat("Unable to parse string list %s.", key);
	}
	
	public List<String> parseSafe(ConfigurationSection config, String key) {
		
		List<String> result = new ArrayList<String>();
		
		// Retrieve string elements
		if (config.isString(key))
			result.add(config.getString(key));
		else if (config.isList(key))
			result.addAll(config.getStringList(key));
		else
			return null;
		
		return result;
	}
}
