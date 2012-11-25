package com.comphenix.xp.parser;

import static org.junit.Assert.*;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.Test;

import com.comphenix.xp.expressions.RangeExpression;
import com.google.common.collect.Lists;

public class RangeParserTest {

	@Test
	public void test() {
		
		RangeParser parser = new RangeParser();
		
		String key = "range";
		ConfigurationSection listValue = createWithKey(key, Lists.newArrayList(12));
		ConfigurationSection textValue = createWithKey(key, "5 - 10");
		ConfigurationSection doubleValue = createWithKey(key, 6.5);
		
		try {
			assertEquals(new RangeExpression(12), parser.parse(listValue, key));
			assertEquals(new RangeExpression(5, 10), parser.parse(textValue, key));
			assertEquals(new RangeExpression(6.5), parser.parse(doubleValue, key));
			
		} catch (ParsingException e) {
			// None of these should throw an exception
			fail(e.toString());
		}
	}

	private ConfigurationSection createWithKey(String key, Object value) {
		MemoryConfiguration config = new MemoryConfiguration();
		
		// Set the key-value pair
		config.set(key, value);
		return config;
	}
}
