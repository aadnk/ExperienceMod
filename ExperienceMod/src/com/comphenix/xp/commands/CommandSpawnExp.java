package com.comphenix.xp.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.comphenix.xp.ExperienceMod;
import com.comphenix.xp.Server;
import com.comphenix.xp.parser.Utility;

public class CommandSpawnExp implements CommandExecutor {

	private final String permissionAdmin = "experiencemod.admin";
	private final String commandSpawnExp = "spawnexp";

	// Constants
	private final int spawnExpMaxDistance = 50;
	
	private ExperienceMod plugin;

	public CommandSpawnExp(ExperienceMod plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		// Execute the correct command
		if (command != null && command.getName().equalsIgnoreCase(commandSpawnExp))
			return handleSpawnExp(sender, args);
		else
			return false;
	}
	
	private boolean handleSpawnExp(CommandSender sender, String[] args) {

		// We don't support console yet
		if (sender == null || !(sender instanceof Player)) {
			plugin.respond(sender, ChatColor.RED + "This command can only be sent by a player");
			return false;
		}
		
		// Make sure the sender has permissions
		if (!plugin.hasCommandPermission(sender, permissionAdmin)) {
			plugin.respond(sender, ChatColor.RED + "You haven't got permission to execute this command.");
			return true;
		}
		
		if (args.length == 1 && !Utility.isNullOrIgnoreable(args[0])) {
			
			Integer experience = Integer.parseInt(args[0]);
			Player player = (Player) sender;
			
			if (experience == null) {
				plugin.respond(sender, ChatColor.RED + "Error: Parameter must be a valid integer.");
				return false;
			}
			
			Block startBlock = player.getEyeLocation().getBlock();
			List<Block> list = player.getLastTwoTargetBlocks(null, spawnExpMaxDistance);
			
			// Remember the start location
			list.add(0, startBlock);
			
			// We want to spawn the experience at the surface of the block.
			list.remove(list.size() - 1);
			
			if (list.size() > 0) {
				Block target = list.get(list.size() - 1);
				Location loc = target.getLocation();
				
				// Spawn experience at this location
				plugin.printDebug(this, "Spawning %d experience at %b.", experience, loc);
				Server.spawnExperienceAtBlock(target, experience);
				return true;
			}
				

		} else {
			plugin.respond(sender, ChatColor.RED + "Error: Incorrect number of parameters.");
		}
		
		return false;
	}
}
