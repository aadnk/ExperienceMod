package com.comphenix.xp.parser.text;

import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.comphenix.xp.parser.Parser;
import com.comphenix.xp.parser.ParsingException;
import com.comphenix.xp.parser.Utility;

public class MobDamageCauseParser extends Parser<DamageCause> {

	@Override
	public DamageCause parse(String text) throws ParsingException {
		
		// Extra check
		if (Utility.isNullOrIgnoreable(text)) 
			throw new ParsingException("Text cannot be empty or null.");

		try {
			return DamageCause.valueOf(Utility.getEnumName(text));

		} catch (IllegalArgumentException e) {
			throw ParsingException.fromFormat("Unable to find damage cause %s.", text);
		}
	}
}
