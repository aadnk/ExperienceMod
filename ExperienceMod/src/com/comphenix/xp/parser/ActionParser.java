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

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import com.comphenix.xp.Action;
import com.comphenix.xp.Range;
import com.comphenix.xp.messages.Message;
import com.comphenix.xp.rewards.RewardProvider;

/**
 * Represents a parser that will convert item configuration sections into action objects.
 * 
 * @author Kristian
 */
public class ActionParser extends ConfigurationParser<Action> {

	// The current global action ID
	private static int currentID;
	
	private static final String messageTextSetting = "message";
	private static final String messageChannelSetting = "channels";

	private RewardProvider provider;

	public ActionParser(RewardProvider provider) {
		this.provider = provider;
	}
	
	public Action parse(ConfigurationSection input, String key) throws ParsingException {
		
		if (input == null)
			return null;

		Action result = new Action();
		Range topLevel = readRange(input, key, null);
		
		String text = null;
		List<String> channels = null;
		
		// This is a default range value
		if (topLevel != null) {
			result.addReward(provider.getDefaultName(), topLevel);
			result.setId(currentID++);
			return result;
		}
		
		ConfigurationSection values = input.getConfigurationSection(key);
		
		// See if this is a configuration section
		if (values == null)
			return null;
		
		// If not, get sub-rewards
		for (String sub : values.getKeys(false)) {
			
			if (sub.equalsIgnoreCase(messageTextSetting)) {
				text = values.getString(sub);
			} else if (sub.equalsIgnoreCase(messageChannelSetting)) {
				channels = readStrings(values, sub);
			} else {
				Range range = readRange(values, sub, null);
				
				if (range != null) {
					result.addReward(sub, range);
				}
			}
			
		}
		
		// Add message
		if (text != null) {
			Message message = new Message();
			message.setText(text);
			message.setChannels(channels);
			result.setMessage(message);
		}
		
		result.setId(currentID++);
		return result;
	}
	
	public List<String> readStrings(ConfigurationSection config, String key) {
		
		List<String> result = new ArrayList<String>();
		
		// Retrieve string elements
		if (config.isString(key))
			result.add(config.getString(key));
		else if (config.isList(key))
			result.addAll(config.getStringList(key));
		else
			return null; // No data
		
		return result;
	}
	
	private Range readRange(ConfigurationSection config, String key, Range defaultValue) {
		
		String start = key + ".first";
		String end = key + ".last";
		
		if (config.isDouble(key)) {
			return new Range(config.getDouble(key));
			
		} else if (config.isInt(key)) {
			return new Range((double) config.getInt(key));
			
		} else if (config.contains(start) && config.contains(end)) {
			return new Range(config.getDouble(start), config.getDouble(end));
	
		} else if (config.isList(key)) {
			// Try to get a double list
			List<Double> attempt = config.getDoubleList(key);

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
	
	public static int getCurrentID() {
		return currentID;
	}
	
	public static void setCurrentID(int id) {
		currentID = id;
	}
}
