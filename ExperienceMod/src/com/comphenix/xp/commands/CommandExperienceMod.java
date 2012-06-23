package com.comphenix.xp.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.comphenix.xp.ExperienceMod;

public class CommandExperienceMod implements CommandExecutor {

	private final String permissionAdmin = "experiencemod.admin";

	// Mod command(s)
	private final String commandReload = "experiencemod";
	private final String subCommandToggleDebug = "debug";
	private final String subCommandWarnings = "warnings";
	private final String subCommandReload = "reload";
	private final String subCommandQuery = "query";
	private final String subSubCommandItem = "item";
	private final String subSubCommandMob = "mob";
	
	private ExperienceMod plugin;

	public CommandExperienceMod(ExperienceMod plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		// Execute the correct command
		if (command != null && command.getName().equalsIgnoreCase(commandReload))
			return handleMainCommand(sender, args);
		else
			return false;
	}
	
	private boolean handleMainCommand(CommandSender sender, String[] args) {
		
		// Make sure the sender has permissions
		if (!plugin.hasCommandPermission(sender, permissionAdmin)) {
			plugin.respond(sender, ChatColor.RED + "You haven't got permission to execute this command.");
			return true;
		} 
		
		String sub = args.length > 0 ? args[0] : "";
		
		// Toggle debugging
		if (sub.equalsIgnoreCase(subCommandToggleDebug)) {
			
			plugin.toggleDebug();
			plugin.respond(sender, ChatColor.BLUE + "Debug " + (plugin.isDebugEnabled() ? " enabled " : " disabled"));
			return true;
			
		// Display the parse warnings during the last configuration load
		} else if (sub.equalsIgnoreCase(subCommandWarnings)) {
			
			if (sender != null && plugin.getInformer().hasWarnings())
				plugin.getInformer().displayWarnings(sender, true);
			else
				sender.sendMessage(ChatColor.GREEN + "No warnings found.");
			return true;
			
		} else if (sub.equalsIgnoreCase(subCommandQuery)) {
			
			String type = args.length > 0 ? args[1] : "";
			
			if (type.equalsIgnoreCase(subSubCommandItem)) {
				// ToDo: Make command
				return true;
			} else if (type.equalsIgnoreCase(subSubCommandMob)) {
				
				return true;
			} else {
				plugin.respond(sender, ChatColor.RED + "Unknown query type. Must be mob or item.");
				return false;
			}
			
		} else if (sub.equalsIgnoreCase(subCommandReload) || sub.length() == 0) {
			
			plugin.loadDefaults(true);
			plugin.respond(sender, ChatColor.BLUE + "Reloaded ExperienceMod.");
    		return true;

		} else {
		
			plugin.respond(sender, ChatColor.RED + "Error: Unknown subcommand.");
			return false; 
		}
	}
}
