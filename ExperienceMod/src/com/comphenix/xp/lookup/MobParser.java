package com.comphenix.xp.lookup;

import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class MobParser {
	
	public MobQuery fromString(String text) {
		
		String[] components = Parsing.getParameterArray(text);
		String mobName = components[0];
		
		EntityType type = EntityType.fromName(mobName);
		
		if (type == null && !Parsing.isNullOrIgnoreable(mobName)) {
			throw new IllegalArgumentException(String.format("Unable to find a mob with the name %s.", mobName));
		} else if (type != null && !type.isAlive()) {
			throw new IllegalArgumentException(String.format("%s is not a mob.", mobName));
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
