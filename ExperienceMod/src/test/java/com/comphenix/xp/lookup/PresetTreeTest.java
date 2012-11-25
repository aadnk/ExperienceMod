package com.comphenix.xp.lookup;

import static org.junit.Assert.*;

import org.junit.Test;

import com.comphenix.xp.ActionTypes;
import com.comphenix.xp.Configuration;
import com.comphenix.xp.parser.ParsingException;
import com.comphenix.xp.parser.primitives.StringParser;
import com.comphenix.xp.parser.text.ParameterParser;

public class PresetTreeTest {

	private ParameterParser<String> stringParser = new ParameterParser<String>(new StringParser());
	
	@Test
	public void testPresetQuery() throws ParsingException {

		PresetTree tree = new PresetTree();
		
		PresetQuery universal = PresetQuery.fromAny();
		PresetQuery emptyPreset = PresetQuery.fromAny("empty", null);
		PresetQuery worldPreset = PresetQuery.fromAny(null, "world_nether");
		
		Configuration one = new Configuration(null, ActionTypes.Default());
		Configuration two = new Configuration(null, ActionTypes.Default());
		Configuration three = new Configuration(null, ActionTypes.Default());
		
		tree.put(universal, one);
		tree.put(emptyPreset, two);
		tree.put(worldPreset, three);
		
		assertEquals(tree.get(PresetQuery.fromExact((String) null, "world_the_end")), one);
		assertEquals(tree.get(PresetQuery.fromExact("empty", "world")), two);
		assertEquals(tree.get(PresetQuery.fromExact(stringParser.parse((String) null), "world_nether")), three);
	}
}
