package com.comphenix.xp.lookup;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.junit.Test;

import com.comphenix.xp.Range;

public class ItemTreeTest {

	@Test
	public void testItemQuerying() {
		ItemTree tree = new ItemTree();
		
		int redColor = (int) DyeColor.RED.getData();
		int blueColor = (int) DyeColor.BLUE.getData();
		int brownColor = (int) DyeColor.BROWN.getData();
		
		ItemQuery universal = new ItemQuery();
		ItemQuery stone = new ItemQuery(Material.STONE, null);
		ItemQuery redWool = new ItemQuery(
				Arrays.asList(Material.WOOL.getId()), 
				Arrays.asList(redColor, blueColor));
		
		Range universalValue = new Range(0);
		Range stoneValue = new Range(1);
		Range redValue = new Range(5);
		
		tree.put(universal, universalValue);
		tree.put(stone, stoneValue);
		tree.put(redWool, redValue);
		
		assertEquals(stoneValue, tree.get(new ItemQuery(Material.STONE.getId(), 1)));
		assertEquals(universalValue, tree.get(new ItemQuery(Material.WOOD.getId(), 0)));
		assertEquals(redValue, tree.get(new ItemQuery(Material.WOOL.getId(), redColor)));
		assertEquals(redValue, tree.get(new ItemQuery(Material.WOOL.getId(), blueColor)));
		assertEquals(universalValue, tree.get(new ItemQuery(Material.WOOL.getId(), brownColor)));
	}
	
	@Test
	public void testItemSpeed() {
		
		
	}
}
