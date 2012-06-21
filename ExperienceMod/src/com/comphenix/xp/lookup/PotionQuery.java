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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.bukkit.Material;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import com.comphenix.xp.parser.Parsing;


public class PotionQuery implements Query {

	// DON'T CARE fields are marked with NULL
	private PotionType type;
	private Integer level;
	private Boolean extended;
	private Boolean splash;
	
	public PotionQuery() {
		// Match all potions
		this(null, null, null, null);
	}
	
	public PotionQuery(PotionType type) {
		this(type, null, null, null);
	}
	
	public PotionQuery(PotionType type, Integer level) {
		this(type, level, null, null);
	}
	
	public PotionQuery(PotionType type, Integer level, Boolean extended, Boolean splash) {
		this.type = type;
		this.level = level;
		this.extended = extended;
		this.splash = splash;
	}
	
	public PotionQuery(Potion potionObject) {
		if (potionObject == null)
			throw new IllegalArgumentException("Potion must be non-zero.");
		
		loadFromPotion(potionObject);
	}
	
	public PotionQuery(ItemQuery query) {
		if (query.getItemID() != Material.POTION.getId())
			throw new IllegalArgumentException("Can only create potion queries from potions.");
		
		Potion potion = Potion.fromDamage(query.getDurability());
		loadFromPotion(potion);
	}

	private void loadFromPotion(Potion source) {
		this.type = source.getType();
		this.level = source.getLevel();
		this.extended = source.hasExtendedDuration();
		this.splash = source.isSplash();
	}
	
	public PotionType getType() {
		return type;
	}

	public Integer getLevel() {
		return level;
	}

	public Boolean getExtended() {
		return extended;
	}

	public Boolean getSplash() {
		return splash;
	}
	
	public boolean hasType() {
		return type != null;
	}
	
	public boolean hasLevel() {
		return level != null;
	}
	
	public boolean hasExtended() {
		return extended != null;
	}
	
	public boolean hasSplash() {
		return splash != null;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).
	            append(type).
	            append(level).
	            append(extended).
	            append(splash).
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

        PotionQuery other = (PotionQuery) obj;
        return new EqualsBuilder().
            append(type, other.type).
            append(level, other.level).
            append(extended, other.extended).
            append(splash, other.splash).
            isEquals();
	}

	@Override
	public String toString() {
		return String.format("Potion|%s|%s|%s|%s", 
						hasType() ? type : "", 
						hasLevel() ? level : "", 
						Parsing.formatBoolean("extended", extended), 
						Parsing.formatBoolean("splash", splash));
	}

	@Override
	public Types getQueryType() {
		return Types.Potions;
	}
}
