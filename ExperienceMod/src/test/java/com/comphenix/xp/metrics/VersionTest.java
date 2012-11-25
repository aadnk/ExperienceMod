package com.comphenix.xp.metrics;

import static org.junit.Assert.*;

import org.junit.Test;

public class VersionTest {

	@Test
	public void test() {
		
		assertLessThan(new Version("1.0.0"), new Version("1.1.2"));
		assertGreaterThan(new Version("2.0.0"), new Version("1.3"));
		assertGreaterThan(new Version("2.2.3"), new Version("2.2.3d"));
		assertGreaterThan(new Version("ExperienceMod 2.2.3f"), new Version("2.2.3d"));
	}

	private static <T extends Comparable<T>> void assertLessThan(T a, T b) {
		assertTrue(String.format("The value %s was not less than %s.", a, b), a.compareTo(b) < 0);
	}
	
	private static <T extends Comparable<T>> void assertGreaterThan(T a, T b) {
		assertTrue(String.format("The value %s was not greater than %s.", a, b), a.compareTo(b) > 0);
	}
}
