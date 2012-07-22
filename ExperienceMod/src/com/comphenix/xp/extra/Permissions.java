package com.comphenix.xp.extra;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Contains every permission and permission test.
 * 
 * @author Kristian
 */
public class Permissions {
	public final static String permissionKeepExp = "experiencemod.keepexp";
	public final static String permissionMaxEnchant = "experiencemod.maxenchant";
	public final static String permissionRewardBonus = "experiencemod.rewards.bonus";
	public final static String permissionRewardBlock = "experiencemod.rewards.block";
	public final static String permissionRewardPlacing = "experiencemod.rewards.placing";
	public final static String permissionRewardSmelting = "experiencemod.rewards.smelting";
	public final static String permissionRewardBrewing = "experiencemod.rewards.brewing";
	public final static String permissionRewardCrafting = "experiencemod.rewards.crafting";
	public final static String permissionRewardFishing = "experiencemod.rewards.fishing";
	public final static String permissionUntouchable = "experiencemod.untouchable";

	// Whether or not to display warning messages
	public final static String permissionInfo = "experiencemod.info";
	
	public static boolean hasRewardSmelting(Player player) {
		return player.hasPermission(permissionRewardSmelting);
	}
	
	public static boolean hasRewardBrewing(Player player) {
		return player.hasPermission(permissionRewardBrewing);
	}
	
	public static boolean hasRewardCrafting(Player player) {
		return player.hasPermission(permissionRewardCrafting);
	}
	
	public static boolean hasRewardFishing(Player player) {
		return player.hasPermission(permissionRewardFishing);
	}
	
	public static boolean hasRewardBonus(Player player) {
		return player.hasPermission(permissionRewardBonus);
	}
	
	public static boolean hasRewardBlock(Player player) {
		return player.hasPermission(permissionRewardBlock);
	}
	
	public static boolean hasRewardPlacing(Player player) {
		return player.hasPermission(permissionRewardPlacing);
	}
	
	public static boolean hasUntouchable(Player player) {
		return player.hasPermission(permissionUntouchable);
	}
	
	public static boolean hasKeepExp(Player player) {
		return player.hasPermission(permissionKeepExp);
	}
	
	public static boolean hasMaxEnchant(Player player) {
		return player.hasPermission(permissionMaxEnchant);
	}
	
	public static boolean hasInfo(CommandSender sender) {
		return sender.hasPermission(permissionInfo);
	}
}
