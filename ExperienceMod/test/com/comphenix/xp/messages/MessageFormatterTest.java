package com.comphenix.xp.messages;

import static org.junit.Assert.*;

import org.junit.Test;

import com.comphenix.xp.rewards.ResourceHolder;
import com.comphenix.xp.rewards.xp.ExperienceHolder;
import com.google.common.collect.Lists;

public class MessageFormatterTest {

	@Test
	public void test() {

		MockPlayer testPlayer = new MockPlayer();
		testPlayer.setDisplayName("test");
		
		MessageFormatter formatter = new MessageFormatter(testPlayer, 
				Lists.newArrayList((ResourceHolder) new ExperienceHolder(1)));
		
		String colored = formatter.formatMessage("&7{player} got {experience}.");
		String expected = "§7test got 1 experience.";
		
		assertEquals(expected, colored);
	}

}
