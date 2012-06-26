package com.comphenix.xp.parser;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Represents an object that transforms strings of text into objects.
 * 
 * @author Kristian
 * @param <TResult> Type of the resulting object.
 */
public abstract class TextParser<TResult> {
	/**
	 * Transforms the given text into an object. 
	 * @param text Text to parse.
	 * @return Result of the parsing.
	 * @throws ParsingException The text cannot be transformed into a list of objects.
	 */
	public abstract TResult parse(String text) throws ParsingException;
	 
	/**
	 * Transforms the head of the queue into a string of objects. No head is treated as an empty string.
	 * @param tokens Queue of tokens.
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
	 * @param text Query rule body to read.
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
	 * @param input Text of the integer to parse.
	 * @return The parsed integer if successful, or NULL if unsuccessful.
	 */
	public static Integer tryParse(String input) {
		return tryParse(input, null);
	}
	
	/**
	 * Attempt to parse integer.
	 * @param input Text of the integer to parse.
	 * @param defaultValue Value to return if the parsing was unsuccessful.
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
