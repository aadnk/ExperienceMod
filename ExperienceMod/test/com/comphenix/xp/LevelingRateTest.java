package com.comphenix.xp;

import static org.junit.Assert.*;

import org.junit.Test;

import com.comphenix.xp.lookup.LevelingRate;

public class LevelingRateTest {

	@Test
	public void testRanges() {
		LevelingRate levels = new LevelingRate();
		
		levels.put(1, 5, 10);
		levels.put(2, 3, 9);
		levels.put(7, 10, 15);
		levels.put(11, 14, 20);
		
		// Check that everything is working
		assertEquals(levels.get(1), new Integer(10));
		assertEquals(levels.get(2), new Integer(9));
		assertEquals(levels.get(4), new Integer(10));
		assertEquals(levels.get(6), null);
		assertEquals(levels.get(8), new Integer(15));
		assertEquals(levels.get(14), new Integer(20));
		
		// Try to remove some parts
		levels.remove(6, 12);
		
		// Now only 1 - 5 and 2 - 3 is left.
		assertEquals(levels.get(4), new Integer(10));
		assertEquals(levels.get(8), null);
		assertEquals(levels.get(11), null);
	}
}
