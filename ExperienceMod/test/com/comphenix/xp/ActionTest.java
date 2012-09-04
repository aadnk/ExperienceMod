package com.comphenix.xp;

import static org.junit.Assert.*;

import java.util.concurrent.ConcurrentHashMap;

import org.junit.Test;

import com.comphenix.xp.rewards.RewardTypes;
import com.comphenix.xp.rewards.xp.ExperienceFactory;

public class ActionTest {

	@Test
	public void testEquals() {
		ConcurrentHashMap<Action, Integer> map = new ConcurrentHashMap<Action, Integer>();
		Action test = new Action(RewardTypes.EXPERIENCE.name(), new ExperienceFactory(5));
		
		map.put(test, 5);
		
		// Multiplying the action by one shouldn't change it
		assertTrue(map.containsKey(test.multiply(1)));
		assertFalse(map.containsKey(test.multiply(0.5)));
		
		map.put(test.multiply(1), 10);
		assertEquals(1, map.size());
	}
}
