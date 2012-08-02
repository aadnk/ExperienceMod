package com.comphenix.xp.parser;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import com.comphenix.xp.Range;

/**
 * Responsible for reading ranges. 
 * 
 * @author Kristian
 */
public class RangeParser extends ConfigurationParser<Range> {
	
	@Override
	public Range parse(ConfigurationSection input, String key) throws ParsingException {

		Range result = parse(input, key, null);
		
		// Turn default value into an exception
		if (result != null)
			return result;
		else
			throw ParsingException.fromFormat("Cannot parse range from key %s.", key);
	}
	
	/**
	 * Transform a configuration object, represented by a key, into a range. 
	 * @param input - the configuration section in which the key to the object is stored.
	 * @param key - the key of the object.
	 * @param defaultValue - value to return if parsing failed.
	 * @return A range from the given configuration object, or defaultValue if failed.
	 */
	public Range parse(ConfigurationSection input, String key, Range defaultValue) {
		
		String start = key + ".first";
		String end = key + ".last";
		
		if (input.isDouble(key)) {
			return new Range(input.getDouble(key));
			
		} else if (input.isInt(key)) {
			return new Range((double) input.getInt(key));
			
		} else if (input.contains(start) && input.contains(end)) {
			return new Range(input.getDouble(start), input.getDouble(end));
	
		} else if (input.isList(key)) {
			// Try to get a double list
			List<Double> attempt = input.getDoubleList(key);

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
}
