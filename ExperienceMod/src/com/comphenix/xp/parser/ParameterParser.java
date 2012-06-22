package com.comphenix.xp.parser;

import java.util.ArrayList;
import java.util.List;

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
		if (Parsing.isNullOrIgnoreable(text))
			// If so, return an empty list
			return elements; 
		
		String[] tokens = text.split(VALUE_DIVIDER);
		
		// Now the interesting thing happens
		for (String token : tokens) {
			
			// Check validity and so on
			if (Parsing.isNullOrIgnoreable(token))
				throw ParsingException.fromFormat(
						"Universal matcher (%s) cannot be part of a list of values.", token);
			
			// Exceptions will bubble up the chain
			elements.add(elementParser.parse(token.trim()));
		}
		
		return elements;
	}
	
	public Parser<TItem> getElementParser() {
		return elementParser;
	}
}
