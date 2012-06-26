package com.comphenix.xp.parser.primitives;

import com.comphenix.xp.parser.Parser;
import com.comphenix.xp.parser.ParsingException;

public class StringParser extends Parser<String> {
	@Override
	public String parse(String text) throws ParsingException {
		return text.trim();
	}
}
