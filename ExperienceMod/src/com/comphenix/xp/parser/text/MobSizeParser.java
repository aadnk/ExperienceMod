package com.comphenix.xp.parser.text;

import java.util.HashMap;
import java.util.Map;

import com.comphenix.xp.parser.ParsingException;
import com.comphenix.xp.parser.TextParser;
import com.comphenix.xp.parser.Utility;

public class MobSizeParser extends TextParser<Integer> {
	
	// Possible to extend or override the parser
	protected Map<String, Integer> sizeNames = new HashMap<String, Integer>();
	
	public MobSizeParser() {
		initializeNames();
	}
	
	protected void initializeNames() {
		sizeNames.put("TINY", 1);
		sizeNames.put("SMALL", 2);
		sizeNames.put("BIG", 4);
	}
	
	@Override
	public Integer parse(String text) throws ParsingException {

		// Make sure we're not passed an empty element
		if (Utility.isNullOrIgnoreable(text))
			throw new ParsingException("Text cannot be empty or null.");
		 
		// See if the size is directly encoded
		Integer size = tryParse(text);
		
		if (size == null) {
			// Try to find a name version
			String enumName = Utility.getEnumName(text);
			
			// Convert from a name to a size
			if (sizeNames.containsKey(enumName)) {
				return sizeNames.get(enumName);
			} else {
				throw ParsingException.fromFormat("%s is not a recognized slime size.", text);
			}
		} 
		
		// Make sure this size is possible
		if (isLegalSize(size)) {
			return size;
		} else {
			throw ParsingException.fromFormat("The number %d is not a legal slime size.", size);
		}
	}
	
	/**
	 * Determines if the given size is a possible slime size.
	 * @param size - size to test.
	 * @return TRUE if this size exists (default: 1, 2 or 4), FALSE otherwise.
	 */
	protected boolean isLegalSize(int size) {
		return size == 1 || size == 2 || size == 4;
	}
}
