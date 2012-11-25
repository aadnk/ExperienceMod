package com.comphenix.xp.rewards.items;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.configuration.ConfigurationSection;

import com.comphenix.xp.lookup.ItemQuery;
import com.comphenix.xp.lookup.PotionQuery;
import com.comphenix.xp.lookup.Query;
import com.comphenix.xp.parser.ParsingException;
import com.comphenix.xp.parser.RangeParser;
import com.comphenix.xp.parser.text.ExpressionParser;
import com.comphenix.xp.parser.text.ItemNameParser;
import com.comphenix.xp.parser.text.ItemParser;
import com.comphenix.xp.rewards.ResourceFactory;
import com.comphenix.xp.rewards.ResourcesParser;

public class ItemsParser extends ResourcesParser {
	
	protected RangeParser rangeParser;
	protected ItemParser itemParser;
	
	public ItemsParser(ItemNameParser nameParser, String[] namedParameters) {
		// Initialize parsers
		this(new ItemParser(nameParser), namedParameters);
	}

	public ItemsParser(ItemParser itemParser, String[] namedParameters) {
		this.rangeParser = new RangeParser(new ExpressionParser(namedParameters));
		this.itemParser = itemParser;
	}

	@Override
	public ResourceFactory parse(ConfigurationSection input, String key) throws ParsingException {
		
		if (input == null)
			throw new NullArgumentException("input");
		if (key == null)
			throw new NullArgumentException("key");
		
		// Move to the next section
		if (key != null && input.isConfigurationSection(key)) {
			input = input.getConfigurationSection(key);
		}
		
		ItemsFactory factory = new ItemsFactory();
		
		for (String node : input.getKeys(false)) {
				
			Query query = itemParser.parse(node);

			// Handle both item queries and potion queries
			if (query instanceof ItemQuery)
				factory.addItems((ItemQuery) query, rangeParser.parse(input, node));
			else if (query instanceof PotionQuery)
				factory.addItems(((PotionQuery) query).toItemQuery(true), rangeParser.parse(input, node));
			else
				throw ParsingException.fromFormat("Unable to parse range on item %s.", key);
		}
		
		return factory;
	}

	public RangeParser getRangeParser() {
		return rangeParser;
	}

	public ItemParser getItemParser() {
		return itemParser;
	}

	@Override
	public ResourcesParser withParameters(String[] namedParameters) {
		return new ItemsParser(itemParser, namedParameters);
	}
}
