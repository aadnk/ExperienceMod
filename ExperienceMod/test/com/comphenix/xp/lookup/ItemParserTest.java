package com.comphenix.xp.lookup;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.potion.PotionType;
import org.junit.Test;

import com.comphenix.xp.parser.ItemParser;
import com.comphenix.xp.parser.ParsingException;


public class ItemParserTest {

	@Test
	public void testItems() throws ParsingException {

		int redColor = (int) DyeColor.RED.getData();
		int blueColor = (int) DyeColor.BLUE.getData();
		
		ItemQuery universal = new ItemQuery();
		ItemQuery stoneQuery = new ItemQuery(Material.STONE);
		ItemQuery redWool = new ItemQuery(Material.WOOL, redColor);
		ItemQuery blueStuff = new ItemQuery((Material) null, blueColor);
		ItemQuery redAndBlue = new ItemQuery(Arrays.asList(Material.WOOL.getId()), 
				 							 Arrays.asList(redColor, blueColor));
		
		PotionQuery universalPotion = new PotionQuery();
		PotionQuery levelTwoPotion = new PotionQuery(null, 2, null, null);
		PotionQuery specificPotion = new PotionQuery(PotionType.FIRE_RESISTANCE, 1, true, true);
		
		ItemParser parser = new ItemParser();
		
		assertEquals(universal, parser.parseItemQuery("?"));
		assertEquals(stoneQuery, parser.parseItemQuery("stone"));
		assertEquals(stoneQuery, parser.parseItemQuery("1"));
		assertEquals(redWool, parser.parseItemQuery("wool|14"));
		assertEquals(redWool, parser.parseItemQuery("wool|red"));
		assertEquals(redAndBlue, parser.parseItemQuery("wool|red,blue"));
		assertEquals(blueStuff, parser.parseItemQuery("?|11"));
		
		assertEquals(universalPotion, parser.parseItemQuery("potion|?"));
		assertEquals(levelTwoPotion, parser.parseItemQuery("potion|?|2"));
		assertEquals(specificPotion, parser.parseItemQuery("potion|fire resistance|1|extended|splash"));
	}
}
