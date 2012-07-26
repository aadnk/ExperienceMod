package com.comphenix.xp.lookup;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.BeforeClass;
import org.junit.Test;

import com.comphenix.xp.Action;
import com.comphenix.xp.ActionTypes;
import com.comphenix.xp.Configuration;
import com.comphenix.xp.Debugger;
import com.comphenix.xp.Range;
import com.comphenix.xp.messages.ChannelProvider;
import com.comphenix.xp.parser.ParsingException;
import com.comphenix.xp.parser.text.ItemNameParser;
import com.comphenix.xp.parser.text.ItemParser;
import com.comphenix.xp.parser.text.MobMatcher;
import com.comphenix.xp.parser.text.MobParser;
import com.comphenix.xp.rewards.RewardProvider;
import com.comphenix.xp.rewards.RewardTypes;

public class ItemTreeTest {

	private static final int REPEAT_COUNT = 1000;
	private static Configuration configuration;
	
	@BeforeClass
    public static void loadDefaultConfiguration() throws FileNotFoundException {
		//InputStream file = new FileInputStream("E:\\Games\\Minecraft\\Test Server\\plugins\\ExperienceMod\\config.yml");
		InputStream file = ItemTreeTest.class.getResourceAsStream("/config.yml");
		YamlConfiguration defaultFile = YamlConfiguration.loadConfiguration(file);
		RewardProvider rewards = new RewardProvider();
		ChannelProvider channels = new ChannelProvider();
		rewards.setDefaultReward(RewardTypes.EXPERIENCE);
		
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
		configuration = new Configuration(injected, rewards, channels);
		configuration.setItemParser(new ItemParser(new ItemNameParser()));
		configuration.setMobParser(new MobParser(new MobMatcher()));
		configuration.setActionTypes(ActionTypes.Default());
		configuration.loadFromConfig(defaultFile);
    }
	
	@Test
	public void testItemMerging() throws ParsingException {
		
		int redColor = (int) DyeColor.RED.getData();
		int blueColor = (int) DyeColor.BLUE.getData();
		String def = "EXPERIENCE";
		
		ItemQuery universal = ItemQuery.fromAny();
		ItemQuery stoneQuery = ItemQuery.fromAny(Material.STONE);
		ItemQuery redWool = new ItemQuery(
				Arrays.asList(Material.WOOL.getId()), 
				Arrays.asList(redColor, blueColor));
		
		ItemTree tree1 = new ItemTree(1);
		ItemTree tree2 = new ItemTree(2);
		ItemTree tree3 = new ItemTree(3);
		ItemTree result = new ItemTree(1);
		
		Action universalValue = new Action(def, new Range(0));
		Action stoneValue = new Action(def,new Range(1));
		Action redValue = new Action(def,new Range(5));
		
		tree1.put(stoneQuery, stoneValue);
		tree2.put(universal, universalValue);
		tree3.put(redWool, redValue);
		
		result.putAll(tree1);
		result.putAll(tree2);
		result.putAll(tree3);
		
		assertEquals(stoneValue, result.get(ItemQuery.fromExact(Material.STONE.getId(), 1)));
		assertEquals(universalValue, result.get(ItemQuery.fromExact(Material.WOOD.getId(), 0)));
		assertEquals(redValue, result.get(ItemQuery.fromExact(Material.WOOL.getId(), redColor)));
	}
	
	@Test
	public void testItemQuerying() {
		ItemTree tree = new ItemTree(1);
		
		int redColor = (int) DyeColor.RED.getData();
		int blueColor = (int) DyeColor.BLUE.getData();
		int brownColor = (int) DyeColor.BROWN.getData();
		String def = "EXPERIENCE";
		
		ItemQuery universal = ItemQuery.fromAny();
		ItemQuery stone = ItemQuery.fromAny(Material.STONE, null);
		ItemQuery redWool = new ItemQuery(
				Arrays.asList(Material.WOOL.getId()), 
				Arrays.asList(redColor, blueColor));
		
		Action universalValue = new Action(def, new Range(0));
		Action stoneValue = new Action(def, new Range(1));
		Action redValue = new Action(def, new Range(5));
		
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
		Action lastAction = null;
		
		// Assuming no errors, try searching for diamond a couple of times
		for (int i = 0; i < REPEAT_COUNT; i++) {
			lastAction = configuration.getSimpleBlockReward().get(diamondQuery);
		}
		
		// The default configuration should have it
		assertNotNull(lastAction);
	}
}
