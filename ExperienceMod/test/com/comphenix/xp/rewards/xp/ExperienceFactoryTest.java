package com.comphenix.xp.rewards.xp;

import static org.junit.Assert.*;

import org.junit.Test;

import com.comphenix.xp.rewards.ResourceFactory;

public class ExperienceFactoryTest {

	@Test
	public void test() {
		
		ResourceFactory initial = new ExperienceFactory(10);
		ResourceFactory same = initial.withMultiplier(1);
		
		assertEquals(initial, same);
	}
}
