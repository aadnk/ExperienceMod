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

package com.comphenix.xp.messages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.comphenix.xp.parser.Utility;
import com.comphenix.xp.rewards.ResourceHolder;

public class MessageFormatter {

	private Player source;
	private Integer count;
	
	private Collection<ResourceHolder> result;
	private List<ResourceHolder> generated;
	
	private static Pattern parameterPattern = Pattern.compile("\\{\\w+\\}");
	
	// Default
	public MessageFormatter() {
		setCount(1);
	}

	public MessageFormatter(Player player, Collection<ResourceHolder> result, List<ResourceHolder> generated) {
		this(player, result, generated, 1);
	}
	
	public MessageFormatter(Player player, Collection<ResourceHolder> result, 
							List<ResourceHolder> generated, Integer count) {
		setSource(player);
		setResult(result);
		setGenerated(generated);
		setCount(count);
	}
	
	/**
	 * Replaces parameters in the text with their respective value.
	 * @param message - message to format.
	 * @return Message with every parameter replaced with the corresponding value.
	 */
	public String formatMessage(String message) {
		
		if (message == null)
			return null;
		
		Map<String, ResourceHolder> lookup = getResultMapping();
		
		StringBuffer output = new StringBuffer();
	    Matcher matcher = parameterPattern.matcher(message);
	    
	    // Simple variables
	    // TODO: Add more variables.
	    String sourceText = source != null ? source.getDisplayName() : "Unknown";
	    String countText = count != null ? count.toString() : "N/A";
	    
	    while (matcher.find()) {

	    	String enumed = Utility.getEnumName(matcher.group());
	    	
	    	// Replace parameters
	    	if (enumed.equals("PLAYER"))
	    		matcher.appendReplacement(output, sourceText);
	    	else if (enumed.equals("COUNT"))
	    		matcher.appendReplacement(output, countText);
	    	else if (lookup.containsKey(enumed)) 
	    		matcher.appendReplacement(output, lookup.get(enumed).toString());
	    	else 
	    		matcher.appendReplacement(output, "{CANNOT FIND " + matcher.group() + "}");
	    }

		// Remember color
		matcher.appendTail(output);
		return formatUnescape(formatColor(output));
	}

	private String formatColor(StringBuffer input) {

		// Treat ampersand as a color character
		return translateAlternateColorCodes('&', input.toString());
	}

	private String formatUnescape(String input) {

		return StringEscapeUtils.unescapeJava(input);
	}

	// Don't translate color codes when the ampersand is escaped
	private static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {

		boolean hasEscape = false;
		char[] b = textToTranslate.toCharArray();

		// Handle Java escaping as well
		for (int i = 0; i < b.length - 1; i++) {
			if (!hasEscape && b[i] == '\\') {
				hasEscape = true;
			} else if (!hasEscape && b[i] == altColorChar
					&& "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
				b[i] = ChatColor.COLOR_CHAR;
				b[i + 1] = Character.toLowerCase(b[i + 1]);

			} else {
				hasEscape = false;
			}
		}
		return new String(b);
	}

	// Convert a list of resources into a Hash Table
	private Map<String, ResourceHolder> getResultMapping() {
		 Map<String, ResourceHolder> lookup = new HashMap<String, ResourceHolder>();
		 
		 for (ResourceHolder resource : getResult()) {
			 lookup.put(resource.getName(), resource);
		 }
		 
		 return lookup;
	}
	
	/**
	 * Retrieves the number of times this message has been generated since it was last transmitted.
	 * @return The number of outstanding messages of this type.
	 */
	public Integer getCount() {
		return count;
	}

	/**
	 * Sets the number of times this message has been generated since it was last transmitted.
	 * @param count - new count.
	 */
	public void setCount(Integer count) {
		this.count = count;
	}

	/**
	 * Retrieves the player that caused this message to be sent, or NULL if it was caused by the environment.
	 * @return The player, if any, that caused the message to be sent.
	 */
	public Player getSource() {
		return source;
	}

	/**
	 * Sets the player that caused this message to be sent.
	 * @param source - the player that caused the message, or NULl it if was the enviornment.
	 */
	public void setSource(Player source) {
		this.source = source;
	}
	
