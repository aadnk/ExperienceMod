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

import java.util.Collection;
import java.util.HashMap;
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
	
	private static Pattern parameterPattern = Pattern.compile("\\{\\w+\\}");
	
	// Default
	public MessageFormatter() {
		setCount(1);
	}

	public MessageFormatter(Player player, Collection<ResourceHolder> result) {
		this(player, result, 1);
	}
	
	public MessageFormatter(Player player, Collection<ResourceHolder> result, Integer count) {
		setSource(player);
		setResult(result);
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
	
	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Player getSource() {
		return source;
	}

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
	
	private static int getInt(Integer value) {
		return value != null ? value : 0;
	}
	
	// Create a copy of the message formatter with the given parameters
	public MessageFormatter createView(Player player, Collection<ResourceHolder> result) {
		return new MessageFormatter(player, result);
	}
	
	// Create a copy of the message formatter with the given parameters
	public MessageFormatter createView(Player player, Collection<ResourceHolder> result, Integer count) {
		return new MessageFormatter(player, result, count);
	}
	
	// Create a copy of the message formatter with the given parameters
	public MessageFormatter createView(Collection<ResourceHolder> result) {
		return createView(null, result);
	}
}
