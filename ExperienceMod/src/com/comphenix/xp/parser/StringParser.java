package com.comphenix.xp.parser;

public class StringParser extends Parser<String> {
	@Override
	String parse(String text) throws ParsingException {
		return text.trim();
	}
}
