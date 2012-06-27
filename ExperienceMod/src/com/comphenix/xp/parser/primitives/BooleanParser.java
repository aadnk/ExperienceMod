package com.comphenix.xp.parser.primitives;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import com.comphenix.xp.parser.TextParser;
import com.comphenix.xp.parser.ParsingException;

public class BooleanParser extends TextParser<List<Boolean>> {

	private static List<Boolean> emptyList = new ArrayList<Boolean>();
	
	private String parameterName;

	public BooleanParser(String parameterName) {
		this.parameterName = parameterName;
	}
	
	public String getParameterName() {
		return parameterName;
	}

	/**
	 * Parses the given parameter as a boolean. 
	 * @param text - parameter to parse.
	 * @return Boolean value if parsing succeeded, or NULL otherwise.
	 */
	@Override
	public List<Boolean> parse(String text) throws ParsingException {
		
		if (text == null)
			return null;
		
		String[] elements = text.split(",");
		List<Boolean> values = new ArrayList<Boolean>();
		
		for (String element : elements) {
			
			boolean currentValue = !element.startsWith("!"); // Negative prefix
			String currentName = element.substring(currentValue ? 0 : 1);
			
			// Permit prefixing 
			if (parameterName.startsWith(currentName))  {
				
				// Be careful to handle the case !parameterName, parameterName
				if (!values.contains(currentValue))
					values.add(currentValue);
				else
					throw ParsingException.fromFormat("Duplicate value detected: %", element);
				
			} else  {
				return null; 
			}
		}

		// Null indicates any value
		if (values.isEmpty())
			return null;
		else
			return values;
	}
	
	/**
	 * Transforms and returns the first non-null element from the left into an object. That element is removed.
	 * @param tokens - queue of items.
	 * @return List containing the removed object, OR an empty list if no object was removed.
	 */
	public List<Boolean> parseAny(Queue<String> tokens) throws ParsingException {

		String toRemove = null;
		List<Boolean> result = null;
				
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
	    	
	    	if (result.size() == 1)
	    		return result;
	    	else
	    		// Match true and false at the same time
	    		return emptyList;
	    	
	    } else {
	    	
	    	// Speed things up a bit
	    	return emptyList;
	    }
	}
}
