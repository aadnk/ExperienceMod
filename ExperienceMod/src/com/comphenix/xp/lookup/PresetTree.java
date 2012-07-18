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
	
	public boolean usesPresetNames() {
		for (String param : presetNames.getKeys()) {
			if (param != null)
				return true;
		}
		
		return false;
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
