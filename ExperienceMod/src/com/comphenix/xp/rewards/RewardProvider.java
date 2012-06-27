package com.comphenix.xp.rewards;

import java.util.HashMap;
import org.apache.commons.lang.NullArgumentException;

import com.comphenix.xp.Configuration;
import com.comphenix.xp.rewards.RewardTypes;

/**
 * API for adding or removing reward managers.
 * 
 * @author Kristian
 */
public class RewardProvider {
	
	private static String customUnsupported = "rewardType cannot be custom. Use getByString().";
	private static String defaultRewardName = "DEFAULT";
	
	private HashMap<String, Rewardable> nameLookup;
	private HashMap<RewardTypes, Rewardable> enumLookup;

	private String defaultReward;
	private Configuration configuration;
	
	public RewardProvider() {
		// Default constructor
		this.nameLookup = new HashMap<String, Rewardable>();
		this.enumLookup = new HashMap<RewardTypes, Rewardable>();
	}
	
	public RewardProvider(RewardProvider reference, Configuration configuration) {
		this.nameLookup = reference.nameLookup;
		this.enumLookup = reference.enumLookup;
		this.configuration = configuration;
	}
	
	/**
	 * Returns the currently registered reward manager for this type.
	 * @param rewardType type to search for.
	 * @return The currently registered reward manager, or NULL if not found.
	 */
	public Rewardable getByEnum(RewardTypes rewardType) {
		if (rewardType == null)
			throw new NullArgumentException("rewardType");
		if (rewardType == RewardTypes.CUSTOM)
			throw new IllegalArgumentException(customUnsupported);
		if (rewardType == RewardTypes.DEFAULT)
			return getByName(getDefaultReward());
		
		return getConfigSpecific(enumLookup.get(rewardType));
	}
	
	/**
	 * Returns the currently registered reward manager with this name. The name 
	 * should be conforming to the Java Enum convention.
	 * @param rewardName name to search for.
	 * @return The currently registered reward manager, or NULL if not found.
	 */
	public Rewardable getByName(String rewardName) {
		if (rewardName == null)
			throw new NullArgumentException("rewardName");
		if (rewardName.equalsIgnoreCase(defaultRewardName))
			return nameLookup.get(getDefaultReward());
		
		return getConfigSpecific(nameLookup.get(rewardName));
	}
	
	/**
	 * Registers a reward manager in the system.
	 * @param reward the reward manager to register.
	 * @param override TRUE to override any previously registered managers with the same name or type. 
	 * @return The previously registered manager with this type and name, or NULL otherwise.
	 */
	public Rewardable register(Rewardable reward, boolean override) {
		if (reward == null)
			throw new NullArgumentException("reward");
		
		RewardTypes type = reward.getRewardType();
		String name = reward.getServiceName();
		
		if (type.isSpecialMarker()) {
			throw new IllegalArgumentException("reward cannot be of type custom or default");
		}
		
		// Note that the first check will always be FALSE if the type is a special marker
		if (override) {
			if (enumLookup.containsKey(type) || nameLookup.containsKey(name)) {
					return getByName(name);
			}
		}
		
		// Add to special lookup
		if (!type.isSpecialMarker()) {
			enumLookup.put(type, reward);
		}
		
		return nameLookup.put(reward.getServiceName(), reward);
	}
	
	/**
	 * Unregisters a specified reward manager.
	 * @param rewardType the type of the reward manager to unregister.
	 * @return The previously registered manager with this type, or NULL otherwise.
	 */
	public Rewardable unregister(RewardTypes rewardType) {
		if (rewardType == null)
			throw new NullArgumentException("rewardType");
		if (rewardType == RewardTypes.CUSTOM)
			throw new IllegalArgumentException(customUnsupported);
		if (rewardType == RewardTypes.DEFAULT)
			return unregister(getDefaultReward());
		
		Rewardable removed = enumLookup.remove(rewardType);
		
		// Make sure to remove it from the name list too
		if (removed != null)
			nameLookup.remove(removed.getServiceName());
		return removed;
	}
	
	/**
	 * Unregisters a specified reward manager.
	 * @param rewardType the name of the reward manager to unregister.
	 * @return The previously registered manager with this name, or NULL otherwise.
	 */
	public Rewardable unregister(String rewardName) {
		if (rewardName == null)
			throw new NullArgumentException("rewardName");

		Rewardable removed = nameLookup.remove(rewardName);
		
		if (!removed.getRewardType().isSpecialMarker())
			enumLookup.remove(removed.getRewardType());
		return removed;
	}
	
	/**
	 * Determines whether or not the given reward has been registered.
	 * @param name Name of the reward to check.
	 * @return TRUE if it has, FALSE otherwise.
	 */
	public boolean containsReward(String name) {
		return nameLookup.containsKey(name);
	}
	
	/**
	 * Determines whether or not the given reward type has been registered.
	 * @param type Type of the reward to check.
	 * @return TRUE if it has, FALSE otherwise.
	 */
	public boolean containsReward(RewardTypes type) {
		if (type == RewardTypes.DEFAULT)
			return containsReward(defaultReward);
		else if (type == RewardTypes.CUSTOM)
			throw new IllegalArgumentException(customUnsupported);
		
		return enumLookup.containsKey(type);
	}
	
	// Make sure the reward manager has the correct configuration associated with it
	private Rewardable getConfigSpecific(Rewardable reward) {
		
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
