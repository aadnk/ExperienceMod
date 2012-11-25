/*
 *  ExperienceMod - Bukkit server plugin for modifying the experience system in Minecraft.
 *  Copyright (C) 2012 Kristian S. Stangeland
 *
 *  This program is free software; you can redistribute it and/or modify it under the terms of the 
 *  GNU General Public License as published by the Free Software Foundation; either version 2 of 
 *  the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with this program; 
 *  if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 
 *  02111-1307 USA
 */

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

	/**
	 * Transforms a configuration object, represented by a key, into an object. 
	 * @param input - the configuration section in which the object is stored.
	 * @param key - the key of the configuration object.
	 * @param defaultValue - value to return if the parsing failed.
	 * @return A transformed object, or default value if the parsing failed.
	 */
	public TOutput parse(ConfigurationSection input, String key, TOutput defaultValue) {
		
		// Convert exceptions into default values
		try {
			return parse(input, key);
		} catch (ParsingException e) {
			return defaultValue;
		}
	}
}
