package com.comphenix.xp;

/**
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

import java.io.File;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.xp.Configuration.RewardTypes;
import com.comphenix.xp.commands.CommandExperienceMod;
import com.comphenix.xp.commands.CommandSpawnExp;

public class ExperienceMod extends JavaPlugin implements Debugger {
	
	private final String permissionInfo = "experiencemod.info";
	
	private final String commandReload = "experiencemod";
	private final String commandSpawnExp = "spawnexp";
	
	private Logger currentLogger;
	private PluginManager manager;
	private Economy economy;
	
	private ExperienceListener listener;
	private ExperienceInformer informer;
	private Configuration configuration;
	
	// Commands
	private CommandExperienceMod commandExperienceMod;
	private CommandSpawnExp commandSpawn;
	
	private boolean debugEnabled;
	
	@Override
	public void onEnable() {
		manager = getServer().getPluginManager();
		
		currentLogger = this.getLogger();
		informer = new ExperienceInformer();
		
		commandExperienceMod = new CommandExperienceMod(this);
		commandSpawn = new CommandSpawnExp(this);
		
		// Load economy, if it exists
		if (!hasEconomy())
			setupEconomy();
		
		// Initialize configuration
		loadDefaults(false);
		
		// Register listeners
		manager.registerEvents(listener, this);
		manager.registerEvents(informer, this);
		
		// Register commands
		getCommand(commandReload).setExecutor(commandExperienceMod);
		getCommand(commandSpawnExp).setExecutor(commandSpawn);
	}
	
	public void loadDefaults(boolean reload) {
		FileConfiguration config = getConfig();
		File path = new File(getDataFolder(), "config.yml");
		
		// Reset warnings
		informer.clearMessages();
		
		// See if we need to create the file
		if (!path.exists()) {
			// Supply default values if empty
			config.options().copyDefaults(true);
			saveConfig();
			currentLogger.info("Creating default configuration file.");
		}
		
		// Read from disk again
		if (reload) {
			reloadConfig();
			
			// Reload internal representation
			configuration = null;
			config = getConfig();
		}
		
		// Load it
		if (configuration == null) {
			configuration = new Configuration(config, this);
			setConfiguration(configuration);
		}
		
		RewardTypes reward = configuration.getRewardType();
		
		// See if we actually can enable the economy
		if (economy == null && reward == RewardTypes.ECONOMY) {
			printWarning(this, "Cannot enable economy. VAULT plugin was not found.");
			reward = RewardTypes.EXPERIENCE;
		}
		
		// Set reward type
		switch (reward) {
		case EXPERIENCE:
			listener.setRewardManager(new RewardExperience());
			currentLogger.info("Using experience as reward.");
			break;
		case VIRTUAL:
			listener.setRewardManager(new RewardVirtual());
			currentLogger.info("Using virtual experience as reward.");
			break;
		case ECONOMY:
			listener.setRewardManager(new RewardEconomy(economy, this));
			currentLogger.info("Using the economy as reward.");
			break;
		default:
			printWarning(this, "Unknown reward manager.");
			break;
		}
	}
	
	private void setupEconomy()
    {
		try {
	        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
	        
	        if (economyProvider != null) {
	            economy = economyProvider.getProvider();
	        }
        
		} catch (NoClassDefFoundError e) {
			// No vault
			return;
		}
    }
	
	private boolean hasEconomy() {
		return economy != null;
	}
	
	private void setConfiguration(Configuration configuration) {
		
		// Create a new listener if necessary
		if (listener == null) {
			listener = new ExperienceListener(this, this, configuration);
		} else {
			listener.setConfiguration(configuration);
		}
	}
	
	@Override
	public void onDisable() {
	}
	
	public boolean hasCommandPermission(CommandSender sender, String permission) {
		
		// Make sure the sender has permissions
		if (sender != null && !sender.hasPermission(permission)) {
			return false;
		} else {
			// We have permission
			return true;
		}
	}

	@Override	
	public boolean isDebugEnabled() {
		return debugEnabled;
	}
	
	public void toggleDebug() {
		debugEnabled = !debugEnabled;
	}
	
	public ExperienceInformer getInformer() {
		return informer;
	}
	
	public Configuration getConfiguration() {
		return configuration;
	}
	
	@Override
	public void printDebug(Object sender, String message, Object... params) {
		if (debugEnabled) {
			
			String formattedMessage = String.format("[ExperienceMod] " + message, params);
			
			// Every player with the info permission will also see this message
			getServer().broadcast(formattedMessage, permissionInfo);
		}
	}

	public void respond(CommandSender sender, String message) {
		if (sender == null) // Sent by the console
			currentLogger.info(message);
		else
			sender.sendMessage(message);
	}

	@Override
	public void printWarning(Object sender, String message, Object... params) {
		String warningMessage = ChatColor.RED + "Warning: " + message;
		
		// Print immediately
		currentLogger.warning(String.format(warningMessage, params));
		
		// Add to list of warnings
	    informer.addWarningMessage(String.format(message, params));
	}
}
