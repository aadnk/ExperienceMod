package com.comphenix.xp.parser.primitives;

import com.comphenix.xp.parser.TextParser;
import com.comphenix.xp.parser.ParsingException;

public class IntegerParser extends TextParser<Integer> {

	@Override
	public Integer parse(String text) throws ParsingException {
		try {
			return Integer.parseInt(text);
		} catch (NumberFormatException e) {
			throw new ParsingException("Number is not well formed.", e);
		}
	}
}
