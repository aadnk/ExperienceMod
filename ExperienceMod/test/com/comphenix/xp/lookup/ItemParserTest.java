package com.comphenix.xp.lookup;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.potion.PotionType;
import org.junit.Test;

import com.comphenix.xp.parser.ParsingException;
import com.comphenix.xp.parser.text.ItemParser;


public class ItemParserTest {

	@Test
	public void testItems() throws ParsingException {

		int redColor = (int) DyeColor.RED.getData();
		int blueColor = (int) DyeColor.BLUE.getData();
		
		ItemQuery universal = ItemQuery.fromAny();
		ItemQuery stoneQuery = ItemQuery.fromAny(Material.STONE);
		ItemQuery redWool = ItemQuery.fromAny(Material.WOOL, redColor);
		ItemQuery blueStuff = ItemQuery.fromAny((Material) null, blueColor);
		ItemQuery redAndBlue = new ItemQuery(Arrays.asList(Material.WOOL.getId()), 
				 							 Arrays.asList(redColor, blueColor));
		
		PotionQuery universalPotion = PotionQuery.fromAny();
		PotionQuery levelTwoPotion = PotionQuery.fromAny(null, 2);
		PotionQuery specificPotion = PotionQuery.fromAny(PotionType.FIRE_RESISTANCE, 1, true, true);
		
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
