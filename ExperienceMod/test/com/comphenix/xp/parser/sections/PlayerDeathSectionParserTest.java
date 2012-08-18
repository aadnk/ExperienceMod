package com.comphenix.xp.parser.sections;

import static org.junit.Assert.*;

import org.junit.Test;

import com.comphenix.xp.Action;
import com.comphenix.xp.Configuration;
import com.comphenix.xp.ConfigurationTest;
import com.comphenix.xp.Debugger;
import com.comphenix.xp.MockDebugger;
import com.comphenix.xp.lookup.PlayerQuery;
import com.comphenix.xp.lookup.PlayerTree;
import com.comphenix.xp.parser.ParsingException;
import com.comphenix.xp.rewards.RewardProvider;
import com.comphenix.xp.rewards.xp.ExperienceFactory;
import com.comphenix.xp.rewards.xp.RewardExperience;

public class PlayerDeathSectionParserTest {

	@Test
	public void testExample() throws ParsingException {
	
		Debugger debugger = new MockDebugger();
		
		RewardProvider provider = new RewardProvider();
		provider.register(new RewardExperience());
		
		Configuration test = ConfigurationTest.createConfig("player death:\n" +
				"  aadnk: 10\n" + 
				"  ?|vip: 500\n", 
				debugger, provider);
		
		// Lookup
		PlayerTree tree = test.getPlayerDeathDrop();
		PlayerQuery aadnkQuery = PlayerQuery.fromExact("aadnk", "admin");
		PlayerQuery vipQuery = PlayerQuery.fromExact("test", "vip");
		
		Action aadnkAction = new Action();
		aadnkAction.addReward("EXPERIENCE", new ExperienceFactory(10));
		aadnkAction.setId(0);
		
		Action vipAction = new Action();
		vipAction.addReward("EXPERIENCE", new ExperienceFactory(500));
		vipAction.setId(1);
		
		assertEquals(aadnkAction, tree.get(aadnkQuery));
		assertEquals(vipAction, tree.get(vipQuery));
	}
}
