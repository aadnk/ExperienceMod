package com.comphenix.xp;

import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.xp.lookup.Parsing;

public class ExperienceMod extends JavaPlugin {
	// Mod command(s)
	private final String commandReload = "experiencemod";
	private final String toggleDebug = "DEBUG";
	
	private Logger currentLogger;
	private ExperienceListener listener;
	private Configuration configuration;
	
	private boolean debugEnabled;
	
	@Override
	public void onEnable() {
		PluginManager manager = getServer().getPluginManager();
		
		currentLogger = this.getLogger();
		
		// Intialize configuration and listeners
		loadDefaults();
		listener = new ExperienceListener(this, configuration);
		
		// Begin changing stuff
		manager.registerEvents(listener, this);
	}
	
	private void loadDefaults() {
		FileConfiguration config = getConfig();

		// Supply default values if empty
		if (!config.contains("multiplier")) {
			config.options().copyDefaults(true);
			saveConfig();
		}
		
		// Load it
		configuration = new Configuration(config, currentLogger);
	}
	
	@Override
	public void onDisable() {
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
    	if(cmd.getName().equalsIgnoreCase(commandReload)) {
    		
    		// Undocumented.
    		if (args.length > 0) {
    			
    			// Toggle debuging
    			if (Parsing.getEnumName(args[0]).equals(toggleDebug)) {
    				debugEnabled = !debugEnabled;
    				respond(sender, "Debug " + (debugEnabled ? " enabled " : " disabled"));
    			} else {
    				respond(sender, "Error: Unknown subcommand.");
    			}
    
    		} else {
    			
    			loadDefaults();
	    		listener.setConfiguration(configuration);
	    		respond(sender, "Reloaded ExperienceMod.");
	    		return true;
    		}
    	}
  
    	return false; 
    }
	
	public boolean isDebugEnabled() {
		return debugEnabled;
	}
	
	public void printDebug(String message) {
		if (debugEnabled)
			currentLogger.info("DEBUG: " + message);
	}
	
	private void respond(CommandSender sender, String message) {
		if (sender == null) // Sent by the console
			currentLogger.info(message);
		else
			sender.sendMessage(message);
	}
}
