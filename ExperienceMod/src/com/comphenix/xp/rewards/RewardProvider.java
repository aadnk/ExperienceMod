package com.comphenix.xp.rewards;

import java.util.HashMap;
import org.apache.commons.lang.NullArgumentException;

import com.comphenix.xp.Configuration;
import com.comphenix.xp.extra.ServiceProvider;
import com.comphenix.xp.rewards.RewardTypes;

/**
 * API for adding or removing reward managers.
 * 
 * @author Kristian
 */
public class RewardProvider extends ServiceProvider<RewardService> {
	
	public static String defaultRewardName = "DEFAULT";
	
	// Error messages
	private static String customUnsupported = "rewardType cannot be CUSTOM.";
	
	// Enum type lookup
	private HashMap<RewardTypes, RewardService> enumLookup;

	private String defaultReward;
	private Configuration configuration;
	
	public RewardProvider() {
		// Default constructor
		super();
		this.nameLookup = new HashMap<String, RewardService>();
		this.enumLookup = new HashMap<RewardTypes, RewardService>();
	}
	
	public RewardProvider(RewardProvider reference, Configuration configuration) {
		super();
		this.nameLookup = reference.nameLookup;
		this.enumLookup = reference.enumLookup;
		this.configuration = configuration;
	}
	
	/**
	 * Returns the currently registered reward service for this type.
	 * @param rewardType - type to search for.
	 * @return The currently registered reward service, or NULL if not found.
	 */
	public RewardService getByEnum(RewardTypes rewardType) {
		if (rewardType == null)
			throw new NullArgumentException("rewardType");
		if (rewardType == RewardTypes.CUSTOM)
			throw new IllegalArgumentException(customUnsupported);
		if (rewardType == RewardTypes.DEFAULT)
			return getByName(defaultRewardName);
		
		// Add configuration here too
		return getConfigSpecific(enumLookup.get(rewardType));
	}
	
	@Override
	public RewardService getByName(String rewardName) {
		RewardService service;
		
		if (rewardName.equalsIgnoreCase(defaultRewardName))
			service = super.getByName(getDefaultReward());
		else
			service = super.getByName(rewardName);
		
		// Add configuration
		return getConfigSpecific(service);
	}
	
	@Override
	public RewardService register(RewardService reward, boolean override) {
		if (reward == null)
			throw new NullArgumentException("reward");
		
		RewardTypes type = reward.getRewardType();

		// Add to lookup
		if (type != RewardTypes.CUSTOM) {
			enumLookup.put(type, reward);
		} else if (type == RewardTypes.DEFAULT) {
			throw new IllegalArgumentException("Reward type cannot be default.");
		}
		
		// Register with name
		return super.register(reward, override);
	}
	
	/**
	 * Unregisters a specified reward manager.
	 * @param rewardType the type of the reward manager to unregister.
	 * @return The previously registered manager with this type, or NULL otherwise.
	 */
	public RewardService unregister(RewardTypes rewardType) {
		if (rewardType == null)
			throw new NullArgumentException("rewardType");
		if (rewardType == RewardTypes.CUSTOM)
			throw new IllegalArgumentException(customUnsupported);
		if (rewardType == RewardTypes.DEFAULT)
			return unregister(getDefaultReward());
		
		RewardService removed = enumLookup.remove(rewardType);
		
		// Make sure to remove it from the name list too
		if (removed != null)
			super.unregister(removed);
		return removed;
	}
	
	@Override
	public RewardService unregister(String rewardName) {

		RewardService removed = super.unregister(rewardName);
		
		if (!removed.getRewardType().isSpecialMarker())
			enumLookup.remove(removed.getRewardType());
		return removed;
	}
	
	/**
	 * Determines whether or not the given reward type has been registered.
	 * @param type - type of the reward to check.
	 * @return TRUE if it has, FALSE otherwise.
	 */
	public boolean containsReward(RewardTypes type) {
		if (type == RewardTypes.DEFAULT)
			return containsService(getDefaultReward());
		else if (type == RewardTypes.CUSTOM)
			throw new IllegalArgumentException(customUnsupported);
		
		return enumLookup.containsKey(type);
	}
	
	// Make sure the reward manager has the correct configuration associated with it
	private RewardService getConfigSpecific(RewardService reward) {
		
		if (reward == null)
			return null;
		else if (configuration != null)
			return reward.clone(configuration);
		else
			return reward;
	}
	
	/**
	 * Creates a copy of this reward provider with shallow references to the same list of rewards, except with a different
	 * internal default reward type. This allows multiple copies of the provider to reference the same rewards, but use
	 * a different default reward manager.
	 * @param config Configuration settings for the different managers.
	 * @return A shallow copy of this reward manager.
	 */
	public RewardProvider createView(Configuration config) {
		return new RewardProvider(this, config);
	}
	
	/**
	 * Retrieves the default reward manager name.
	 * @return Default manager name.
	 */
	public String getDefaultReward() {
		return defaultReward;
	}

	/**
	 * Sets the default reward manager name.
	 * @param defaultReward default manager name.
	 */
	public void setDefaultReward(String defaultReward) {
		this.defaultReward = defaultReward;
	}
	
	/**
	 * Sets the default reward manager by type.
	 * @param defaultReward default manager type.
	 */
	public void setDefaultReward(RewardTypes defaultType) {
		if (defaultType == RewardTypes.DEFAULT)
			throw new IllegalArgumentException("Cannot set the default with the default.");
		else if (defaultType == RewardTypes.CUSTOM)
			throw new IllegalArgumentException(customUnsupported);
		
		setDefaultReward(defaultType.name());
	}
	
	/**
	 * Retrieves the configuration containing settings for different reward managers.
	 * @return The configuration file.
	 */
	public Configuration getConfiguration() {
		return configuration;
	}

	/**
	 * Sets the configuration containing settings for different reward managers.
	 * @param configuration New configuration.
	 */
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
}
