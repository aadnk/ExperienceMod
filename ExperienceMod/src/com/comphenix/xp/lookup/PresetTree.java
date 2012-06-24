package com.comphenix.xp.lookup;

import java.util.HashSet;
import java.util.Set;

import com.comphenix.xp.Configuration;

public class PresetTree extends SearchTree<PresetQuery, Configuration> {

	private Parameter<String> presetNames;
	private Parameter<String> worlds;
	
	public PresetTree() {
		this.presetNames = new Parameter<String>();
		this.worlds = new Parameter<String>();
	}
	
	@Override
	protected void putAllParameters(SearchTree<PresetQuery, Configuration> other, Integer offset) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Integer putFromParameters(PresetQuery source, Integer id) {

		int paramCount = 0;
		
		if (source.hasPresetNames()) {
			presetNames.put(source.getPresetNames(), id); paramCount++;
		}
		
		if (source.hasWorlds()) {
			worlds.put(source.getWorlds(), id); paramCount++;
		}

		return paramCount;
	}

	@Override
	protected Set<Integer> getFromParameters(PresetQuery source) {

		// Begin with the item IDs this can correspond to
		Set<Integer> candidates = new HashSet<Integer>(flatten.keySet());
		
		if (source.hasPresetNames())
			presetNames.retain(candidates, source.getPresetNames());
		
		// Remove items that contain conflicting worlds
		if (source.hasWorlds())
			worlds.retain(candidates, source.getWorlds());
		
		// Any remaining items will be sorted by specificity
		return candidates;
	}
}
