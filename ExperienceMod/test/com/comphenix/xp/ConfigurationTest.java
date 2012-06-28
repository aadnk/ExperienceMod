package com.comphenix.xp;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.junit.Test;

import com.comphenix.xp.lookup.MobQuery;
import com.comphenix.xp.rewards.RewardProvider;
import com.comphenix.xp.rewards.RewardTypes;
import com.google.common.collect.Lists;

public class ConfigurationTest {

	@Test
	public void testMerging() {
		
		Debugger debugger = new MockDebugger();
		
		RewardProvider provider = new RewardProvider();
		provider.register(new MockRewardable(RewardTypes.EXPERIENCE));
		provider.register(new MockRewardable(RewardTypes.VIRTUAL));
		provider.register(new MockRewardable(RewardTypes.ECONOMY));
		provider.setDefaultReward(RewardTypes.EXPERIENCE);
		String def = "EXPERIENCE";
		
		Configuration first = createConfig(
				"multiplier: 1\n" +
				"mobs:\n" + 
				"  ?:\n" +
				"    experience: 5\n" +
				"    economy: 1\n", debugger, provider);
		
		Configuration second = createConfig(
				"multiplier: 1\n" +
				"mobs:\n" + 
				"  zombie: 0\n", debugger, provider);
		
		Configuration result = Configuration.fromMultiple(Lists.newArrayList(first, second), debugger);
		
		MobQuery queryBlace = MobQuery.fromAny(EntityType.BLAZE, DamageCause.ENTITY_ATTACK);
		MobQuery queryZombie = MobQuery.fromAny(EntityType.ZOMBIE, DamageCause.ENTITY_ATTACK);
		
		Action blaceAction = new Action();
		blaceAction.addReward(def, new Range(5));
		blaceAction.addReward("ECONOMY", new Range(1));
		
		Action zombieAction = new Action(def, new Range(0));
		
		assertEquals(blaceAction, result.getExperienceDrop().get(queryBlace));
		assertEquals(zombieAction, result.getExperienceDrop().get(queryZombie));
	}
	
	// Load configuration from text
	private Configuration createConfig(String text, Debugger debugger, RewardProvider provider) {
		return new Configuration(fromText(text), debugger, provider);
	}
	
	private YamlConfiguration fromText(String text) {
		try {
			InputStream buffer = new ByteArrayInputStream(text.getBytes("UTF-8"));
			return YamlConfiguration.loadConfiguration(buffer);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("No UTF-8 installed.");
		}
	}
}
