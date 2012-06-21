package com.comphenix.xp.lookup;

import static org.junit.Assert.*;

import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.junit.Test;

import parser.MobParser;
import parser.ParsingException;

public class MobParserTest {

	@Test
	public void testParser() throws ParsingException {
		
		MobQuery universal = new MobQuery();
		MobQuery allZombies = new MobQuery(EntityType.ZOMBIE);
		MobQuery fallingZombies = new MobQuery(EntityType.ZOMBIE, DamageCause.FALL, null, null, null);
		MobQuery spawnedMobs = new MobQuery(null, null, SpawnReason.SPAWNER, null, null);
		
		MobParser parser = new MobParser();
		
		assertEquals(universal, parser.fromString("?"));
		assertEquals(allZombies, parser.fromString("zombie"));
		assertEquals(fallingZombies, parser.fromString("zombie|fall"));
		assertEquals(spawnedMobs, parser.fromString("?|?|spawner"));
	}
}
