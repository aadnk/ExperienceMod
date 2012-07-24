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

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.comphenix.xp.Action;
import com.comphenix.xp.ExperienceMod;
import com.comphenix.xp.extra.Permissions;
import com.comphenix.xp.lookup.MobQuery;
import com.comphenix.xp.lookup.Query;
import com.comphenix.xp.parser.ParsingException;
import com.comphenix.xp.parser.text.ItemParser;
import com.comphenix.xp.parser.text.MobParser;

public class CommandExperienceMod implements CommandExecutor {

	// Mod command(s)
	public static final String COMMAND_RELOAD = "experiencemod";
	public static final String COMMAND_ABBREVIATED = "expmod";
	
	public static final String SUB_COMMAND_TOGGLE_DEBUG = "debug";
	public static final String SUB_COMMAND_WARNINGS = "warnings";
	public static final String SUB_COMMAND_RELOAD = "reload";
	public static final String SUB_COMMAND_ITEM = "item";
	public static final String SUB_COMMAND_MOB = "mob";
	
	private ExperienceMod plugin;

	private ItemParser itemParser;
	private MobParser mobParser;
	
	public CommandExperienceMod(ExperienceMod plugin) {
		this.plugin = plugin;
		
		// Load item and mob parsers
		itemParser = plugin.getConfigLoader().getItemParser();
		mobParser = plugin.getConfigLoader().getMobParser();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		// Execute the correct command
		if (command != null && 
				command.getName().equalsIgnoreCase(COMMAND_RELOAD) || 
				command.getName().equalsIgnoreCase(COMMAND_ABBREVIATED))
			
			return handleMainCommand(sender, args);
		else
			return false;
	}
	
	private boolean handleMainCommand(CommandSender sender, String[] args) {
		
		// Make sure the sender has permissions
		if (!Permissions.hasAdmin(sender)) {
			plugin.respond(sender, ChatColor.RED + "You haven't got permission to execute this command.");
			return true;
		} 
		
		String sub = CommandUtilities.getSafe(args, 0);
		
		// Toggle debugging
		if (sub.equalsIgnoreCase(SUB_COMMAND_TOGGLE_DEBUG)) {
			
			plugin.toggleDebug();
			plugin.respond(sender, ChatColor.BLUE + "Debug " + (plugin.isDebugEnabled() ? " enabled " : " disabled"));
			return true;
			
		// Display the parse warnings during the last configuration load
		} else if (sub.equalsIgnoreCase(SUB_COMMAND_WARNINGS)) {
			
			if (sender != null && plugin.getInformer().hasWarnings())
				plugin.getInformer().displayWarnings(sender, true);
			else
				sender.sendMessage(ChatColor.GREEN + "No warnings found.");
			return true;
			
		} else if (sub.equalsIgnoreCase(SUB_COMMAND_ITEM)) {
			
			handleQueryItem(sender, args, 1);
			return true;
			
		} else if (sub.equalsIgnoreCase(SUB_COMMAND_MOB)) {
			
			handleQueryMob(sender, args, 1);
			return true;
	
		} else if (sub.equalsIgnoreCase(SUB_COMMAND_RELOAD) || sub.length() == 0) {
			
			try {
				plugin.loadDefaults(true);
				plugin.respond(sender, ChatColor.BLUE + "Reloaded ExperienceMod.");
	    		
			} catch (IOException e) {
				plugin.respond(sender, ChatColor.RED + "Error: " + e.getMessage());
			}
			
    		return true;

		} else {
		
			plugin.respond(sender, ChatColor.RED + "Error: Unknown subcommand.");
			return false; 
		}
	}

	private void handleQueryMob(CommandSender sender, String[] args, int offset) {

		try {
			String text = StringUtils.join(args, " ", offset, args.length);
			MobQuery query = mobParser.parse(text);
			
			List<Action> results = plugin.getMobReward(getPlayer(sender), query);
			
			// Query result
			displayActions(sender, results);
			
		} catch (ParsingException e) {
			plugin.respond(sender,
					ChatColor.RED + "Query parsing error: " + e.getMessage());
		}
	}
	
	private void handleQueryItem(CommandSender sender, String[] args, int offset) {

		Integer type = plugin.getActionTypes().getType(
				CommandUtilities.getSafe(args, offset));
		
		// Make sure it's valid
		if (type == null) {
			plugin.respond(sender, ChatColor.RED + "Unknown action type: " + CommandUtilities.getSafe(args, offset));
			return;
		}

		try {
			String text = StringUtils.join(args, " ", offset + 1, args.length);
			Query query = itemParser.parse(text);
			
			// Determine player rewards
			List<Action> results = plugin.getPlayerReward(getPlayer(sender), type, query);
			
			// Finally, display query result
			displayActions(sender, results);

		} catch (IllegalArgumentException e) {
			plugin.respond(sender,
					ChatColor.RED + "Query parsing error: " + e.getMessage());
			
		} catch (ParsingException e) {
			plugin.respond(sender,
					ChatColor.RED + "Query parsing error: " + e.getMessage());
		}
	}
	
	// Gets the player, or NULL
	private Player getPlayer(CommandSender sender) {
		if (sender instanceof Player)
			return (Player) sender;
		else
			return null;
	}
	
	private void displayActions(CommandSender sender, List<Action> actions) {
		
		if (actions == null || actions.isEmpty()) {
			plugin.respond(sender, ChatColor.BLUE + "No results.");
		} else {
			
			plugin.respond(sender, "Result in order of priority:");
			
			// Print every applicable range with the correct at the top
			for (int i = 0; i < actions.size(); i++) {
				plugin.respond(sender, String.format(" %d. %s", i + 1, actions.get(i)));
			}
		}
	}
}
