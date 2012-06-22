package com.comphenix.xp.parser;

import java.util.Queue;

public class BooleanParser extends Parser<Boolean> {

	private String parameterName;

	public BooleanParser(String parameterName) {
		this.parameterName = parameterName;
	}
	
	public String getParameterName() {
		return parameterName;
	}

	/**
	 * Parses the given parameter as a boolean. 
	 * @param text Parameter to parse.
	 * @return Boolean value if parsing succeeded, or NULL otherwise.
	 */
	@Override
	Boolean parse(String text) throws ParsingException {
		
		if (text == null)
			return null;
		
		boolean value = !text.startsWith("!"); // Negative prefix
		
		// Use null instead of exceptions
		if (parameterName.startsWith(text, value ? 0 : 1))
			return value;
		else
			return null; 
	}
	
	/**
	 * Transforms and returns the first non-null element from the left into an object. That element is removed.
	 * @param tokens Queue of items.
	 * @return The object that was removed OR null if no element could be found.
	 */
	public Boolean parseAny(Queue<String> tokens) throws ParsingException {

		String toRemove = null;
		Boolean result = null;
				
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
