package com.comphenix.xp.lookup;

import static org.junit.Assert.*;

import org.bukkit.Material;
import org.junit.Test;

import com.comphenix.xp.Range;

public class ItemTreeTest {

	@Test
	public void testItemQuerying() {
		ItemTree tree = new ItemTree();
		
		ItemQuery universal = new ItemQuery();
		ItemQuery stone = new ItemQuery(Material.STONE, null);
		
		Range universalValue = new Range(0);
		Range stoneValue = new Range(1);
		
		tree.put(universal, universalValue);
		tree.put(stone, stoneValue);
		
		assertEquals(tree.get(new ItemQuery(Material.STONE.getId(), 1)), stoneValue);
		assertEquals(tree.get(new ItemQuery(Material.WOOD.getId(), 0)), universalValue);
	}
}
