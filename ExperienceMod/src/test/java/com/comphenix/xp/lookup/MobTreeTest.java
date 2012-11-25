package com.comphenix.xp.lookup;

import static org.junit.Assert.*;

import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.junit.Test;

import com.comphenix.xp.Action;
import com.comphenix.xp.rewards.xp.ExperienceFactory;

public class MobTreeTest {

	@Test
	public void testMobQuerying() {

		MobTree tree = new MobTree(1);
		String def = "EXPERIENCE";
		
		MobQuery fallGib = MobQuery.fromAny(null, DamageCause.FALL);
		MobQuery magicGib = MobQuery.fromAny(null, DamageCause.MAGIC);
		MobQuery zombieKill = MobQuery.fromAny(EntityType.ZOMBIE);
		MobQuery noSpawnXP = MobQuery.fromAny((EntityType) null, null, SpawnReason.SPAWNER, null, null, null);
		MobQuery smallSlime = MobQuery.fromAny(EntityType.SLIME, null,2, SpawnReason.NATURAL, null, null, null);
		
		Action zombieValue = new Action(def, new ExperienceFactory(5));
		Action magicValue = new Action(def,  new ExperienceFactory(2));
		Action noSpawnValue = new Action(def, new ExperienceFactory(0));
		Action fallValue = new Action(def, new ExperienceFactory(15));
		Action slimeValue = new Action(def, new ExperienceFactory(10));
		
		tree.put(zombieKill, zombieValue);
		tree.put(magicGib, magicValue);
		tree.put(noSpawnXP, noSpawnValue);
		tree.put(fallGib, fallValue);
		tree.put(smallSlime, slimeValue);
		
		assertEquals(zombieValue, queryTree(tree, EntityType.ZOMBIE, DamageCause.ENTITY_ATTACK, SpawnReason.NATURAL, true));
		assertEquals(magicValue, queryTree(tree, EntityType.ZOMBIE, DamageCause.MAGIC, SpawnReason.NATURAL, true));
		assertEquals(noSpawnValue, queryTree(tree, EntityType.ZOMBIE, DamageCause.MAGIC, SpawnReason.SPAWNER, true));
		assertEquals(fallValue, queryTree(tree, EntityType.ZOMBIE, DamageCause.FALL, SpawnReason.SPAWNER, false));
		assertEquals(slimeValue, queryTree(tree, EntityType.SLIME, 2, DamageCause.ENTITY_ATTACK, SpawnReason.NATURAL, true));
		assertNull(queryTree(tree, EntityType.SLIME, 4, DamageCause.ENTITY_ATTACK, SpawnReason.NATURAL, true));
	}
	
	private Action queryTree(MobTree tree, EntityType type, DamageCause cause, SpawnReason reason, boolean hasKiller) {
		return tree.get(MobQuery.fromExact(type, cause, reason, false, false, hasKiller));
	}
	
	private Action queryTree(MobTree tree, EntityType type, int size, DamageCause cause, SpawnReason reason, boolean hasKiller) {
		return tree.get(MobQuery.fromExact(type, cause, size, reason, false, false, hasKiller));
	}
}
