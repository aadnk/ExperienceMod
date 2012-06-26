package com.comphenix.xp.parser.text;

import java.util.ArrayList;
import java.util.List;

import com.comphenix.xp.parser.Parser;
import com.comphenix.xp.parser.ParsingException;
import com.comphenix.xp.parser.Utility;

/**
 * Reads a comma-delimited list of parsable values.
 * 
 * @author Kristian
 * @param <TItem> Type of each value.
 */
public class ParameterParser<TItem> extends Parser<List<TItem>>{

	private static final String VALUE_DIVIDER = ",";
	private Parser<TItem> elementParser;

	public ParameterParser(Parser<TItem> elementParser) {
		this.elementParser = elementParser;
	}

	@Override
	public List<TItem> parse(String text) throws ParsingException {

		List<TItem> elements = new ArrayList<TItem>();
		
		// First things first. Is this an empty sequence?
		if (Utility.isNullOrIgnoreable(text))
			// If so, return an empty list
			return elements; 
		
		String[] tokens = text.split(VALUE_DIVIDER);
		
		// Now the interesting thing happens
		for (String token : tokens) {
			
			// Check validity and so on
			if (Utility.isNullOrIgnoreable(token))
				throw ParsingException.fromFormat(
						"Universal matcher (%s) cannot be part of a list of values.", token);
			
			// Exceptions will bubble up the chain
			elements.add(elementParser.parse(token.trim()));
		}
		
		return elements;
	}
	
	public List<TItem> parseExact(String text) throws ParsingException {
		List<TItem> result = parse(text);
		
		// Represent nothing with NULL
		if (result.size() == 0)
			result.add(null);
		
		return result;
	}
	
	public Parser<TItem> getElementParser() {
		return elementParser;
	}
}
