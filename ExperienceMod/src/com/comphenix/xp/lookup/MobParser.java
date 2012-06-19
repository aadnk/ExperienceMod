package com.comphenix.xp.lookup;

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

import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class MobParser {
	
	public MobQuery fromString(String text) throws ParsingException {
		
		String[] components = Parsing.getParameterArray(text);
		String mobName = components[0];
		
		EntityType type = EntityType.fromName(mobName);
		
		if (type == null && !Parsing.isNullOrIgnoreable(mobName)) {
			throw ParsingException.fromFormat("Unable to find a mob with the name %s.", mobName);
		} else if (type != null && !type.isAlive()) {
			throw ParsingException.fromFormat("%s is not a mob.", mobName);
		} else {
			
			DamageCause cause = getDamageCause(components, 1);
			int offset = 0;
			
			// ToDo: Parse using a stack instead.
			if (cause != null)
				offset = 2;
			else if (mobName != null)
				offset = 1;
			
			Boolean spawner = Parsing.hasElementPrefix(components, offset, "spawner");
			Boolean baby = Parsing.hasElementPrefix(components, offset, "baby");
			Boolean tamed = Parsing.hasElementPrefix(components, offset, "tamed");
			SpawnReason reason = spawner != null ? (spawner ? SpawnReason.SPAWNER : SpawnReason.NATURAL) : null;
			
			return new MobQuery(type, cause, reason , baby, tamed);
		}
	}
	
	private DamageCause getDamageCause(String[] components, int index) {
		
		try {
			if (index < components.length && !Parsing.isNullOrIgnoreable(components[index]))
				return DamageCause.valueOf(Parsing.getEnumName(components[index]));
			else
				 // Empty = ignore parameter
				return null;
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
}
