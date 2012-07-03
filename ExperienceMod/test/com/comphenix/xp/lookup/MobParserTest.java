package com.comphenix.xp.lookup;

import static org.junit.Assert.*;

import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.junit.Test;

import com.comphenix.xp.parser.ParsingException;
import com.comphenix.xp.parser.text.MobParser;

public class MobParserTest {

	@Test
	public void testParser() throws ParsingException {
		
		MobQuery universal = MobQuery.fromAny();
		MobQuery allZombies = MobQuery.fromAny(EntityType.ZOMBIE);
		MobQuery fallingZombies = MobQuery.fromAny(EntityType.ZOMBIE, DamageCause.FALL, null, null, null, null);
		MobQuery spawnedMobs = MobQuery.fromAny(null, null, SpawnReason.SPAWNER, null, null, null);
		
		MobParser parser = new MobParser();
		
		assertEquals(universal, parser.parse("?"));
		assertEquals(allZombies, parser.parse("zombie"));
		assertEquals(fallingZombies, parser.parse("zombie|fall"));
		assertEquals(fallingZombies, parser.parse("zombie|fall|spawner,!spawner"));
		assertEquals(spawnedMobs, parser.parse("?|?|spawner"));
		assertNotSame(universal, parser.parse("?|?|spawner"));
	}
}
