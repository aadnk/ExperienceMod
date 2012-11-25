package com.comphenix.xp.parser;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Parses a double field from a configuration file.
 * 
 * @author Kristian
 */
public class DoubleParser extends ConfigurationParser<Double> {

	@Override
	public Double parse(ConfigurationSection input, String key) throws ParsingException {

		if (input == null)
			throw new NullArgumentException("input");
		if (key == null)
			throw new NullArgumentException("key");
		
		return parseDouble(input, key, null, true);
	}
	
	@Override
	public Double parse(ConfigurationSection input, String key, Double defaultValue) {

		// Just return the defaults
		if (input == null || key == null)
			return defaultValue;
		
		try {
			return parseDouble(input, key, defaultValue, false);
		} catch (ParsingException e) {
			// If this occurs, something REALLY bad has happened
			throw new IllegalStateException("Unexpected error occured: " + e.getMessage(), e);
		}
	}
	
	private Double parseDouble(ConfigurationSection input, String key, Double defaultValue, boolean throwIfError) throws ParsingException  {
		Object value = input.get(key, null);
		
		// Handle every conceivable case
		if (value instanceof Double)
			return (Double) value;
		else if (value instanceof Integer)
			return ((Integer) value).doubleValue();
		else if (value == null) {
			// This is because the key doesn't exist
			if (throwIfError)
				throw ParsingException.fromFormat("Cannot find double %s.", key);
			else
				return defaultValue;
			
		} else {
			// We got something else instead. BARK!
			if (throwIfError)
				throw ParsingException.fromFormat("The key %s should be a number, but is a %s instead.", 
						key, value.getClass().getName());
			else
				return defaultValue;
		}
	}
}
