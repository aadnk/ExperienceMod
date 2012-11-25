package com.comphenix.xp.rewards;

import com.comphenix.xp.parser.ConfigurationParser;

/**
 * Represents a generic resource parser that transforms configuration sections into 
 * resource factories.
 * 
 * @author Kristian
 */
public abstract class ResourcesParser extends ConfigurationParser<ResourceFactory> {
	
	/**
	 * Constructs a new resource parser with the given parameters.
	 * @param namedParameters - new list of named parameters to accept.
	 * @return New resource parser.
	 */
	public abstract ResourcesParser withParameters(String[] namedParameters);
}
