package com.comphenix.xp.lookup;

import static org.junit.Assert.*;

import org.junit.Test;

public class ItemQueryTest {

	@Test
	public void testMatch() {

		ItemQuery particularStone = ItemQuery.fromExact(1, 4);
		ItemQuery particularDirt = ItemQuery.fromExact(3, 5);
		ItemQuery anyStone = ItemQuery.fromAny(1, null);
		ItemQuery anyQuery = ItemQuery.fromAny((Integer) null, null);
		
		ItemQuery matchOnlyAny = ItemQuery.fromExact(null, null);
		
		assertTrue(anyStone.match(particularStone));
		assertFalse(anyStone.match(particularDirt));
		
		assertTrue(anyQuery.match(matchOnlyAny));
		assertFalse(anyStone.match(matchOnlyAny));
	}
}
