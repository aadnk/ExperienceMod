package com.comphenix.xp.lookup;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.comphenix.xp.Range;

public class MobTree extends SearchTree<MobQuery, Range> {

	// DON'T CARE fields are marked with NULL
	private Parameter<EntityType> type = new Parameter<EntityType>();
	private Parameter<DamageCause> deathCause = new Parameter<DamageCause>();
	private Parameter<Boolean> spawner = new Parameter<Boolean>();
	private Parameter<Boolean> baby = new Parameter<Boolean>();
	private Parameter<Boolean> tamed = new Parameter<Boolean>();
	
	@Override
	protected Integer putFromParameters(MobQuery source, Integer id) {

		int paramCount = 0;
		
		if (source.hasType()) {
			type.put(source.getType(), id); paramCount++;
		}
		
		if (source.hasDeathCause()) {
			deathCause.put(source.getDeathCause(), id); paramCount++;
		}
		
		if (source.hasSpawner()) {
			spawner.put(source.getSpawner(), id); paramCount++;
		}
		
		if (source.hasBaby()) {
			baby.put(source.getBaby(), id); paramCount++;
		}
		
		if (source.hasTamed()) {
			tamed.put(source.getTamed(), id); paramCount++;
		}
		
		return paramCount;
	}

	@Override
	protected Set<Integer> getFromParameters(MobQuery source) {

		Set<Integer> candidates = new HashSet<Integer>(flatten.keySet());
		
		if (source.hasType()) {
			type.retain(candidates, source.getType());
		}
		
		if (source.hasDeathCause()) {
			deathCause.retain(candidates, source.getDeathCause());
		}
		
		if (source.hasSpawner()) {
			spawner.retain(candidates, source.getSpawner());
		}
		
		if (source.hasBaby()) {
			baby.retain(candidates, source.getBaby());
		}
		
		if (source.hasTamed()) {
			tamed.retain(candidates, source.getTamed());
		}
		
		return candidates;
	}
}
