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

public class ActionParser extends Parser<ConfigurationSection, Action> {

	private static final String messageTextSetting = "message";
	private static final String messageChannelSetting = "channels";
	
	private ParameterParser<String> textParsing = new ParameterParser<String>(new StringParser());
	private RewardProvider provider;
	
	public ActionParser(RewardProvider provider) {
		this.provider = provider;
	}
	
	@Override
	public Action parse(ConfigurationSection input) throws ParsingException {
		
		Action result = new Action();
		Range topLevel = readRange(input, null);
		
		String text = null;
		List<String> channels = new ArrayList<String>();
		
		// This is a default range value
		if (topLevel != null) {
			result.addReward(provider.getDefaultReward(), topLevel);
			return result;
		}
		
		// If not, get sub-rewards
		for (String key : input.getKeys(false)) {
			
			if (key.equalsIgnoreCase(messageTextSetting)) {
				text = input.getString(key);
			} else if (key.equalsIgnoreCase(messageChannelSetting)) {
				channels = textParsing.parse(input.getString(key));
			} else {
				Range range = readRange(input.getConfigurationSection(key), null);
				
				if (range != null) {
					result.addReward(key, range);
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
	
	public Action parse(ConfigurationSection input, String key) throws ParsingException {
		return parse(input.getConfigurationSection(key));
	}
	
	private Range readRange(ConfigurationSection config, Range defaultValue) {
		
		String start = "first";
		String end = "last";
		
		if (config.isDouble("")) {
			return new Range(config.getDouble(""));
			
		} else if (config.isInt("")) {
			return new Range((double) config.getInt(""));
			
		} else if (config.contains(start) && config.contains(end)) {
			return new Range(config.getDouble(start), config.getDouble(end));
	
		} else if (config.isList("")) {
			// Try to get a double list
			List<Double> attempt = config.getDoubleList("");

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
