package com.comphenix.xp.parser.sections;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.configuration.ConfigurationSection;

import com.comphenix.xp.SampleRange;
import com.comphenix.xp.lookup.LevelingRate;
import com.comphenix.xp.parser.ParsingException;
import com.comphenix.xp.parser.RangeParser;

import de.congrace.exp4j.Calculable;
import de.congrace.exp4j.CustomFunction;
import de.congrace.exp4j.ExpressionBuilder;
import de.congrace.exp4j.InvalidCustomFunctionException;
import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;

public class LevelsSectionParser extends SectionParser<LevelingRate> {

	@Override
	public LevelingRate parse(ConfigurationSection input, String sectionName) throws ParsingException {

		LevelingRate levels = new LevelingRate();
		
		RangeParser rangeParser = new RangeParser();
		
		if (input == null)
			throw new NullArgumentException("input");
		
		// Null is handled as the root
		if (sectionName != null) {
			input = input.getConfigurationSection(sectionName);
			
			// No rewards found
			if (input == null)
				return levels;
		}
		
		// Load levels
		for (String key : input.getKeys(false)) {
			try {
				
				SampleRange levelRange = rangeParser.parseString(key, null);
				
				if (levelRange != null) {
					parseValue(levelRange, levels, input.get(key));
					
				} else {
					// Inform about this problem
					throw ParsingException.fromFormat("Not a valid range.");
				}
				
			} catch (ParsingException ex) {
				if (isCollectExceptions()) {
					debugger.printWarning(this, "Cannot parse level %s: ", key, ex.getMessage());
				} else {
					throw ex;
				}
			}
		}
		
		// Return result
		return levels;
	}
	
	private void parseValue(SampleRange range, LevelingRate rate, Object value) throws ParsingException {
		
		if (value instanceof Integer) {
			rate.put(range.getMinimum(), range.getMaximum(), (Integer) value);
		
		// Parse expressions too
		} else if (value instanceof String) {
			try {
				Calculable func = new ExpressionBuilder((String) value).withCustomFunction(new CustomFunction("round") {
					@Override
					public double applyFunction(double... args) {
						return Math.round(args[0]);
					}
				}).withVariableNames("level").build();
				
				// Store this expression
				rate.put(range.getMinimum(), range.getMaximum(), func);
				
				// Convert errors to parsing exceptions
			} catch (UnknownFunctionException e) {
				throw new ParsingException(e.getMessage(), e);
			} catch (UnparsableExpressionException e) {
				throw new ParsingException(e.getMessage(), e);
			} catch (InvalidCustomFunctionException e) {
				throw new ParsingException(e.getMessage(), e);
			}
			
		} else {
			throw ParsingException.fromFormat("Unknown value: %s", value);
		}
	}
}
