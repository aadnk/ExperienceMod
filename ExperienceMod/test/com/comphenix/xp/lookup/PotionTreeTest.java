package com.comphenix.xp.lookup;

import static org.junit.Assert.*;

import org.bukkit.potion.PotionType;
import org.junit.Test;

import com.comphenix.xp.Range;

public class PotionTreeTest {

	@Test
	public void test() {
		PotionTree tree = new PotionTree();
		
		// Match every regen
		PotionQuery regenUniversal = new PotionQuery(PotionType.REGEN);
		PotionQuery universal = new PotionQuery();
		
		// Match just level 2 splash potions
		PotionQuery regenSplashLvl2 = new PotionQuery(null, 2, null, true);
		
		Range regenSplashValue = new Range(2);
		Range regenValue = new Range(1);
		Range universalValue = new Range(0);
		
		// Add both to the tree
		tree.put(universal, universalValue);
		tree.put(regenUniversal, regenValue);
		tree.put(regenSplashLvl2, regenSplashValue);
		
		assertEquals(regenSplashValue, tree.get(new PotionQuery(PotionType.REGEN, 2, false, true)));
		assertEquals(regenSplashValue, tree.get(new PotionQuery(PotionType.REGEN, 2, true, true)));
		assertEquals(regenValue, tree.get(new PotionQuery(PotionType.REGEN, 1, true, true)));
		assertEquals(universalValue, tree.get(new PotionQuery(PotionType.INSTANT_HEAL, 1, false, true)));
	}
}
