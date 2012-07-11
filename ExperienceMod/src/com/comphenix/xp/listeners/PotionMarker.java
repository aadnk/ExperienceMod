package com.comphenix.xp.listeners;

import org.bukkit.potion.Potion;

/**
 * Builds a potion with extra information.
 * 
 * @author Kristian
 */
public class PotionMarker {

	private static final int BIT_REWARDED = 0x1000;
	
	// The current stored damage value, representing the given potion
	private short damage;
	
	public PotionMarker(Potion potion) {
		this.damage = potion.toDamageValue();
	}
	
	public PotionMarker(short durability) {
		this.damage = durability;
	}
	
	/**
	 * Whether or not the potion has already rewarded a player with experience.
	 * @return TRUE if a reward has already been given, FALSE otherwise.
	 */
	public boolean hasBeenRewarded() {
		return hasBit(BIT_REWARDED);
	}
	
	/**
	 * Sets whether or not the potion has given a reward to the player.
	 * @param value TRUE if the reward has been given, FALSE otherwise.
	 */
	public void setBeenRewarded(boolean value) {
		setBit(BIT_REWARDED, value);
	}
	
	/**
	 * Resets all special potion markers.
	 */
	public void reset() {
		setBeenRewarded(false);
	}
	
	/**
	 * Constructs a potion from the original values together with the current markers.
	 * @return New potion with the correct values.
	 */
	public Potion toPotion() {
		return Potion.fromDamage(damage);
	}
	
	/**
	 * Returns the modified durability value.
	 * @return Modified durability value.
	 */
	public short toDurability() {
		return damage;
	}
	
	// Check for a bit
	private boolean hasBit(int bitMask) {
		return (damage & bitMask) != 0;
	}
	
	// Set a bit
	private void setBit(int bitMask, boolean value) {
		if (value) {
			damage |= bitMask;
		} else {
			damage &= ~bitMask;
		}
	}
}
