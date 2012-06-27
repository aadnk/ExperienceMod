package com.comphenix.xp.commands;

import org.bukkit.command.CommandSender;

public class CommandUtilities {
	
	/**
	 * Determines if the given command sender has a permission.
	 * @param sender - sender to test.
	 * @param permission - permission to find.
	 * @return TRUE if the sender exists and has the given permission, FALSE otherwise.
	 */
	public static boolean hasCommandPermission(CommandSender sender, String permission) {
		
		// Make sure the sender has permissions
		if (sender != null && !sender.hasPermission(permission)) {
			return false;
		} else {
			// We have permission
			return true;
		}
	}
	
	/**
	 * Retrieves an array element.
	 * @param args - array to retrieve from.
	 * @param index - index of the element to retrieve.
	 * @return Returns the element if it exists, OR an empty ("") string.
	 */
	public static String getSafe(String[] args, int index) {
		return (index >= 0 && args.length > index) ? args[index] : "";
	}
}
