package com.comphenix.xp.lookup;

import static org.junit.Assert.*;

import org.junit.Test;

import com.comphenix.xp.Configuration;
import com.google.common.collect.Lists;

public class PresetTreeTest {

	@Test
	public void testPresetQuery() {

		PresetTree tree = new PresetTree();
		
		PresetQuery universal = new PresetQuery();
		PresetQuery emptyPreset = new PresetQuery("empty", null);
		
		PresetQuery getUniversal = PresetQuery.fromExact(null, null);
		
		Configuration one = new Configuration(null);
		Configuration two = new Configuration(null);
		
		tree.put(universal, one);
		tree.put(emptyPreset, two);
		
		assertEquals(tree.get(getUniversal), one);
		assertEquals(tree.get(emptyPreset), two);
	}

}
