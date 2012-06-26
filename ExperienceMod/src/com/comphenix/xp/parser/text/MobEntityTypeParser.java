package com.comphenix.xp.parser.text;

import org.bukkit.entity.EntityType;

import com.comphenix.xp.parser.Parser;
import com.comphenix.xp.parser.ParsingException;
import com.comphenix.xp.parser.Utility;

public class MobEntityTypeParser extends Parser<EntityType> {

	@Override
	public EntityType parse(String text) throws ParsingException {
		
		// Make sure we're not passed an empty element
		if (Utility.isNullOrIgnoreable(text))
			throw new ParsingException("Text cannot be empty or null.");
		
		EntityType type = EntityType.fromName(text);
		
		if (type == null) {
			throw ParsingException.fromFormat("Unable to find a mob with the name %s.", text);
			
		} else if (type != null) {
			if (!type.isAlive())
				throw ParsingException.fromFormat("%s is not a mob.", text);
		}
		
		return type;
	}
}
