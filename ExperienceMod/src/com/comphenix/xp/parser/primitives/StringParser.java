package com.comphenix.xp.parser.primitives;

import com.comphenix.xp.parser.TextParser;
import com.comphenix.xp.parser.ParsingException;

public class StringParser extends TextParser<String> {
	@Override
	public String parse(String text) throws ParsingException {
		return text.trim();
	}
}
