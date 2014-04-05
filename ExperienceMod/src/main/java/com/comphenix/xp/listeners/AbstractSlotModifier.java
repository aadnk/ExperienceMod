package com.comphenix.xp.listeners;

import org.bukkit.entity.Player;

import com.comphenix.xp.Debugger;
import com.comphenix.xp.Presets;

/**
 * Represesnts an enchanting table slot modifier.
 * @author Kristian
 */
public abstract class AbstractSlotModifier extends AbstractExperienceListener {
	/**
	 * The current debugger.
	 */
	protected Debugger debugger;

	public AbstractSlotModifier(Debugger debugger, Presets presets) {
		this.presets = presets;
		this.debugger = debugger;
	}

	/**
	 * Invoked after we have prepared the enchanting table of a player.
	 * @param player - the player.
	 */
	public abstract void onPreparedEnchanting(Player player);

	/**
	 * Invoked when we are ready to modify the cost list.
	 * @param player - the current player.
	 * @param output - the cost list that will be the result of the prepare enchanting event.
	 * @param modified - the desired cost list.
	 */
	public abstract void modifyCostList(Player player, int[] output, int[] modified);
}