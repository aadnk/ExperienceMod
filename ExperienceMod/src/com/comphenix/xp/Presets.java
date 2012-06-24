package com.comphenix.xp;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import net.milkbowl.vault.chat.Chat;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.comphenix.xp.lookup.PresetQuery;
import com.comphenix.xp.lookup.PresetTree;
import com.comphenix.xp.parser.ParsingException;
import com.comphenix.xp.parser.PresetParser;
import com.google.common.collect.Lists;

/**
 * Contains every loaded configuration preset.
 */
public class Presets {

	// Mapping of preset name and configuration
	private PresetTree presets;
	
	// Parser
	private PresetParser presetParser = new PresetParser();
	
	// Cache of configurations
	private HashMap<File, Configuration> configurationFiles;
	private Debugger logger;
	
	// Chat
	private Chat chat;
	
	public Presets(ConfigurationSection config, Debugger debugger, Chat chat, File dataFolder) {
		this.logger = debugger;
		this.presets = new PresetTree();
		this.chat = chat;
		this.configurationFiles = new HashMap<File, Configuration>();
		
		loadPresets(config, dataFolder);
		configurationFiles.clear();
	}
	
	/**
	 * Retrieves a stored configuration from a key value.
	 * @param keyName Key value of the configuration to retrieve.
	 * @param worldName TRUE to return the default configuration if no such key exists.
	 * @return The stored configuration, or NULL if no configuration exists.
	 */
	public Configuration getConfiguration(String presetName, String worldName) {
		
		PresetQuery query = new PresetQuery(presetName, worldName);
		Configuration result = presets.get(query);
		
		// Determine what to return
		if (result != null)
			return result;
		else
			return null;
	}
	
	public Configuration getConfiguration(CommandSender sender) {
		
		String preset = null;
		String world = null;
		
		if (chat != null && sender instanceof Player) {
			Player player = (Player) sender;
			preset = chat.getPlayerInfoString(player, "experience", null);
			world = player.getWorld().getName();
		}
		
		return getConfiguration(preset, world);
	}
	
	private void loadPresets(ConfigurationSection section, File dataFolder) {
		for (String key : section.getKeys(false)) { 
			
			// Load query
			try {
				PresetQuery query = presetParser.parse(key);
			
				// Load section
				Configuration data = loadPreset(
						section.getConfigurationSection(key), dataFolder);
				
				if (data != null)
					presets.put(query, data);
			
			} catch (ParsingException ex) {
				logger.printWarning(this, "Cannot parse preset - %s", ex.getMessage());
			}
		}
	}
	
	private Configuration loadPreset(ConfigurationSection data, File dataFolder) {
		
		List<Configuration> files = getConfigurations(data, dataFolder);
		Configuration local = getLocal(data);
		Configuration result = null;
		
		// Local configuration has the highest priority
		if (local != null) {
			files.add(local);
		}
		
		// Make sure there is anything to return
		if (files.isEmpty())
			result = new Configuration(logger);
		else
			result = Configuration.fromMultiple(files, logger);
		
		return result;
	}
	
	private Configuration getLocal(ConfigurationSection data) {
		
		if (data.isConfigurationSection("local"))
			return new Configuration(
					data.getConfigurationSection("local"), logger);
		else
			return null;
	}
	
	private List<Configuration> getConfigurations(ConfigurationSection data, File dataFolder) {
		
		List<Configuration> result = new ArrayList<Configuration>();
		
		for (String path : getFiles(data)) {
		
			File absolutePath = new File(dataFolder, path);
			
			if (absolutePath.exists())
				result.add(loadFromFile(absolutePath));
			else
				logger.printWarning(this, "Cannot find configuration file %s.", path);
		}
		
		return result;
	}
	
	private List<String> getFiles(ConfigurationSection data) {
		
		if (data.isString("file"))
			return Lists.newArrayList(data.getString("file"));
		else if (data.isList("file"))
			return data.getStringList("file");
		else
			return Lists.newArrayList();
	}
	
	private Configuration loadFromFile(File path) {
		
		if (!configurationFiles.containsKey(path)) {
			
			YamlConfiguration yaml = YamlConfiguration.loadConfiguration(path);
			Configuration config = new Configuration(yaml, logger);
			
			// Cache 
			configurationFiles.put(path, config);
			return config;
			
		} else {
			
			// Return previously computed value
			return configurationFiles.get(path);
		}
	}

	public Collection<Configuration> getConfigurations() {
		return presets.getValues();
	}
}
