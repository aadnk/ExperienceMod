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

public class ExperienceInformer implements Listener {

	// Whether or not to display warning messages
	private final String permissionInfo = "experiencemod.info";

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
		if (ignorePermission || sender.hasPermission(permissionInfo)) {
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