package com.comphenix.xp.lookup;

import static org.junit.Assert.*;

import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.junit.Test;

import com.comphenix.xp.Action;
import com.comphenix.xp.Range;

public class MobTreeTest {

	@Test
	public void testMobQuerying() {

		MobTree tree = new MobTree(1);
		String def = "EXPERIENCE";
		
		MobQuery fallGib = MobQuery.fromAny(null, DamageCause.FALL);
		MobQuery magicGib = MobQuery.fromAny(null, DamageCause.MAGIC);
		MobQuery zombieKill = MobQuery.fromAny(EntityType.ZOMBIE);
		MobQuery noSpawnXP = MobQuery.fromAny(null, null, SpawnReason.SPAWNER, null, null);
		
		Action zombieValue = new Action(def, new Range(5));
		Action magicValue = new Action(def,  new Range(2));
		Action noSpawnValue = new Action(def, new Range(0));
		Action fallValue = new Action(def, new Range(15));
		
		tree.put(zombieKill, zombieValue);
		tree.put(magicGib, magicValue);
		tree.put(noSpawnXP, noSpawnValue);
		tree.put(fallGib, fallValue);
		
		assertEquals(zombieValue, queryTree(tree, EntityType.ZOMBIE, DamageCause.ENTITY_ATTACK, SpawnReason.NATURAL));
		assertEquals(magicValue, queryTree(tree, EntityType.ZOMBIE, DamageCause.MAGIC, SpawnReason.NATURAL));
		assertEquals(noSpawnValue, queryTree(tree, EntityType.ZOMBIE, DamageCause.MAGIC, SpawnReason.SPAWNER));
		assertEquals(fallValue, queryTree(tree, EntityType.ZOMBIE, DamageCause.FALL, SpawnReason.SPAWNER));
	}
	
	private Action queryTree(MobTree tree, EntityType type, DamageCause cause, SpawnReason reason) {
		return tree.get(MobQuery.fromExact(type, cause, reason, false, false));
	}
}
