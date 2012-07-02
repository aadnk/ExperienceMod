package com.comphenix.xp.messages;

import static org.junit.Assert.*;

import org.junit.Test;

public class StandardServiceTest {

	@Test
	public void test() {

		MockServer server = new MockServer();
		StandardService service = new StandardService(server);
		
		assertTrue(service.hasChannel("global"));
		assertTrue(service.hasChannel("world"));
		assertTrue(service.hasChannel("private"));
		assertFalse(service.hasChannel("abcd"));
		
		service.announce("global", "Hello world!");
		service.announce("abcd", "fake");
		
		assertEquals(1, server.getBroadcastCount());
	}

}
