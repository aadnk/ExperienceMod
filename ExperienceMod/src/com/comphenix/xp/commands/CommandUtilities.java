package com.comphenix.xp.commands;

import org.bukkit.command.CommandSender;

public class CommandUtilities {
	
	public static boolean hasCommandPermission(CommandSender sender, String permission) {
		
		// Make sure the sender has permissions
		if (sender != null && !sender.hasPermission(permission)) {
			return false;
		} else {
			// We have permission
			return true;
		}
	}
	
	public static String getSafe(String[] args, int index) {
		return args.length > index ? args[index] : "";
	}
}
