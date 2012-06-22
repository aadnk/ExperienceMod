package com.comphenix.xp.parser;

import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class MobDamageCauseParser implements Parser<DamageCause> {

	@Override
	public DamageCause Parse(String text) throws ParsingException {
		
		// Extra check
		if (Parsing.isNullOrIgnoreable(text)) 
			throw new ParsingException("Text cannot be empty or null.");

		try {
			return DamageCause.valueOf(Parsing.getEnumName(text));

		} catch (IllegalArgumentException e) {
			throw ParsingException.fromFormat("Unable to find damage cause %s.", text);
		}
	}
}
