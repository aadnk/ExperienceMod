package com.comphenix.xp.parser.text;

import org.bukkit.entity.Skeleton.SkeletonType;

import com.comphenix.xp.parser.ParsingException;
import com.comphenix.xp.parser.TextParser;
import com.comphenix.xp.parser.Utility;

public class MobSkeletonParser extends TextParser<SkeletonType> {

	@Override
	public SkeletonType parse(String text) throws ParsingException {

		// Make sure we're not passed an empty element
		if (Utility.isNullOrIgnoreable(text))
			throw new ParsingException("Text cannot be empty or null.");
		
		Integer id = tryParse(text);
		
		if (id != null) {
			SkeletonType type = SkeletonType.getType(id);
			
			// Validate output
			if (type != null)
				return type;
			else
				throw new ParsingException("The ID " + id + " doesn't represent any skeleton types.");
		} else {
			
			// Try parsing the type as a name
			try {
				return SkeletonType.valueOf(Utility.getEnumName(text));
			} catch (IllegalArgumentException e) {
				throw new ParsingException("The text " + text + " is not a valid skeleton type.", e);
			}
		}
	}
}
