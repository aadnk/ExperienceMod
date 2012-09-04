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
		
		if (key.equals("Player")) {
			result = null;
		}
		
		try {
			result = parse(input, key, null, true);
		} catch (Exception e) {
			// Convert to parsing exception
			throw new ParsingException(
					String.format("Range error: %s", e.getMessage()), e);
		}
		
		// Turn default value into an exception
		if (result != null)
			return result;
		else
			throw ParsingException.fromFormat("Range error at key %s.", key );
	}
	
	@Override
	public VariableFunction parse(ConfigurationSection input, String key, VariableFunction defaultValue) {
		
		try {
			return parse(input, key, defaultValue, false);
		} catch (Exception e) {
			// You and I know this can never happen. The compiler, however ...
			throw new IllegalStateException("This should never occur.", e);
		}
	}
	
	private VariableFunction parse(ConfigurationSection input, String key, VariableFunction defaultValue, boolean throwException) throws Exception {

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
					throw new ParsingException("Too many elements in range - must be one or two.");
			
			} catch (Exception e) {
				// Parsing error
				if (throwException)
					throw e;
				else
					return defaultValue;
			}
			
		} else if (root instanceof String) { 
			// Parse it as a string
			try {
				return textParser.parse((String) root);
				
			} catch (Exception e) {
				// Error here too
				if (throwException)
					throw e;
				else
					return defaultValue;
			}
			
		} else if (root instanceof ConfigurationSection) {
			ConfigurationSection section = (ConfigurationSection) root;
			Double first = toDouble(section.get("first"));
			Double last = toDouble(section.get("last"));
			
			// Backwards compatibility
			if (first != null && last != null && section.getValues(false).size() == 2) {
				result = new SampleRange(first, last);
			} else {
				return defaultValue;
			}
		}
		
		// Convert to a function at the end
		return VariableFunction.fromRange(result);
	}
	
	private Double toDouble(Object value) {
		// Handle integers as well as doubles
		if (value instanceof Integer)
			return ((Integer) value).doubleValue();
		else if (value instanceof Double)
			return (Double) value;
		else
			return null;
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
