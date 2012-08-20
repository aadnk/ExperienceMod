package com.comphenix.xp.parser;

import java.util.List;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.configuration.ConfigurationSection;

import com.comphenix.xp.SampleRange;
import com.comphenix.xp.expressions.VariableFunction;
import com.comphenix.xp.parser.text.StringRangeParser;

/**
 * Responsible for reading ranges. 
 * 
 * @author Kristian
 */
public class RangeParser extends ConfigurationParser<VariableFunction> {
	
	protected TextParser<VariableFunction> textParser;
	
	public RangeParser() {
		// Automatically convert the string parser to a function parser
		this(StringRangeParser.toFunctionParser(new StringRangeParser()));
	}
	
	/**
	 * Constructs a range parser with a specified text parser.
	 * @param textParser - text parser to use.
	 */
	public RangeParser(TextParser<VariableFunction> textParser) {
		this.textParser = textParser;
	}	
	
	@Override
	public VariableFunction parse(ConfigurationSection input, String key) throws ParsingException {

		VariableFunction result = null;
		
		try {
			result = parse(input, key, null, false);
		} catch (Exception e) {
			e.printStackTrace();
			// Convert to parsing exception
			throw new ParsingException(
					String.format("Cannot parse range from key %s: %s", key, e.getMessage()), e);
		}
		
		// Turn default value into an exception
		if (result != null)
			return result;
		else
			throw ParsingException.fromFormat("Cannot parse range from key %s.", key );
	}
	
	/**
	 * Transform a configuration object, represented by a key, into a range. 
	 * @param input - the configuration section in which the key to the object is stored.
	 * @param key - the key of the object.
	 * @param defaultValue - value to return if parsing failed.
	 * @return A range from the given configuration object, or defaultValue if failed.
	 */
	public VariableFunction parse(ConfigurationSection input, String key, VariableFunction defaultValue) {
		
		try {
			return parse(input, key, defaultValue, false);
		} catch (Exception e) {
			// You and I know this can never happen. The compiler, however ...
			throw new IllegalStateException("This should never occur.", e);
		}
	}
	
	private VariableFunction parse(ConfigurationSection input, String key, VariableFunction defaultValue, boolean throwException) throws Exception {
		String start = key + ".first";
		String end = key + ".last";
		
		Object root = input.get(key);
		SampleRange result = null;
		
		if (root instanceof Double) {
			result = new SampleRange((Double) root);
		} else if (root instanceof Integer) {
			result = new SampleRange((Integer) root); 
		} else if (root instanceof List) {
			@SuppressWarnings("rawtypes")
			List attempt = (List) root;

			try {
				// Try to extract two or one elements from the list
				if (attempt != null && attempt.size() == 2)
					result = new SampleRange(tryParse(attempt.get(0)), tryParse(attempt.get(1)));
				else if (attempt != null && attempt.size() == 1)
					result = new SampleRange(tryParse(attempt.get(0)));
				else if (!throwException)
					return defaultValue;
				else
					// Make errors more descriptive
					throw new Exception("Too many elements in range - must be one or two.");
			
			} catch (Exception e) {
				// Parsing error
				if (throwException)
					throw e;
				else
					return defaultValue;
			}
			
		} else if (root instanceof String) { 
			// Parse it as a string
			return textParser.parse((String) root, defaultValue);
			
		} else {
			// Backwards compatibility
			if (input.contains(start) && input.contains(end)) {
				result = new SampleRange(input.getDouble(start), input.getDouble(end));
			} else {
				return defaultValue;
			}
		}
		
		// Convert to a function at the end
		return VariableFunction.fromRange(result);
	}

	/**
	 * Constructs a similar range parser with the given text parser.
	 * @param parser - text parser to handle string ranges.
	 * @return New range parser.
	 */
	public RangeParser withParser(TextParser<VariableFunction> parser) {
		return new RangeParser(parser);
	}
	
	private double tryParse(Object obj) {
		if (obj == null)
			throw new NullArgumentException("obj");
		
		// Handle different types
		if (obj instanceof Double)
			return (Double) obj;
		else if (obj instanceof Integer)
			return (int) ((Integer) obj);
		else if (obj instanceof String)
			return Double.parseDouble((String) obj);
		else
			throw new IllegalArgumentException("Unknown argument type.");
	}
}
