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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Represents an object that transforms strings of text into objects.
 * 
 * @author Kristian
 * @param <TResult> - type of the resulting object.
 */
public abstract class TextParser<TResult> {
	/**
	 * Transforms the given text into an object. 
	 * @param text - text to parse.
	 * @return Result of the parsing.
	 * @throws ParsingException The text cannot be transformed into a list of objects.
	 */
	public abstract TResult parse(String text) throws ParsingException;
	
	/**
	 * Transforms the given text into an object. 
	 * @param text - text to parse.
	 * @param defaultValue - value to return if unable to parse anything.
	 * @return Result of the parsing, or default value.
	 */
	public TResult parse(String text, TResult defaultValue) {
		
		// Simple implementation that should be overriden if more control or efficiency is needed
		try {
			return parse(text);
		} catch (ParsingException e) {
			return defaultValue;
		}	
	}
	
	/**
	 * Transforms the head of the queue into a string of objects. No head is treated as an empty string.
	 * @param tokens - queue of tokens.
	 * @return The corresponding string of objects.
	 * @throws ParsingException The head cannot be transformed into a list of objects.
	 */
	public TResult parse(Queue<String> tokens) throws ParsingException {
		
		// If the token stream is lacking an element, 
		// it will be treated as a universal matcher
		String token = !tokens.isEmpty() ? tokens.peek() : ""; 
		
		// Exceptions will bubble up, but they will not corrupt the token stream
		TResult element = parse(token);
		
		// Clean up
		tokens.poll();
		return element;
	}
	
	/**
	 * Reads tokens from (delimited by a vertical bar) a query rule body.
	 * @param text - query rule body to read.
	 * @return Queue of the tokens.
	 */
	protected Queue<String> getParameterQueue(String text) {
		
		String[] components = text.split("\\||:");
		
		// Clean up
		for (int i = 0; i < components.length; i++) 
			components[i] = components[i].trim().toLowerCase();
		
		return new LinkedList<String>(Arrays.asList(components));
	}
	
	/**
	 * Attempt to parse integer.
	 * @param input - text of the integer to parse.
	 * @return The parsed integer if successful, or NULL if unsuccessful.
	 */
	public static Integer tryParse(String input) {
		return tryParse(input, null);
	}
	
	/**
	 * Attempt to parse integer.
	 * @param input - text of the integer to parse.
	 * @param defaultValue - value to return if the parsing was unsuccessful.
	 * @return The parsed integer if successful, or defaultValue if not.
	 */
	public static Integer tryParse(String input, Integer defaultValue) {
		try { 
			if (!Utility.isNullOrIgnoreable(input)) {
				return Integer.parseInt(input);
			} else {
				return defaultValue;
			}
				
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
}