	/**
	 * Retrieves the list of resources awarded.
	 * @return List of resources.
	 */
	public Collection<ResourceHolder> getResult() {
		return result;
	}

	/**
	 * Sets the list of resources awarded.
	 * @param result - the new list of resources awarded.
	 */
	public void setResult(Collection<ResourceHolder> result) {
		this.result = result;
	}
	
	/**
	 * Retrieves the list of resources generated, ordered by reward providers.
	 * <p>
	 * This list is the sum of all previous lists.
	 * @return The current list.
	 */
	public List<ResourceHolder> getGenerated() {
		return generated;
	}

	/**
	 * Sets the list of resources generated, ordered by reward providers.
	 * @param generated - new list of resources.
	 */
	public void setGenerated(List<ResourceHolder> generated) {
		this.generated = generated;
	}
	
	/**
	 * Adds every parameter in both message formatters. Note that a and b
	 * must be non-null and have the same player source.
	 * @param a - first message formatter to add.
	 * @param b - second message formatter to add.
	 * @return The resulting message formatter.
	 */
	public static MessageFormatter add(MessageFormatter a, MessageFormatter b) {
		if (a == null)
			throw new NullArgumentException("a");
		if (b == null)
			throw new NullArgumentException("b");
		
		// Add the two formatters
		return a.add(b);
	}
	
	/**
	 * Adds every parameter in both message formatters, creating a new message 
	 * formatter with the results.
	 * @param other - message formatter to add.
	 * @return The resulting message formatter.
	 */
	public MessageFormatter add(MessageFormatter other) {
		
		if (!ObjectUtils.equals(getSource(), other.getSource()))
			throw new IllegalArgumentException("Message formatters for different players cannot be added.");
		
		// Add values
		return new MessageFormatter(
				getSource(), 
				addResults(other),
				addGenerated(other),
				getInt(getCount()) + getInt(other.getCount())
		);
	}
		
	// Merge resources
	private Collection<ResourceHolder> addResults(MessageFormatter other) {
		
		Map<String, ResourceHolder> current = getResultMapping();

		// Add every resource from other
		for (ResourceHolder resource : other.getResult()) {
			String name = resource.getName();
			
			if (current.containsKey(name))
				current.put(name, current.get(name).add(resource));
			else
				current.put(name, resource);
		}
		
		return current.values();
	}
	
	// Merge generated
	private List<ResourceHolder> addGenerated(MessageFormatter other) {
		
		List<ResourceHolder> current = new ArrayList<ResourceHolder>(getGenerated());
		List<ResourceHolder> adding = other.getGenerated();
		
		// Add each resource
		for (int i = 0; i < adding.size(); i++) {
			// Elements outside the list are treated as empty
			if (i < current.size())
				current.set(i, current.get(i).add(adding.get(i)));
			else
				current.add(adding.get(i));
		}
		
		return current;
	}
	
	private static int getInt(Integer value) {
		return value != null ? value : 0;
	}
	
	/**
	 * Create a copy of the message formatter with the given parameters
	 * @param player - the player that caused the current action.
	 * @param result - combined resources after awarding a player.
	 * @param generated - the generated list of resources, in the same order as the reward providers.
	 * @return A copy of the current message formatter.
	 */
	public MessageFormatter createView(Player player, Collection<ResourceHolder> result, List<ResourceHolder> generated) {
		return new MessageFormatter(player, result, generated);
	}
	
	/**
	 * Create a copy of the message formatter with the given parameters
	 * @param player - the player that caused the current action.
	 * @param result - combined resources after awarding a player.
	 * @param generated - the generated list of resources, in the same order as the reward providers.
	 * @param count - number of times the message has been sent.
	 * @return A copy of the current message formatter.
	 */
	public MessageFormatter createView(Player player, Collection<ResourceHolder> result, List<ResourceHolder> generated, Integer count) {
		return new MessageFormatter(player, result, generated, count);
	}
	
	/**
	 * Create a copy of the message formatter with the given parameters
	 * @param result - combined resources after awarding a player.
	 * @param generated - the generated list of resources, in the same order as the reward providers.
	 * @return A copy of the current message formatter.
	 */
	public MessageFormatter createView(Collection<ResourceHolder> result, List<ResourceHolder> generated) {
		return createView(source, result, generated, count);
	}
}
