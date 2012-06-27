package com.comphenix.xp;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.comphenix.xp.rewards.RewardProvider;

public class ConfigurationLoader {

	// Cache of configurations
	private Map<File, Configuration> configurationFiles;
	
	private File rootPath;
	private Debugger logger;
	private RewardProvider provider;
	
	public ConfigurationLoader(File rootPath, Debugger logger, RewardProvider provider) {
		this.rootPath = rootPath;
		this.logger = logger;
		this.provider = provider;
		
		this.configurationFiles = new HashMap<File, Configuration>();
	}
	
	public void clearCache() {
		configurationFiles.clear();
	}
	
	public Configuration getFromPath(String path) {
		
		File absolutePath = new File(rootPath, path);
		
		if (absolutePath.exists())
			return loadFromFile(absolutePath);
		else
			return null;
	}
	
	public Configuration getFromSection(ConfigurationSection data) {

		return new Configuration(data, logger, provider);
	}
	
	private Configuration loadFromFile(File path) {
		
		if (!configurationFiles.containsKey(path)) {
			
			YamlConfiguration yaml = YamlConfiguration.loadConfiguration(path);
			Configuration config = new Configuration(yaml, logger, provider);
			
			// Cache 
			configurationFiles.put(path, config);
			return config;
			
		} else {
			
			// Return previously computed value
			return configurationFiles.get(path);
		}
	}
}
