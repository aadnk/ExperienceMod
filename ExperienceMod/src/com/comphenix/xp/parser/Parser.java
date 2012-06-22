package com.comphenix.xp.parser;

import java.util.Queue;

/**
 * Represents an object that transforms strings of text into objects.
 * 
 * @author Kristian
 * @param <TResult> Type of the resulting object.
 */
public abstract class Parser<TResult> {
	/**
	 * Transforms the given text into an object. 
	 * @param text Text to parse.
	 * @return Result of the parsing.
	 * @throws ParsingException The text cannot be transformed into a list of objects.
	 */
	abstract TResult parse(String text) throws ParsingException;
	 
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
	 * Transforms and returns the first non-null element from the left into an object. That element is removed.
	 * @param tokens Queue of items.
	 * @return The object that was removed OR null if no element could be found.
	 */
	public TResult parseAny(Queue<String> tokens) throws ParsingException {

		String toRemove = null;
		TResult result = null;
				
		for (String current : tokens ){
			result = parse(current);
			
			// We have a solution
			if (result != null) {
				toRemove = current;
				break;
			}
		}
		
		// Return and remove token
	    if (result != null) {
	    	tokens.remove(toRemove);
	    	return result;
	    	
	    } else {
	    	
	    	// Speed things up a bit
	    	return null;
	    }
	}
}
