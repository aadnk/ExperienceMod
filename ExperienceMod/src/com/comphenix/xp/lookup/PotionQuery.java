package com.comphenix.xp.lookup;

import org.bukkit.Material;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

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
