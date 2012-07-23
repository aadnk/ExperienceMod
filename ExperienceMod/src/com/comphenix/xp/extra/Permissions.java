package com.comphenix.xp.extra;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.comphenix.xp.GlobalSettings;

/**
 * Contains every permission and permission test.
 * 
 * @author Kristian
 */
public class Permissions {
	public final static String KEEP_EXP = "experiencemod.keepexp";
	public final static String MAX_ENCHANT = "experiencemod.maxenchant";
	public final static String REWARDS_BONUS = "experiencemod.rewards.bonus";
	public final static String REWARDS_BLOCK = "experiencemod.rewards.block";
	public final static String REWARDS_PLACING = "experiencemod.rewards.placing";
	public final static String REWARDS_SMELTING = "experiencemod.rewards.smelting";
	public final static String REWARDS_BREWING = "experiencemod.rewards.brewing";
	public final static String REWARDS_CRAFTING = "experiencemod.rewards.crafting";
	public final static String REWARDS_FISHING = "experiencemod.rewards.fishing";
	
	public final static String UNOUCHABLE = "experiencemod.untouchable";
	public final static String ADMIN = "experiencemod.admin";
	
	// Whether or not to display warning messages
	public final static String INFO = "experiencemod.info";
	
	private static GlobalSettings globakSettings;
	
	public static boolean hasRewardSmelting(Player player) {
		return !globakSettings.isUsePermissions() || player.hasPermission(REWARDS_SMELTING);
	}
	
	public static boolean hasRewardBrewing(Player player) {
		return !globakSettings.isUsePermissions() || player.hasPermission(REWARDS_BREWING);
	}
	
	public static boolean hasRewardCrafting(Player player) {
		return !globakSettings.isUsePermissions() || player.hasPermission(REWARDS_CRAFTING);
	}
	
	public static boolean hasRewardFishing(Player player) {
		return !globakSettings.isUsePermissions() || player.hasPermission(REWARDS_FISHING);
	}
	
	public static boolean hasRewardBonus(Player player) {
		return !globakSettings.isUsePermissions() || player.hasPermission(REWARDS_BONUS);
	}
	
	public static boolean hasRewardBlock(Player player) {
		return !globakSettings.isUsePermissions() || player.hasPermission(REWARDS_BLOCK);
	}
	
	public static boolean hasRewardPlacing(Player player) {
		return !globakSettings.isUsePermissions() || player.hasPermission(REWARDS_PLACING);
	}
	
	public static boolean hasUntouchable(Player player) {
		return !globakSettings.isUsePermissions() || player.hasPermission(UNOUCHABLE);
	}
	
	public static boolean hasKeepExp(Player player) {
		return !globakSettings.isUsePermissions() || player.hasPermission(KEEP_EXP);
	}
	
	public static boolean hasMaxEnchant(Player player) {
		return !globakSettings.isUsePermissions() || player.hasPermission(MAX_ENCHANT);
	}
	
	public static boolean hasAdmin(CommandSender sender) {
		// Rely on the "ops.txt" to set this permission
		return sender.hasPermission(ADMIN);
	}
	
	public static boolean hasInfo(CommandSender sender) {
		return sender.hasPermission(INFO);
	}
	
	public static GlobalSettings getGlobakSettings() {
		return globakSettings;
	}

	public static void setGlobakSettings(GlobalSettings globakSettings) {
		Permissions.globakSettings = globakSettings;
	}
}
