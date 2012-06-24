package com.comphenix.xp.lookup;

import static org.junit.Assert.*;

import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.junit.Test;
import com.comphenix.xp.Range;

public class MobTreeTest {

	@Test
	public void testMobQuerying() {

		MobTree tree = new MobTree(1);
		
		MobQuery fallGib = new MobQuery(null, DamageCause.FALL, null, null, null);
		MobQuery magicGib = new MobQuery(null, DamageCause.MAGIC, null, null, null);
		MobQuery zombieKill = new MobQuery(EntityType.ZOMBIE, null, null, null, null);
		MobQuery noSpawnXP = new MobQuery(null, null, SpawnReason.SPAWNER, null, null);
		
		Range zombieValue =  new Range(5);
		Range magicValue = new Range(2);
		Range noSpawnValue = new Range(0);
		Range fallValue = new Range(15);
		
		tree.put(zombieKill, zombieValue);
		tree.put(magicGib, magicValue);
		tree.put(noSpawnXP, noSpawnValue);
		tree.put(fallGib, fallValue);
		
		assertEquals(zombieValue, queryTree(tree, EntityType.ZOMBIE, DamageCause.ENTITY_ATTACK, SpawnReason.NATURAL));
		assertEquals(magicValue, queryTree(tree, EntityType.ZOMBIE, DamageCause.MAGIC, SpawnReason.NATURAL));
		assertEquals(noSpawnValue, queryTree(tree, EntityType.ZOMBIE, DamageCause.MAGIC, SpawnReason.SPAWNER));
		assertEquals(fallValue, queryTree(tree, EntityType.ZOMBIE, DamageCause.FALL, SpawnReason.SPAWNER));
	}
	
	private Range queryTree(MobTree tree, EntityType type, DamageCause cause, SpawnReason reason) {
		return tree.get(new MobQuery(type, cause, reason, false, false));
	}
}
