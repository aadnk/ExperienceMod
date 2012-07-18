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

package com.comphenix.xp;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.comphenix.xp.messages.ChannelProvider;
import com.comphenix.xp.rewards.RewardProvider;

public class ConfigurationLoader {

	// Cache of configurations
	private Map<File, Configuration> configurationFiles;
	
	private File rootPath;
	private Debugger logger;
	private RewardProvider rewardProvider;
	private ChannelProvider channelProvider;
	
	public ConfigurationLoader(File rootPath, Debugger logger, RewardProvider rewardProvider, ChannelProvider channelProvider) {
		this.rootPath = rootPath;
		this.logger = logger;
		this.rewardProvider = rewardProvider;
		this.channelProvider = channelProvider;
		
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

		return new Configuration(data, logger, rewardProvider, channelProvider);
	}
	
	private Configuration loadFromFile(File path) {
		
		if (!configurationFiles.containsKey(path)) {
			
			YamlConfiguration yaml = YamlConfiguration.loadConfiguration(path);
			Configuration config = new Configuration(yaml, logger, rewardProvider, channelProvider);
			
			// Cache 
			configurationFiles.put(path, config);
			return config;
			
		} else {
			
			// Return previously computed value
			return configurationFiles.get(path);
		}
	}
}
