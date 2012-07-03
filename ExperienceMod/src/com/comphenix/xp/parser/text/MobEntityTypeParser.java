package com.comphenix.xp.parser.text;

import org.bukkit.entity.EntityType;

import com.comphenix.xp.parser.TextParser;
import com.comphenix.xp.parser.ParsingException;
import com.comphenix.xp.parser.Utility;
import com.comphenix.xp.parser.text.MobMatcher.Category;

public class MobEntityTypeParser extends TextParser<MobMatcher> {

	@Override
	public MobMatcher parse(String text) throws ParsingException {
		
		// Make sure we're not passed an empty element
		if (Utility.isNullOrIgnoreable(text))
			throw new ParsingException("Text cannot be empty or null.");
		
		Category possibleCategory = MobMatcher.Category.fromName(text);
		EntityType type = EntityType.fromName(text);
		
		// Check for invalid entries
		if (possibleCategory == null) {
			if (type == null) {
				throw ParsingException.fromFormat("Unable to find a mob with the name %s.", text);
				
			} else if (type != null) {
				if (!type.isAlive())
					throw ParsingException.fromFormat("%s is not a mob.", text);
			}
		} else {
			if (possibleCategory == Category.SPECIFIC)
				throw ParsingException.fromFormat("%s is not a mob nor a mob category.");
		}
		
		// It's either a category or a specific mob
		if (possibleCategory != null)
			return new MobMatcher(possibleCategory);
		else
			return new MobMatcher(type);
	}
}
