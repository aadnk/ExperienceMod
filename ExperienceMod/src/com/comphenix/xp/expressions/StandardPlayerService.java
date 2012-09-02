package com.comphenix.xp.expressions;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.entity.Player;

import com.comphenix.xp.Action;
import com.comphenix.xp.parser.Utility;
import com.comphenix.xp.rewards.RewardProvider;
import com.comphenix.xp.rewards.RewardTypes;
import com.comphenix.xp.rewards.xp.ExperienceManager;
import com.comphenix.xp.rewards.xp.RewardEconomy;

public class StandardPlayerService implements ParameterService<Player> {

	public static String NAME = "STANDARD_PLAYER_PARAMETERS";
	
	/**
	 * List of every standard player parameter.
	 * 
	 * @author Kristian
	 */
	public enum PlayerParameters {
		TOTAL_EXPERIENCE,
		LEVEL_EXPERIENCE,
		EXPERIENCE,
		CURRENCY
	}
	
	// The name of every player parameter
	private static String[] PARAM_NAMES = Utility.toStringArray(PlayerParameters.values());
	
	// Used to support the currency parameter
	protected RewardEconomy economy;
	
	public StandardPlayerService(RewardProvider provider) {
		// Load the economy provider
		economy = (RewardEconomy) provider.getByEnum(RewardTypes.ECONOMY);
	}
	
	@Override
	public String getServiceName() {
		return NAME;
	}

	@Override
	public String[] getParameterNames() {
		return PARAM_NAMES;
	}

	@Override
	public Collection<NamedParameter> getParameters(final Action action, final Player player) {

		final Collection<NamedParameter> standard = new ArrayList<NamedParameter>(PARAM_NAMES.length);
		final ExperienceManager manager = new ExperienceManager(player);
		
		// Create short-lived named parameters
		for (PlayerParameters parameter : PlayerParameters.values()) {
			final PlayerParameters current = parameter;
			
			standard.add(new NamedParameter(parameter.toString()) {
				@Override
				public Double call() throws Exception {
				
					// Use a good ol' switch to execute the different functions
					switch (current) {
					case EXPERIENCE:
					case TOTAL_EXPERIENCE:
						return (double) manager.getCurrentExp();
					case LEVEL_EXPERIENCE:
						return (double) (player.getExp() * manager.getXpNeededToLevelUp(player.getLevel()) );
					case CURRENCY:
						if (economy != null) 
							return economy.getBalance(player);
						else
							return 0.0; // Default
					default:
						throw new IllegalArgumentException("Unknown player parameter attribute detected.");
					}
				}
			});
		}
		
		return standard;
	}
}
