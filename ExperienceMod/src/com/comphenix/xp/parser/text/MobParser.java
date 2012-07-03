package com.comphenix.xp.parser.text;

/**
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

import java.util.List;
import java.util.Queue;

import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.comphenix.xp.lookup.MobQuery;
import com.comphenix.xp.parser.TextParser;
import com.comphenix.xp.parser.ParsingException;
import com.comphenix.xp.parser.primitives.BooleanParser;

public class MobParser extends TextParser<MobQuery> {
	
	private ParameterParser<EntityType> entityTypeParser = new ParameterParser<EntityType>(new MobEntityTypeParser());
	private ParameterParser<DamageCause> damageCauseParser = new ParameterParser<DamageCause>(new MobDamageCauseParser());
	
	private BooleanParser spawnerParser = new BooleanParser("spawner");
	private BooleanParser babyParser = new BooleanParser("baby");
	private BooleanParser tamedParser = new BooleanParser("tamed");
	private BooleanParser playerParser = new BooleanParser("player");
	
	@Override
	public MobQuery parse(String text) throws ParsingException {
		
		Queue<String> tokens = getParameterQueue(text);
		
		ParsingException errorReason = null;
		
		List<EntityType> types = null;
		List<DamageCause> causes = null;
		
		try {
			types = entityTypeParser.parse(tokens);
			causes = damageCauseParser.parse(tokens);
			
		} catch (ParsingException e) {
			// Try more
			errorReason = e;
		}
		
		// Scan all unused parameters for these options first
		List<Boolean> spawner = spawnerParser.parseAny(tokens);
		List<Boolean> baby = babyParser.parseAny(tokens);
		List<Boolean> tamed = tamedParser.parseAny(tokens);
		List<Boolean> player = playerParser.parseAny(tokens);

		// If there are some tokens left, a problem occured
		if (!tokens.isEmpty()) {
			
			// Let the user know about the reason too
			if (errorReason != null)
				throw errorReason;
			else
				throw ParsingException.fromFormat("Unknown item tokens: %s", StringUtils.join(tokens, ", "));
		}
		
		return new MobQuery(types, causes, spawner, baby, tamed, player);
	}
}
