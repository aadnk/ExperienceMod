package com.comphenix.xp.extra;

import org.bukkit.permissions.Permissible;

/**
 * Contains every permission and permission test.
 * 
 * @author Kristian
 */
public class PermissionSystem {
	/**
	 * Represents every permission recognized by ExperienceMod.
	 * @author Kristian
	 */
	public enum CustomPermission {
		KEEP_EXP("experiencemod.keepexp", PermissionType.OPTION),
		MAX_ENCHANT("experiencemod.maxenchant", PermissionType.REWARD),
		REWARDS_BONUS("experiencemod.rewards.bonus", PermissionType.REWARD),
		REWARDS_BLOCK("experiencemod.rewards.block", PermissionType.REWARD),
		REWARDS_PLACING("experiencemod.rewards.placing", PermissionType.REWARD),
		REWARDS_SMELTING("experiencemod.rewards.smelting", PermissionType.REWARD),
		REWARDS_BREWING("experiencemod.rewards.brewing", PermissionType.REWARD),
		REWARDS_CRAFTING("experiencemod.rewards.crafting", PermissionType.REWARD),
		REWARDS_FISHING("experiencemod.rewards.fishing", PermissionType.REWARD),
		UNOUCHABLE("experiencemod.untouchable", PermissionType.ADMIN),
		ADMIN("experiencemod.admin", PermissionType.ADMIN),
		INFO("experiencemod.info", PermissionType.ADMIN);
		
		private final String bukkitPerm;
		private final PermissionType type;
		
		private CustomPermission(String bukkit, PermissionType type) {
			this.bukkitPerm = bukkit;
			this.type = type;
		}
		
		/**
		 * Determine if the given permissable object has this permission.
		 * @param target - the permissable object.
		 * @return TRUE if it does, FALSE otherwise.
		 */
		public boolean check(Permissible target) {
			return type.check(bukkitPerm, target);
		}
		
		/**
		 * Retrieve the Bukkit permission string.
		 * @return The bukkit permission.
		 */
		public String getBukkitPerm() {
			return bukkitPerm;
		}
	}
	
	/**
	 * Represents a type of a permission.
	 * <p>
	 * This alters how it is affected by turning the permission system on and off.
	 * @author Kristian
	 */
	private abstract static class PermissionType {
		/**
		 * Represents a permission for being granted a reward. These are enabled when the permission system is turned off.
		 */
		public static final PermissionType REWARD = new PermissionType() {
			@Override
			public boolean check(String bukkitPerm, Permissible target) {
				return !ENABLED || target.hasPermission(bukkitPerm);
			}
		};
		
		/**
		 * Represents a permission that alters game rules. This can only be used when the permission system is on.
		 */
		public static final PermissionType OPTION = new PermissionType() {
			@Override
			public boolean check(String bukkitPerm, Permissible target) {
				return ENABLED && target.hasPermission(bukkitPerm);
			}
		};
		
		/**
		 * Represents a permission for administrating the plugin. These cannot be turned off, but should always be enabled
		 * for operators/console users.
		 */
		public static final PermissionType ADMIN = new PermissionType() {
			@Override
			public boolean check(String bukkitPerm, Permissible target) {
				return target.hasPermission(bukkitPerm);
			}
		};
		
		/**
		 * Determine if the given target has the given permission.
		 * @param bukkitPerm - the Bukkit permission.
		 * @param target - the target.
		 * @return TRUE if it does, FALSE otherwise.
		 */
		public abstract boolean check(String bukkitPerm, Permissible target);
	}
	
	/**
	 * Whether or not the permisison system is enabled or not.
	 */
	private static boolean ENABLED = true;
	
	/**
	 * Determine if the permission system is enabled.
	 * @return TRUE if it is, FALSE otherwise.
	 */
	public static boolean isEnabled() {
		return PermissionSystem.ENABLED;
	}
	
	/**
	 * Set whether or not the permission system is enabled.
	 * @param enabled - TRUE if it is, FALSE otherwise.
	 */
	public static void setEnabled(boolean enabled) {
		PermissionSystem.ENABLED = enabled;
	}
	
	public static boolean hasRewardSmelting(Permissible target) {
		return CustomPermission.REWARDS_SMELTING.check(target);
	}
	
	public static boolean hasRewardBrewing(Permissible target) {
		return CustomPermission.REWARDS_BREWING.check(target);
	}
	
	public static boolean hasRewardCrafting(Permissible target) {
		return CustomPermission.REWARDS_CRAFTING.check(target);
	}
	
	public static boolean hasRewardFishing(Permissible target) {
		return CustomPermission.REWARDS_FISHING.check(target);
	}
	
	public static boolean hasRewardBonus(Permissible target) {
		return CustomPermission.REWARDS_BONUS.check(target);
	}
	
	public static boolean hasRewardBlock(Permissible target) {
		return CustomPermission.REWARDS_BLOCK.check(target);
	}
	
	public static boolean hasRewardPlacing(Permissible target) {
		return CustomPermission.REWARDS_PLACING.check(target);
	}
		
	public static boolean hasMaxEnchant(Permissible target) {
		return CustomPermission.MAX_ENCHANT.check(target);
	}
	
	public static boolean hasKeepExp(Permissible target) {
		// This feature is only present for permission users
		return CustomPermission.KEEP_EXP.check(target);
	}
	
	public static boolean hasUntouchable(Permissible target) {
		return CustomPermission.UNOUCHABLE.check(target);
	}
	
	public static boolean hasAdmin(Permissible target) {
		// Rely on the "ops.txt" to set this permission
		return CustomPermission.ADMIN.check(target);
	}
	
	public static boolean hasInfo(Permissible target) {
		return CustomPermission.INFO.check(target);
	}
}
