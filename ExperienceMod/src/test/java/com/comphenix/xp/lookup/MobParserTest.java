package com.comphenix.xp.lookup;

import static org.junit.Assert.*;

import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.junit.Test;

import com.comphenix.xp.parser.ParsingException;
import com.comphenix.xp.parser.text.MobMatcher;
import com.comphenix.xp.parser.text.MobParser;

public class MobParserTest {

	@Test
	public void testParser() throws ParsingException {
		
		MobQuery universal = MobQuery.fromAny();
		MobQuery allZombies = MobQuery.fromAny(EntityType.ZOMBIE);
		MobQuery fallingZombies = MobQuery.fromAny(EntityType.ZOMBIE, DamageCause.FALL);
		MobQuery spawnedMobs = MobQuery.fromAny((EntityType) null, null, SpawnReason.SPAWNER, null, null, null, null);
		MobQuery smallSlimes = MobQuery.fromAny(EntityType.SLIME, null, 1, null, null, null, null, null);
		
		MobParser parser = new MobParser(new MobMatcher());
		
		assertEquals(universal, parser.parse("?"));
		assertEquals(allZombies, parser.parse("zombie"));
		assertEquals(fallingZombies, parser.parse("zombie|fall"));
		assertEquals(fallingZombies, parser.parse("zombie|fall|spawner,!spawner"));
		assertEquals(smallSlimes, parser.parse("slime|?|tiny"));
		assertEquals(spawnedMobs, parser.parse("?|?|spawner"));
		assertNotSame(universal, parser.parse("?|?|spawner"));
	}
}
