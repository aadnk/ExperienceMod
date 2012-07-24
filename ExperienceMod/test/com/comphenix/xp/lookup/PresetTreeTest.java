package com.comphenix.xp.lookup;

import static org.junit.Assert.*;

import org.junit.Test;

import com.comphenix.xp.ActionTypes;
import com.comphenix.xp.Configuration;

public class PresetTreeTest {

	@Test
	public void testPresetQuery() {

		PresetTree tree = new PresetTree();
		
		PresetQuery universal = PresetQuery.fromAny();
		PresetQuery emptyPreset = PresetQuery.fromAny("empty", null);
		
		PresetQuery getUniversal = PresetQuery.fromExact((String) null, null);
		
		Configuration one = new Configuration(null, ActionTypes.Default());
		Configuration two = new Configuration(null, ActionTypes.Default());
		
		tree.put(universal, one);
		tree.put(emptyPreset, two);
		
		assertEquals(tree.get(getUniversal), one);
		assertEquals(tree.get(emptyPreset), two);
	}
}
