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
import com.google.common.collect.Lists;

public class ConfigurationTest {

	@Test
	public void testMerging() {
		
		Debugger debugger = new MockDebugger();
		
		Configuration first = createConfig(
				"multiplier: 1\n" +
				"mobs:\n" + 
				"  ?: 5\n", debugger);
		
		Configuration second = createConfig(
				"multiplier: 1\n" +
				"mobs:\n" + 
				"  zombie: 0\n", debugger);
		
		Configuration result = Configuration.fromMultiple(Lists.newArrayList(first, second), debugger);
		
		MobQuery queryBlace = MobQuery.fromAny(EntityType.BLAZE, DamageCause.ENTITY_ATTACK);
		MobQuery queryZombie = MobQuery.fromAny(EntityType.ZOMBIE, DamageCause.ENTITY_ATTACK);
		
		assertEquals(new Range(5), result.getExperienceDrop().get(queryBlace));
		assertEquals(new Range(0), result.getExperienceDrop().get(queryZombie));
	}
	
	// Load configuration from text
	private Configuration createConfig(String text, Debugger debugger) {
		return new Configuration(fromText(text), debugger);
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