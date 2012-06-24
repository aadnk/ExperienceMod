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
	
	public PresetQuery(String presetName, String world) {
		presetNames = Utility.getElementList(presetName);
		worlds = Utility.getElementList(world);
	}

	public PresetQuery(List<String> presetNames, List<String> worlds) {
		this.presetNames = presetNames;
		this.worlds = worlds;
	}
	
	public static PresetQuery fromExact(String presetName, String world) {
		return new PresetQuery(
				Lists.newArrayList(presetName), 
				Lists.newArrayList(world)
		);
	}
	
	/**
	 * Univeral query.
	 */
	public PresetQuery() {
		this((String) null, (String) null);
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
	public String toString() {
		return ToStringBuilder.reflectionToString(this);  
	}
	
	@Override
	public Types getQueryType() {
		return Types.Configurations;
	}
}
