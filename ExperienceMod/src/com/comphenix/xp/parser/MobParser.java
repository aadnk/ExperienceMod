package com.comphenix.xp.parser;

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
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionType;

import com.comphenix.xp.lookup.MobQuery;
import com.comphenix.xp.lookup.PotionQuery;

public class MobParser {
	
	private ParameterParser<EntityType> entityTypeParser = new ParameterParser<EntityType>(new MobEntityTypeParser());
	private ParameterParser<DamageCause> damageCauseParser = new ParameterParser<DamageCause>(new MobDamageCauseParser());
	
	private BooleanParser spawnerParser = new BooleanParser("spawner");
	private BooleanParser babyParser = new BooleanParser("baby");
	private BooleanParser tamedParser = new BooleanParser("tamed");
	
	public MobQuery fromString(String text) throws ParsingException {
		
		Queue<String> tokens = Parsing.getParameterQueue(text);
		
		ParsingException reason = null;
		
		List<EntityType> types = null;
		List<DamageCause> causes = null;
		
		try {
			types = entityTypeParser.parse(tokens);
			causes = damageCauseParser.parse(tokens);
			
		} catch (ParsingException e) {
			// Try more
			reason = e;
		}
		
		// Scan all unused parameters for these options first
		Boolean spawner = spawnerParser.parseAny(tokens);
		Boolean baby = babyParser.parseAny(tokens);
		Boolean tamed = tamedParser.parseAny(tokens);
		SpawnReason spawnReason = spawner != null ? (spawner ? SpawnReason.SPAWNER : SpawnReason.NATURAL) : null;
		
		// If there are some tokens left, a problem occured
		if (!tokens.isEmpty()) {
			
			// Let the user know about the reason too
			if (reason != null)
				throw reason;
			else
				throw ParsingException.fromFormat("Unknown item tokens: ", StringUtils.join(tokens, ", "));
		}
		
		return new MobQuery(types, causes, reason , baby, tamed);
	}
}
