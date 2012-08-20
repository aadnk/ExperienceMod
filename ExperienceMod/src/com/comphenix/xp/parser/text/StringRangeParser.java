package com.comphenix.xp.parser.text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.comphenix.xp.SampleRange;
import com.comphenix.xp.expressions.VariableFunction;
import com.comphenix.xp.parser.ParsingException;
import com.comphenix.xp.parser.TextParser;

/**
 * A simple parser that accepts ranges in the form "a - b" and produces a sample range.
 * 
 * @author Kristian
 */
public class StringRangeParser extends TextParser<SampleRange> {

	private static Pattern matchRange = Pattern.compile("\\s*\\[?([^\\[\\],-]+)[,-]?([^\\[\\],-]+)?\\]?\\s*");
	
	/**
	 * Converts a string range parser into a variable function parser.
	 * @param parser - the string range parser to convert.
	 * @return A variable function parser.
	 */
	public static TextParser<VariableFunction> toFunctionParser(TextParser<SampleRange> parser) {
		
		// Copy the reference
		final TextParser<SampleRange> stringParser = parser;
		
		// Implement a simple adapter
		return new TextParser<VariableFunction>() {
			@Override
			public VariableFunction parse(String text) throws ParsingException {
				return VariableFunction.fromRange(stringParser.parse(text));
			}
			
			@Override
			public VariableFunction parse(String text, VariableFunction defaultValue) {
				SampleRange result = stringParser.parse(text, null);
				
				if (result != null)
					return VariableFunction.fromRange(result);
				else
					return defaultValue;
			}
		};
	}
	
	@Override
	public SampleRange parse(String text) throws ParsingException {

		SampleRange result = parse(text, null);
		
		// Return value or throw exception
		if (result != null)
			return result;
		else
			throw ParsingException.fromFormat("Unable to parse %s as a range.", text);
	}
	
	/**
	 * Parses a given text as a range of the format "a - b".
	 * @param text - text to parse.
	 * @param defaultValue - value to return if the parser was unable to parse the range.
	 * @return The parsed range, or defaultValue if unable to parse the text.
	 */
	@Override
	public SampleRange parse(String text, SampleRange defaultValue) {
		// Try a simple "-" syntax
		Matcher match = matchRange.matcher(text);
		
		// Construct the range from the captured groups
		if (match.matches()) {
			if (match.groupCount() == 1 || match.group(2) == null) {
				return new SampleRange(
						Double.parseDouble(match.group(1))
				);
			} else {
				return new SampleRange(
						Double.parseDouble(match.group(1)),
						Double.parseDouble(match.group(2))
				); 
			}
		}
		
		// No match
		return defaultValue;
	}
}
