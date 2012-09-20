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

import java.util.AbstractQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.comphenix.xp.Debugger;
import com.comphenix.xp.extra.Permissions;

public class ExperienceInformerListener implements Listener {

	private AbstractQueue<String> warningMessages = new ConcurrentLinkedQueue<String>();
	private Server server;
	private Debugger debugger;
	
	private ErrorReporting report = ErrorReporting.DEFAULT;
	
	public ExperienceInformerListener(Debugger debugger, Server server) {
		this.debugger = debugger;
		this.server = server;
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		
		try {
		
			Player player = event.getPlayer();
			
			// Automatically display warning messages
			if (debugger != null) {
				displayWarnings(player, false);
			}
		
		} catch (Exception e) {
			report.reportError(debugger, this, e, event);
		}
	}
	
	public boolean displayWarnings(CommandSender sender, boolean ignorePermission) {
		
		// Player or console
		if (ignorePermission || Permissions.hasInfo(sender)) {
			// Print warning messages
			for (String message : warningMessages) {
				sender.sendMessage(formatMessage(message));
			}
			
			return true;
		}
		
		// No permission
		return false;
	}
	
	/**
	 * Broadcast the given warning on the server.
	 * @param message - warning to broadcast.
	 */
	public void broadcastWarning(String message) {
		
		// Send the warning to every player (console will be informed differently)
		for (Player player : server.getOnlinePlayers()) {
			if (player.hasPermission(Permissions.INFO)) {
				player.sendMessage(formatMessage(message));
			}
		}
	}

	/**
	 * Formats the given message for display.
	 * @param message - warning message to display.
	 * @return Formatted message.
	 */
	protected String formatMessage(String message) {
		return ChatColor.RED + "[ExperienceMod] Warning: " + message;
	}
	
	public void addWarningMessage(String message) {
		warningMessages.add(message);
	}
	
	public void clearMessages() {
		warningMessages.clear();
	}
	
	/**
	 * The number of warnings currently stored.
	 * @return Number of warnings.
	 */
	public int messageCount() {
		return warningMessages.size();
	}
	
	public boolean hasWarnings() {
		return !warningMessages.isEmpty();
	}
}
