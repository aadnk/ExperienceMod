package com.comphenix.xp.lookup;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.comphenix.xp.Action;

public class PlayerTree extends ActionTree<PlayerQuery> implements Multipliable<PlayerTree> {

	// DON'T CARE fields are marked with NULL
	protected Parameter<String> names;
	protected Parameter<String> groups;
	protected Parameter<DamageCause> deathCause;
	protected Parameter<Boolean> murder;

	// For cloning
	protected PlayerTree(PlayerTree other, double newMultiplier) { 
		super(other, newMultiplier);
		
		if (other == null)
			throw new IllegalArgumentException("other");
		
		this.names = other.names;
		this.groups = other.groups;
		this.deathCause = other.deathCause;
		this.murder = other.murder;
	}
	
	public PlayerTree(double multiplier) {
		super(multiplier);
		this.names = new Parameter<String>();
		this.groups = new Parameter<String>();
		this.deathCause = new Parameter<DamageCause>();
		this.murder = new Parameter<Boolean>();
	}

	@Override
	protected Integer putFromParameters(PlayerQuery source, Integer id) {

		int paramCount = 0;
		
		if (source.hasNames()) {
			names.put(source.getNames(), id); paramCount++;
		}
		
		if (source.hasGroups()) {
			groups.put(source.getGroups(), id); paramCount++;
		}
		
		if (source.hasDeathCause()) {
			deathCause.put(source.getDeathCause(), id); paramCount++;
		}
		
		if (source.hasMurder()) {
			murder.put(source.getMurder(), id); paramCount++;
		}
		
		return paramCount;
	}

	@Override
	protected Set<Integer> getFromParameters(PlayerQuery source) {

		Set<Integer> candidates = new HashSet<Integer>(flatten.keySet());
		
		if (source.hasNames()) {
			names.retain(candidates, source.getNames());
		}
		
		if (source.hasGroups()) {
			groups.retain(candidates, source.getGroups());
		}
		
		if (source.hasDeathCause()) {
			deathCause.retain(candidates, source.getDeathCause());
		}
		
		if (source.hasMurder()) {
			murder.retain(candidates, source.getMurder());
		}
		
		return candidates;	
	}
	
	@Override
	protected void putAllParameters(SearchTree<PlayerQuery, Action> source, Integer offset) {

		PlayerTree tree = (PlayerTree) source;

		names.putAll(tree.names, offset);
		groups.putAll(tree.groups, offset);
		deathCause.putAll(tree.deathCause, offset);
		murder.putAll(tree.murder, offset);
	}
	
	@Override
	public PlayerTree withMultiplier(double newMultiplier) {
		return new PlayerTree(this, newMultiplier);
	}
}
