package com.comphenix.xp.parser;

public class IntegerParser extends Parser<Integer> {

	@Override
	Integer parse(String text) throws ParsingException {
		try {
			return Integer.parseInt(text);
		} catch (NumberFormatException e) {
			throw new ParsingException("Number is not well formed.", e);
		}
	}
}
