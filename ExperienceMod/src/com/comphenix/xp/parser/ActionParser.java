package com.comphenix.xp.parser;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import com.comphenix.xp.Action;
import com.comphenix.xp.Range;
import com.comphenix.xp.messages.Message;
import com.comphenix.xp.parser.primitives.StringParser;
import com.comphenix.xp.parser.text.ParameterParser;
import com.comphenix.xp.rewards.RewardProvider;

public class ActionParser {

	private static final String messageTextSetting = "message";
	private static final String messageChannelSetting = "channels";
	
	private ParameterParser<String> textParsing = new ParameterParser<String>(new StringParser());
	private RewardProvider provider;
	
	public ActionParser(RewardProvider provider) {
		this.provider = provider;
	}
	
	public Action parse(ConfigurationSection input, String key) throws ParsingException {
		
		if (input == null)
			return null;

		Action result = new Action();
		Range topLevel = readRange(input, key, null);
		
		String text = null;
		List<String> channels = new ArrayList<String>();
		
		// This is a default range value
		if (topLevel != null) {
			result.addReward(provider.getDefaultReward(), topLevel);
			return result;
		}
		
		ConfigurationSection values = input.getConfigurationSection(key);
		
		// See if this is a configuration section
		if (values == null)
			return null;
		
		// If not, get sub-rewards
		for (String sub : values.getKeys(false)) {
			
			if (sub.equalsIgnoreCase(messageTextSetting)) {
				text = input.getString(key);
			} else if (sub.equalsIgnoreCase(messageChannelSetting)) {
				channels = textParsing.parse(input.getString(key));
			} else {
				Range range = readRange(values, sub, null);
				
				if (range != null) {
					result.addReward(sub, range);
				}
			}
			
		}
		
		// Add message
		if (text != null) {
			Message message = new Message();
			message.setText(text);
			message.setChannels(channels);
			result.setMessage(message);
		}
		
		return result;
	}
	
	private Range readRange(ConfigurationSection config, String key, Range defaultValue) {
		
		String start = key + ".first";
		String end = key + ".last";
		
		if (config.isDouble(key)) {
			return new Range(config.getDouble(key));
			
		} else if (config.isInt(key)) {
			return new Range((double) config.getInt(key));
			
		} else if (config.contains(start) && config.contains(end)) {
			return new Range(config.getDouble(start), config.getDouble(end));
	
		} else if (config.isList(key)) {
			// Try to get a double list
			List<Double> attempt = config.getDoubleList(key);

			if (attempt != null && attempt.size() == 2)
				return new Range(attempt.get(0), attempt.get(1));
			else if (attempt != null && attempt.size() == 1)
				return new Range(attempt.get(0));
			else
				return defaultValue;
			
		} else {
			// Default value
			return defaultValue;
		}
	}
}
