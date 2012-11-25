/*
 *  ExperienceMod - Bukkit server plugin for modifying the experience system in Minecraft.
 *  Copyright (C) 2012 Kristian S. Stangeland
 *
 *  This program is free software; you can redistribute it and/or modify it under the terms of the 
 *  GNU General Public License as published by the Free Software Foundation; either version 2 of 
 *  the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with this program; 
 *  if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 
 *  02111-1307 USA
 */

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
