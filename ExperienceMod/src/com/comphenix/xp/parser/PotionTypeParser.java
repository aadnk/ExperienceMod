package com.comphenix.xp.parser;

import org.bukkit.potion.PotionType;

public class PotionTypeParser extends Parser<PotionType> {

	@Override
	public PotionType parse(String text) throws ParsingException {
		
		// Check for DON'T CARE
		if (Parsing.isNullOrIgnoreable(text)) 
			throw new ParsingException("Text cannot be empty or null.");
		
		Integer potionID = Parsing.tryParse(text);
		
		try {
			// Parse the potion type
			if (potionID != null) {
				return PotionType.getByDamageValue(potionID);
			} else {
				return PotionType.valueOf(Parsing.getEnumName(text));
			}
			
		} catch (IllegalArgumentException e) {
			
			// Handle ID failure and name failure
			if (potionID == null)
				throw ParsingException.fromFormat("Unrecognized potion id: %d", potionID);
			else
				throw ParsingException.fromFormat("Unrecognized potion name: %s.", text);
		}
	}
}
