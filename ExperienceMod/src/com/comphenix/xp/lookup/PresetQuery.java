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

import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.comphenix.xp.parser.Utility;
import com.google.common.collect.Lists;

public class PresetQuery implements Query {
	private List<String> presetNames;
	private List<String> worlds;
	
	/**
	 * Univeral query.
	 */
	public static PresetQuery fromAny() {
		return fromAny(null, null);
	}
	
	public static PresetQuery fromAny(String presetName, String world) {
		return new PresetQuery(
				Utility.getElementList(presetName),
				Utility.getElementList(world)
		);
	}
	
	public static PresetQuery fromExact(String presetName, String world) {
		return new PresetQuery(
				Lists.newArrayList(presetName), 
				Lists.newArrayList(world)
		);
	}
	
	public static PresetQuery fromExact(List<String> presetName, String world) {
		return new PresetQuery(
				presetName, 
				Lists.newArrayList(world)
		);
	}
	
	public PresetQuery(List<String> presetNames, List<String> worlds) {
		this.presetNames = presetNames;
		this.worlds = worlds;
	}
	
	public boolean hasPresetNames() {
		return presetNames != null && !presetNames.isEmpty();
	}

	public boolean hasWorlds() {
		return worlds != null && !worlds.isEmpty();
	}
	
	public List<String> getPresetNames() {
		return presetNames;
	}

	public List<String> getWorlds() {
		return worlds;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).
	            append(presetNames).
	            append(worlds).
	            toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (obj.getClass() != getClass())
            return false;

        PresetQuery other = (PresetQuery) obj;
        return new EqualsBuilder().
            append(presetNames, other.presetNames).
            append(worlds, other.worlds).
            isEquals();
	}
	
	@Override
	public boolean match(Query other) {

		// Yes, we could construct a MobTree, put the current MobQuery 
		// into it and query after other, but this is faster. Probably.
		if (other instanceof PresetQuery) {
			PresetQuery query = (PresetQuery) other;
			
			// Make sure the current query is the superset of other
			return QueryMatching.matchParameter(presetNames, query.presetNames) &&
				   QueryMatching.matchParameter(worlds, query.worlds);
		}
		
		// Query must be of the same type
		return false;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);  
	}
	
	@Override
	public Types getQueryType() {
		return Types.CONFIGURATIONS;
	}
}
