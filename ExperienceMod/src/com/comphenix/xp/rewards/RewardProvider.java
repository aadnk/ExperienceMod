package com.comphenix.xp.rewards;

import java.util.HashMap;
import org.apache.commons.lang.NullArgumentException;
import com.comphenix.xp.rewards.RewardTypes;

/**
 * API for adding or removing reward managers.
 * 
 * @author Kristian
 */
public class RewardProvider {
	
	private static String customUnsupported = "rewardType cannot be custom. Use getByString().";
	
	private HashMap<String, Rewardable> nameLookup;
	private HashMap<RewardTypes, Rewardable> enumLookup;

	private String defaultReward;
	
	public RewardProvider() {
		// Default constructor
		this.nameLookup = new HashMap<String, Rewardable>();
		this.enumLookup = new HashMap<RewardTypes, Rewardable>();
	}
	
	public RewardProvider(RewardProvider reference) {
		this.nameLookup = reference.nameLookup;
		this.enumLookup = reference.enumLookup;
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
		
		return enumLookup.get(rewardType);
	}
	
	/**
	 * Returns the currently registered reward manager with this name.
	 * @param rewardName name to search for.
	 * @return The currently registered reward manager, or NULL if not found.
	 */
	public Rewardable getByName(String rewardName) {
		if (rewardName == null)
			throw new NullArgumentException("rewardName");
		
		return nameLookup.get(rewardName);
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
		
		RewardTypes type = reward.getType();
		String name = reward.getRewardName();
		
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
		
		return nameLookup.put(reward.getRewardName(), reward);
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
			nameLookup.remove(removed.getRewardName());
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
		
		if (!removed.getType().isSpecialMarker())
			enumLookup.remove(removed.getType());
		return removed;
	}
	
	/**
	 * Creates a copy of this reward provider with shallow references to the same list of rewards, except with a different
	 * internal default reward type. This allows multiple copies of the provider to reference the same rewards, but use
	 * a different default reward manager.
	 * @return A shallow copy of this reward manager.
	 */
	public RewardProvider createView() {
		return new RewardProvider(this);
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
}
