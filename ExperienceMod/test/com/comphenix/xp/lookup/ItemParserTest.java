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
		
		assertEquals(universal, parser.parse("?"));
		assertEquals(stoneQuery, parser.parse("stone"));
		assertEquals(stoneQuery, parser.parse("1"));
		assertEquals(redWool, parser.parse("wool|14"));
		assertEquals(redWool, parser.parse("wool|red"));
		assertEquals(redAndBlue, parser.parse("wool|red,blue"));
		assertEquals(blueStuff, parser.parse("?|11"));
		
		assertEquals(universalPotion, parser.parse("potion|?"));
		assertEquals(levelTwoPotion, parser.parse("potion|?|2"));
		assertEquals(specificPotion, parser.parse("potion|fire resistance|1|extended|splash"));
	}
}
