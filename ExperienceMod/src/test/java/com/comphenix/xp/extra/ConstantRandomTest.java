package com.comphenix.xp.extra;

import static org.junit.Assert.*;

import org.junit.Test;

public class ConstantRandomTest {

	@Test
	public void testBits() {

		// No byte literal? Are you kidding me?!
		ConstantRandom simpleTest = new ConstantRandom(new byte[] { (byte) 170, (byte) 170, (byte) 170 });
		
		int readFour = simpleTest.next(4); // 1010 
		int readSeven = simpleTest.next(7); // 010 1010
		int readThree = simpleTest.next(3); // 101

		assertEquals(10, readFour);
		assertEquals(42, readSeven);
		assertEquals(5, readThree);
	}
	
	@Test
	public void testMethods() {
	
		ConstantRandom maximum = new ConstantRandom(new byte[] { (byte) 255 }, true);
		ConstantRandom minimum = new ConstantRandom(new byte[] { (byte) 0 }, true);
		
		// The highest and lowest possible doubles
		assertEquals(1, maximum.nextDouble(), 0.001);
		assertEquals(0, minimum.nextDouble(), 0.001);
		
		// Highest and lowest possible in the given range
		assertEquals(25, maximum.nextInt(26));
		assertEquals(0, minimum.nextInt(26));
	}
}
