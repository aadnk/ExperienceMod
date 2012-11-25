package com.comphenix.xp.parser.sections;

import static org.junit.Assert.*;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.Test;

import com.comphenix.xp.lookup.LevelingRate;
import com.comphenix.xp.parser.ParsingException;

public class LevelsSectionParserTest {

	@Test
	public void test() {

		int[] experience = { 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 
							 17, 20, 23, 26, 29, 32, 35, 38, 41, 44, 47, 50, 53, 56, 59 };
		
		LevelsSectionParser levelsParser = new LevelsSectionParser();

		// Load test configuration
		MemoryConfiguration config = new MemoryConfiguration();
		ConfigurationSection section = config.createSection("levels");
		
		// Default experience bars in Minecraft 1.3
		section.set("0 - 15", "17");
		section.set("16 - 29", "17 + (level - 15) * 3");
		section.set("30 - Infinity", "62 + (level - 30) * 7");
		
		try {
			LevelingRate rate = levelsParser.parse(config, "levels");
			
			// Check experiences
			for (int i = 0; i < 30; i++) {
				assertEquals(experience[i], (int) rate.get(i));
			}
			
		} catch (ParsingException e) {
			fail(e.getMessage());
		}
	}
}
