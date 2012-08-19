package com.comphenix.xp.expressions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.entity.Player;

import com.comphenix.xp.rewards.xp.ExperienceManager;

public class PlayerParameter extends NamedParameter {

	/**
	 * Different player attributes to use.
	 * 
	 * @author Kristian
	 */
	public enum PlayerAttributes {
		TOTAL_EXPERIENCE,
		LEVEL_EXPERIENCE,
		EXPERIENCE,
		CURRENCY
	}
	
	protected PlayerAttributes attribute;
	
	protected Player player;
	protected Economy economy;
	
	public PlayerParameter(PlayerAttributes attribute, Player player, Economy economy) {
		super(attribute.name());
		this.attribute = attribute;
		this.player = player;
		this.economy = economy;
	}

	/**
	 * Retrieves every player attribute from the given player and economy API.
	 * @param player - player to use.
	 * @param economy - economy API that will retrieve the player's currency.
	 * @return Every relevant player parameter.
	 */
	public static Collection<NamedParameter> getAllParameters(Player player, Economy economy) {
		List<NamedParameter> parameters = new ArrayList<NamedParameter>();
		
		// Create parameters of every type
		for (PlayerAttributes attribute : PlayerAttributes.values()) {
			parameters.add(new PlayerParameter(attribute, player, economy));
		}
		
		return parameters;
	}
	
	@Override
	public Double call() throws Exception {

		ExperienceManager manager = new ExperienceManager(player);
		
		// Use a good ol' switch to execute the different functions
		switch (attribute) {
		case EXPERIENCE:
		case TOTAL_EXPERIENCE:
			return (double) manager.getCurrentExp();
		case LEVEL_EXPERIENCE:
			return (double) (player.getExp() * manager.getXpNeededToLevelUp(player.getLevel()) );
		case CURRENCY:
			if (economy != null) 
				return economy.getBalance(player.getName());
			else
				return 0.0; // Default
		default:
			throw new IllegalArgumentException("Unknown player parameter attribute detected.");
		}
	}

	public PlayerAttributes getAttribute() {
		return attribute;
	}

	public Player getPlayer() {
		return player;
	}
}
