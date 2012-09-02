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

import java.util.List;
import java.util.concurrent.Callable;

import org.bukkit.configuration.ConfigurationSection;

import com.comphenix.xp.Action;
import com.comphenix.xp.messages.Message;
import com.comphenix.xp.rewards.ResourceFactory;
import com.comphenix.xp.rewards.ResourcesParser;
import com.comphenix.xp.rewards.RewardProvider;

/**
 * Represents a parser that will convert item configuration sections into action objects.
 * 
 * @author Kristian
 */
public class ActionParser extends ConfigurationParser<Action> {

	// The current global action ID
	private static int currentID;
	
	private static final String MESSAGE_TEXT_SETTING = "message";
	private static final String MESSAGE_CHANNEL_SETTING = "channels";
	private static final String MULTIPLIER_SETTING = "multiplier";
	private static final String INHERIT_SETTING = "inherit";
	
	protected StringListParser listParser = new StringListParser();
	protected DoubleParser doubleParser = new DoubleParser();
	protected RewardProvider provider;
	protected Callable<Action> previousAction;
	
	// The named parameters to supply the parser
	protected String[] namedParameters;
	
	public ActionParser(RewardProvider provider) {
		this.provider = provider;
	}
	
	public ActionParser(RewardProvider provider, String[] namedParameters) {
		this.provider = provider;
		this.namedParameters = namedParameters;
	}

	public Action parse(ConfigurationSection input, String key) throws ParsingException {
		
		if (input == null)
			throw ParsingException.fromFormat("Configuration section cannot be null.");

		Action result = new Action();
		String defaultName = provider.getDefaultName();
		
		String text = null;
		List<String> channels = null;
		boolean seenInherit = false;
		
		// See if this is a top level reward
		if (provider.containsService(defaultName)) {
			
			ResourcesParser parser = provider.getDefaultService().getResourcesParser(namedParameters);
			
			if (parser != null) {
				try {
					ResourceFactory factory = parser.parse(input, key);
					
					// This is indeed a top level reward
					if (factory != null) {
						result.addReward(defaultName, factory);
						result.setId(currentID++);
						return result;
					}
				
				} catch (ParsingException e) {
					// See if it contains multiple rewards
					if (!input.isConfigurationSection(key)) {
						// If not, this error should propagate.
						throw e;
					}
				}
			}
		}
		
		ConfigurationSection values = input.getConfigurationSection(key);
		
		// See if this is a configuration section
		if (values == null) {
			return null;
		}

		// Next, get sub-rewards
		for (String sub : values.getKeys(false)) {
			
			String enumed = Utility.getEnumName(sub);
			
			if (sub.equalsIgnoreCase(MESSAGE_TEXT_SETTING)) {
				text = values.getString(sub);
				
			} else if (sub.equalsIgnoreCase(MESSAGE_CHANNEL_SETTING)) {
				channels = listParser.parseSafe(values, sub);
				
			} else if (provider.containsService(enumed)) {
				ResourcesParser parser = provider.getByName(enumed).getResourcesParser(namedParameters);
				
				if (parser != null) {
					ResourceFactory factory = parser.parse(values, sub);
				
					if (factory != null) 
						result.addReward(sub, factory);
				} else {
					// This is bad
					throw ParsingException.fromFormat("Parser in %s cannot be NULL.", sub);
				}

			} else if (sub.equalsIgnoreCase(MULTIPLIER_SETTING)) {
				
				result.setInheritMultiplier(doubleParser.parse(values, sub));
				
				// Assume inheritance if not otherwise specified
				if (!seenInherit) {
					result.setInheritance(true);
				}
				
			} else if (sub.equalsIgnoreCase(INHERIT_SETTING)) {
				
				Object value = values.get(sub);
				
				// Handle the inheritance field
				if (value instanceof Boolean) {
					result.setInheritance((Boolean) value);
					seenInherit = true;
				} else {
					throw ParsingException.fromFormat("The value %s is not a boolean. Must be TRUE/FALSE:", value);
				}
				
			} else {
				throw ParsingException.fromFormat("Unrecognized reward %s.", sub);
			}
		}
		
		// Add message
		if (text != null) {
			Message message = new Message();
			message.setText(text);
			message.setChannels(channels);
			result.setMessage(message);
		}
		
		// Apply this multiplier to the action itself
		if (result.getInheritMultiplier() != 1) {
			result = result.multiply(result.getInheritMultiplier());
		}
		
		result.setId(currentID++);
		return result;
	}
	
	/**
	 * Creates a shallow copy of this parser with the given reward provider.
	 * @param provider - new reward provider.
	 * @return Shallow copy of this parser.
	 */
	public ActionParser createView(RewardProvider provider) {
		return new ActionParser(provider);
	}
	
	/**
	 * Creates a shallow copy of this parser with the given named parameters.
	 * @param namedParameters - new named parameters.
	 * @return Shallow copy of this parser.
	 */
	public ActionParser createView(String[] namedParameters) {
		return new ActionParser(provider, namedParameters);
	}

	/**
	 * A function that retrieves the previous action, if any, that matches this 
	 * action by its query.
	 * @return Previous action.
	 */
	public Callable<Action> getPreviousAction() {
		return previousAction;
	}

	/**
	 * Sets a function that retrieves the previous action, if any, that matches 
	 * this action by its query.
	 * @param previousAction - the function.
	 */
	public void setPreviousAction(Callable<Action> previousAction) {
		this.previousAction = previousAction;
	}

	public String[] getNamedParameters() {
		return namedParameters;
	}
	
	public static int getCurrentID() {
		return currentID;
	}
	
	public static void setCurrentID(int id) {
		currentID = id;
	}
}
