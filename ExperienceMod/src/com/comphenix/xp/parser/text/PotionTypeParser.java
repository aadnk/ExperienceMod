package com.comphenix.xp.parser.text;

import org.bukkit.potion.PotionType;

import com.comphenix.xp.parser.TextParser;
import com.comphenix.xp.parser.ParsingException;
import com.comphenix.xp.parser.Utility;

public class PotionTypeParser extends TextParser<PotionType> {

	@Override
	public PotionType parse(String text) throws ParsingException {
		
		// Check for DON'T CARE
		if (Utility.isNullOrIgnoreable(text)) 
			throw new ParsingException("Text cannot be empty or null.");
		
		Integer potionID = tryParse(text);
		
		try {
			// Parse the potion type
			if (potionID != null) {
				return PotionType.getByDamageValue(potionID);
			} else {
				return PotionType.valueOf(Utility.getEnumName(text));
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
