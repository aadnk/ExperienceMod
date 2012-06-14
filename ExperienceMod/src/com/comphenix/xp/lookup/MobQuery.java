package com.comphenix.xp.lookup;

import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Tameable;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class MobQuery implements Query {

	// DON'T CARE fields are marked with NULL
	private EntityType type;
	private DamageCause deathCause;
	private Boolean spawner;
	private Boolean baby;
	private Boolean tamed;
	
	public MobQuery(EntityType type) {
		this(type, null, null, null, null);
	}
	
	public MobQuery(EntityType type, DamageCause deathCause, SpawnReason reason, Boolean baby, Boolean tamed) {
		this.type = type;
		this.deathCause = deathCause;
		this.spawner = reason == SpawnReason.SPAWNER;
		this.baby = baby;
		this.tamed = tamed;
	}
	
	public MobQuery(LivingEntity entity, SpawnReason reason) {
		
		EntityDamageEvent cause = entity.getLastDamageCause();
		
		this.type = entity.getType();
		this.spawner = reason == SpawnReason.SPAWNER;
		
		if (cause != null) {
			this.deathCause = cause.getCause();
		}
		// Check age and tame status
		if (entity instanceof Ageable) {
			this.baby = !((Ageable) entity).isAdult();
		}
		if (entity instanceof Tameable) {
			this.tamed = ((Tameable)entity).isTamed();
		}
	}
	
	public DamageCause getDeathCause() {
		return deathCause;
	}
	
	public EntityType getType() {
		return type;
	}
	
	public Boolean getSpawner() {
		return spawner;
	}
	
	public Boolean getBaby() {
		return baby;
	}
	
	public Boolean getTamed() {
		return tamed;
	}
	
	public boolean hasType() {
		return type != null;
	}
	
	public boolean hasDeathCause() {
		return deathCause != null;
	}
	
	public boolean hasSpawner() {
		return spawner != null;
	}
	
	public boolean hasBaby() {
		return baby != null;
	}
	
	public boolean hasTamed() {
		return tamed != null;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return String.format("%s|%s|%s|%s|%s", 
							hasType() ? type : "",
							hasDeathCause() ? deathCause : "",
						    Parsing.formatBoolean("spawner", spawner),
							Parsing.formatBoolean("baby", baby),
							Parsing.formatBoolean("tamed", tamed));
	}
	
	@Override
	public Types getQueryType() {
		return Types.Mobs;
	}
}
