package com.comphenix.xp.lookup;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.Arrays;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.BeforeClass;
import org.junit.Test;

import com.comphenix.xp.Configuration;
import com.comphenix.xp.Debugger;
import com.comphenix.xp.Range;
import com.comphenix.xp.parser.ParsingException;

public class ItemTreeTest {

	private static final int REPEAT_COUNT = 1000;
	private static Configuration configuration;
	
	@BeforeClass
    public static void loadDefaultConfiguration() {
		InputStream file = ItemTreeTest.class.getResourceAsStream("/config.yml");
		YamlConfiguration defaultFile = YamlConfiguration.loadConfiguration(file);
		
		Debugger injected = new Debugger() {
			public void printWarning(Object sender, String message, Object... params) {
				// Let the tester know about the problem
				fail(String.format(message, params));
			}
			
			// We don't care about debug
			public void printDebug(Object sender, String message, Object... params) { }
			public boolean isDebugEnabled() { return false; }
		};
		
		// Load the default configuration
		configuration = new Configuration(defaultFile, injected);
    }
	
	@Test
	public void testItemMerging() throws ParsingException {
		
		ItemQuery universal = ItemQuery.fromAny();
		ItemQuery stoneQuery = ItemQuery.fromAny(Material.STONE);
		
		ItemTree tree1 = new ItemTree(1);
		ItemTree tree2 = new ItemTree(2);
		ItemTree result = new ItemTree(1);
		
		Range universalValue = new Range(0);
		Range stoneValue = new Range(1);
		
		tree1.put(stoneQuery, stoneValue);
		tree2.put(universal, universalValue);
		
		result.putAll(tree1);
		result.putAll(tree2);
		
		assertEquals(stoneValue, result.get(ItemQuery.fromExact(Material.STONE.getId(), 1)));
		assertEquals(universalValue, result.get(ItemQuery.fromExact(Material.WOOD.getId(), 0)));
	}
	
	@Test
	public void testItemQuerying() {
		ItemTree tree = new ItemTree(1);
		
		int redColor = (int) DyeColor.RED.getData();
		int blueColor = (int) DyeColor.BLUE.getData();
		int brownColor = (int) DyeColor.BROWN.getData();
		
		ItemQuery universal = ItemQuery.fromAny();
		ItemQuery stone = ItemQuery.fromAny(Material.STONE, null);
		ItemQuery redWool = new ItemQuery(
				Arrays.asList(Material.WOOL.getId()), 
				Arrays.asList(redColor, blueColor));
		
		Range universalValue = new Range(0);
		Range stoneValue = new Range(1);
		Range redValue = new Range(5);
		
		tree.put(universal, universalValue);
		tree.put(stone, stoneValue);
		tree.put(redWool, redValue);
		
		assertEquals(stoneValue, tree.get(ItemQuery.fromAny(Material.STONE.getId(), null)));
		assertEquals(stoneValue, tree.get(ItemQuery.fromExact(Material.STONE.getId(), 1)));
		assertEquals(universalValue, tree.get(ItemQuery.fromExact(Material.WOOD.getId(), 0)));
		assertEquals(redValue, tree.get(ItemQuery.fromExact(Material.WOOL.getId(), redColor)));
		assertEquals(redValue, tree.get(ItemQuery.fromExact(Material.WOOL.getId(), blueColor)));
		assertEquals(universalValue, tree.get(ItemQuery.fromExact(Material.WOOL.getId(), brownColor)));
	}
	
	@Test
	public void testItemSpeed() {
		
		ItemQuery diamondQuery = ItemQuery.fromAny(Material.DIAMOND_ORE);
		Range lastRange = null;
		
		// Assuming no errors, try searching for diamond a couple of times
		for (int i = 0; i < REPEAT_COUNT; i++) {
			lastRange = configuration.getSimpleBlockReward().get(diamondQuery);
		}
		
		// The default configuration should have it
		assertNotNull(lastRange);
	}
}
