package com.comphenix.xp.parser;

import org.bukkit.configuration.ConfigurationSection;

/**
 * Represents a generic configuration parser. It converts configuration objects to objects of TOutput.
 * 
 * @author Kristian
 * @param <TOutput> - output type.
 */
public abstract class ConfigurationParser<TOutput> {

	/**
	 * Transforms a configuration object, represented by a key, into an object. 
	 * @param input - the configuration section in which the object is stored.
	 * @param key - the key of the configuration object.
	 * @return A transformed object, or NULL if the input is empty.
	 * @throws ParsingException An error occurred during the parsing.
	 */
	public abstract TOutput parse(ConfigurationSection input, String key) throws ParsingException;
}
