package com.comphenix.xp.lookup;

import static org.junit.Assert.*;

import org.bukkit.potion.PotionType;
import org.junit.Test;

import com.comphenix.xp.Action;
import com.comphenix.xp.Range;

public class PotionTreeTest {

	@Test
	public void test() {
		PotionTree tree = new PotionTree(1);
		String def = "EXPERIENCE";
		
		// Match every regen
		PotionQuery regenUniversal = PotionQuery.fromAny(PotionType.REGEN);
		PotionQuery universal = PotionQuery.fromAny();
		
		// Match just level 2 splash potions
		PotionQuery regenSplashLvl2 = PotionQuery.fromAny(null, 2, null, true);
		
		Action regenSplashValue = new Action(def, new Range(2));
		Action regenValue = new Action(def, new Range(1));
		Action universalValue = new Action(def, new Range(0));
		
		// Add both to the tree
		tree.put(universal, universalValue);
		tree.put(regenUniversal, regenValue);
		tree.put(regenSplashLvl2, regenSplashValue);
		
		assertEquals(regenSplashValue, tree.get(PotionQuery.fromExact(PotionType.REGEN, 2, false, true)));
		assertEquals(regenSplashValue, tree.get(PotionQuery.fromExact(PotionType.REGEN, 2, true, true)));
		assertEquals(regenValue, tree.get(PotionQuery.fromExact(PotionType.REGEN, 1, true, true)));
		assertEquals(universalValue, tree.get(PotionQuery.fromExact(PotionType.INSTANT_HEAL, 1, false, true)));
	}
}
