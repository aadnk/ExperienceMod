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
		tree.put(regenUniversal, new Range(1));
		tree.put(regenSplashLvl2, new Range(2));
		
		assertEquals(tree.get(new PotionQuery(PotionType.REGEN, 2, false, true)), regenSplashValue);
		assertEquals(tree.get(new PotionQuery(PotionType.REGEN, 2, true, true)), regenSplashValue);
		assertEquals(tree.get(new PotionQuery(PotionType.REGEN, 1, true, true)), regenValue);
		assertEquals(tree.get(new PotionQuery(PotionType.INSTANT_HEAL, 1, false, true)), universalValue);
	}
}
