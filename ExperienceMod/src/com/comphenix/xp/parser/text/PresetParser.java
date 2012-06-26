package com.comphenix.xp.parser.text;

import java.util.List;
import java.util.Queue;

import org.apache.commons.lang.StringUtils;

import com.comphenix.xp.lookup.PresetQuery;
import com.comphenix.xp.parser.TextParser;
import com.comphenix.xp.parser.ParsingException;
import com.comphenix.xp.parser.primitives.StringParser;

public class PresetParser extends TextParser<PresetQuery> {

	private ParameterParser<String> textParsing = new ParameterParser<String>(new StringParser());
	
	@Override
	public PresetQuery parse(String text) throws ParsingException {

		if (text.length() == 0)
			// Empty names are not legal in YAML, so this shouldn't be possible 
			throw new IllegalArgumentException("Key must have some characters.");
		
		Queue<String> tokens = getParameterQueue(text);
		
		List<String> presetNames = textParsing.parse(tokens);
		List<String> worldNames = textParsing.parse(tokens);
		
		if (!tokens.isEmpty())
			throw ParsingException.fromFormat("Unknown preset tokens: ", 
					StringUtils.join(tokens, ", "));

		return new PresetQuery(presetNames, worldNames);
	}
}
