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

package com.comphenix.xp;

public interface Debugger {
	
	/**
	 * Whether or not a debug mode is enabled.
	 * @return TRUE if debug mode is enabled, FALSE otherwise.
	 */
	public boolean isDebugEnabled();
	
	/**
	 * Prints or logs a debug message.
	 * @param sender - the object that sent this message.
	 * @param message - the format of the debug message to send.
	 * @param params - the parameters to include in the debug message.
	 */
	public void printDebug(Object sender, String message, Object... params);
	
	/**
	 * Prints or logs a warning.
	 * @param sender - the object that sent this message.
	 * @param message - the format of the warning message to send.
	 * @param params - the parameters to include in the warning message.
	 */
	public void printWarning(Object sender, String message, Object... params);
}
