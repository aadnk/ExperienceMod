package com.comphenix.xp.messages;

import static org.junit.Assert.*;

import org.junit.Test;

public class MessageFormatterTest {

	@Test
	public void test() {

		MockPlayer testPlayer = new MockPlayer();
		testPlayer.setDisplayName("test");
		
		MessageFormatter formatter = new MessageFormatter(testPlayer, 1);
		
		String colored = formatter.formatMessage("&7{player} got {experience} exp.");
		String expected = "§7test got 1 exp.";
		
		assertEquals(expected, colored);
	}

}
