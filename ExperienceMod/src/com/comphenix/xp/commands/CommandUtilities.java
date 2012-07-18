/*
 *  ExperienceMod - Bukkit server plugin for modifying the experience system in Minecraft.
 *  Copyright (C) 2012 Kristian S. Stangeland
 *
 *  This program is free software; you can redistribute it and/or modify it under the terms of the 
 *  GNU General Public License as published by the Free Software Foundation; either version 2 of 
 *  the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with this program; 
 *  if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 
 *  02111-1307 USA
 */

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
