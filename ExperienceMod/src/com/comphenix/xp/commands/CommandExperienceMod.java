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

import com.comphenix.xp.Action;
import com.comphenix.xp.Configuration;
import com.comphenix.xp.ExperienceMod;
import com.comphenix.xp.lookup.ItemQuery;
import com.comphenix.xp.lookup.MobQuery;
import com.comphenix.xp.lookup.PotionQuery;
import com.comphenix.xp.lookup.Query;
import com.comphenix.xp.parser.ParsingException;
import com.comphenix.xp.parser.text.ItemParser;
import com.comphenix.xp.parser.text.MobParser;

public class CommandExperienceMod implements CommandExecutor {

	private final String permissionAdmin = "experiencemod.admin";

	// Mod command(s)
	private final String commandReload = "experiencemod";
	private final String subCommandToggleDebug = "debug";
	private final String subCommandWarnings = "warnings";
	private final String subCommandReload = "reload";
	private final String subCommandItem = "item";
	private final String subCommandMob = "mob";
	
	private ExperienceMod plugin;

	private ItemParser itemParser = new ItemParser();
	private MobParser mobParser = new MobParser();
	
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
		if (!CommandUtilities.hasCommandPermission(sender, permissionAdmin)) {
			plugin.respond(sender, ChatColor.RED + "You haven't got permission to execute this command.");
			return true;
		} 
		
		String sub = CommandUtilities.getSafe(args, 0);
		
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
			
		} else if (sub.equalsIgnoreCase(subCommandItem)) {
			
			handleQueryItem(sender, args, 1);
			return true;
			
		} else if (sub.equalsIgnoreCase(subCommandMob)) {
			
			handleQueryMob(sender, args, 1);
			return true;
	
		} else if (sub.equalsIgnoreCase(subCommandReload) || sub.length() == 0) {
			
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
			Configuration config = plugin.getPresets().getConfiguration(sender);
			String text = StringUtils.join(args, " ", offset, args.length);
			
			MobQuery query = mobParser.parse(text);
			List<Action> results = config.getExperienceDrop().getAllRanked(query);
			
			// Query result
			displayActions(sender, results);
			
		} catch (ParsingException e) {
			plugin.respond(sender,
					ChatColor.RED + "Query parsing error: " + e.getMessage());
		}
	}
	
	private void handleQueryItem(CommandSender sender, String[] args, int offset) {

		Configuration.ActionTypes type = Configuration.ActionTypes.matchAction(
				CommandUtilities.getSafe(args, offset));
		
		// Make sure it's valid
		if (type == null) {
			plugin.respond(sender, ChatColor.RED + "Unknown action type: " + CommandUtilities.getSafe(args, offset));
			return;
		}

		try {
			Configuration config = plugin.getPresets().getConfiguration(sender);
			String text = StringUtils.join(args, " ", offset + 1, args.length);
			
			Query query = itemParser.parse(text);
			List<Action> results = null;
			
			switch (type) {
			case BLOCK:
				results = config.getSimpleBlockReward().getAllRanked((ItemQuery) query); 
				break;
			case BONUS:
				results = config.getSimpleBonusReward().getAllRanked((ItemQuery) query); 
				break;
			case CRAFTING:
				results = config.getSimpleCraftingReward().getAllRanked((ItemQuery) query); 
				break;
			case SMELTING:
				results = config.getSimpleSmeltingReward().getAllRanked((ItemQuery) query); 
				break;
			case PLACE:
				results = config.getSimplePlacingReward().getAllRanked((ItemQuery) query); 
				break;
			case BREWING:
				// Handle both possibilities
				if (query instanceof ItemQuery)
					results = config.getSimpleBrewingReward().getAllRanked((ItemQuery) query);
				else
					results = config.getComplexBrewingReward().getAllRanked((PotionQuery) query);
				break;
			}
			
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
