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

package com.comphenix.xp.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.comphenix.xp.extra.Permissions;

public class ExperienceInformerListener implements Listener {

	private List<String> warningMessages = new ArrayList<String>();

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		
		Player player = event.getPlayer();
		
		// Automatically display warning messages
		if (player != null) {
			displayWarnings(player, false);
		}
	}
	
	public boolean displayWarnings(CommandSender sender, boolean ignorePermission) {
		
		// Player or console
		if (ignorePermission || Permissions.hasInfo(sender)) {
			// Print warning messages
			for (String message : warningMessages) {
				sender.sendMessage(ChatColor.RED + "[ExperienceMod] Warning: " + message);
			}
			
			return true;
		}
		
		// No permission
		return false;
	}
	
	public void addWarningMessage(String message) {
		warningMessages.add(message);
	}
	
	public void clearMessages() {
		warningMessages.clear();
	}
	
	public boolean hasWarnings() {
		return !warningMessages.isEmpty();
	}
}
