package com.comphenix.xp.parser.sections;

import java.util.Map;

import com.comphenix.xp.lookup.ItemTree;
import com.comphenix.xp.lookup.PotionTree;

public class ItemsSectionResult {
	// Every standard reward
	private Map<Integer, ItemTree> actionRewards;
	private Map<Integer, PotionTree> complexRewards;
	
	public ItemsSectionResult(Map<Integer, ItemTree> actionRewards, 
							  Map<Integer, PotionTree> complexRewards) {
		this.actionRewards = actionRewards;
		this.complexRewards = complexRewards;
	}
	
	public Map<Integer, ItemTree> getActionRewards() {
		return actionRewards;
	}
	
	public Map<Integer, PotionTree> getComplexRewards() {
		return complexRewards;
	}
	
	/**
	 * Retrieves the rewards for the given action or trigger.
	 * @param actionID - unique ID for the given action.
	 * @return Tree of every associated reward.
	 */
	public ItemTree getActionReward(Integer actionID) {
		return actionRewards.get(actionID);
	}
	
	/**
	 * Retrieves the complex potion rewards for the given action or trigger.
	 * @param actionID - unique ID for the given action.
	 * @return Tree of every associated reward.
	 */
	public PotionTree getComplexReward(Integer actionID) {
		return complexRewards.get(actionID);
	}
}
