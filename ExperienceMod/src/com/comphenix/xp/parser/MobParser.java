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

import java.util.Queue;

import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.comphenix.xp.lookup.MobQuery;

public class MobParser {
	
	public MobQuery fromString(String text) throws ParsingException {
		
		Queue<String> components = Parsing.getParameterQueue(text);
		EntityType type = parseEntityType(components);	
		DamageCause cause = getDamageCause(components);
		
		Boolean spawner = Parsing.hasElementPrefix(components, "spawner");
		Boolean baby = Parsing.hasElementPrefix(components, "baby");
		Boolean tamed = Parsing.hasElementPrefix(components, "tamed");
		SpawnReason reason = spawner != null ? (spawner ? SpawnReason.SPAWNER : SpawnReason.NATURAL) : null;
		
		return new MobQuery(type, cause, reason , baby, tamed);
	}
	
	private EntityType parseEntityType(Queue<String> components) throws ParsingException {
		
		// Check for DON'T CARE
		if (Parsing.isNullOrIgnoreable(components)) {
			return null;
		}
		
		String mobName = components.peek();
		EntityType type = EntityType.fromName(mobName);
		
		if (type == null) {
			throw ParsingException.fromFormat("Unable to find a mob with the name %s.", mobName);
			
		} else if (type != null) {
			if (!type.isAlive())
				throw ParsingException.fromFormat("%s is not a mob.", mobName);
			
			components.remove();
		}
		
		return type;
	}
	
	private DamageCause getDamageCause(Queue<String> components) {
		
		// Check for DON'T CARE
		if (Parsing.isNullOrIgnoreable(components)) {
			return null;
		}
		
		try {
			String current = components.peek();
			DamageCause cause = DamageCause.valueOf(Parsing.getEnumName(current));
		
			components.remove();
			return cause;
			
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
}
