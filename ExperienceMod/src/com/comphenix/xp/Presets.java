package com.comphenix.xp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.collect.Lists;

/**
 * Contains every loaded configuration preset.
 */
public class Presets {

	// Mapping of preset name and configuration
	private HashMap<String, Configuration> presets;
	
	// Cache of configurations
	private HashMap<File, Configuration> configurationFiles;
	
	private Debugger logger;
	private Configuration defaultConfiguration;
	
	public Presets(ConfigurationSection config, Debugger debugger, File dataFolder) {
		this.logger = debugger;
		this.presets = new HashMap<String, Configuration>();
		this.configurationFiles = new HashMap<File, Configuration>();
		
		loadPresets(config, dataFolder);
	}
	
	/**
	 * Retrieves a stored configuration from a key value.
	 * @param keyName Key value of the configuration to retrieve.
	 * @param useDefault TRUE to return the default configuration if no such key exists.
	 * @return The stored configuration, or the default configuration if useDefault is TRUE; null otherwise.
	 */
	public Configuration getConfiguration(String keyName, boolean useDefault) {
		
		Configuration result = presets.get(keyName); 
		
		// Determine what to return
		if (result != null)
			return result;
		else if (useDefault)
			return defaultConfiguration;
		else
			return null;
	}
	
	private void loadPresets(ConfigurationSection section, File dataFolder) {
		for (String key : section.getKeys(false)) { 
			
			// Load section
			Configuration data = loadPreset(
					section.getConfigurationSection(key), dataFolder);
			
			if (data != null)
				presets.put(key, data);
		}
	}
	
	private Configuration loadPreset(ConfigurationSection data, File dataFolder) {
		
		boolean isDefault = data.getBoolean("default", false);

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
		
		// Update default value
		if (isDefault) {
			if (defaultConfiguration != null)
				logger.printWarning(this, "Multiple default sections detected!");
			
			defaultConfiguration = result;
		}
		
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
}
