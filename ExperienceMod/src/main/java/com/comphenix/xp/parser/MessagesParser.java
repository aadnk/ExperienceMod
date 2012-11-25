package com.comphenix.xp.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import com.comphenix.xp.messages.Message;
import com.comphenix.xp.parser.primitives.StringParser;
import com.comphenix.xp.parser.text.ParameterParser;
import com.google.common.collect.Lists;

/**
 * Represents a message parser. 
 * <p>
 * Note that this parser, unlike most other parsers, may MODIFY any configuration section
 * given to it. To avoid polluting the configuration file, pass it cloned sections (using
 * the Utility.cloneSection() method).
 * @author Kristian
 *
 */
public class MessagesParser extends ConfigurationParser<List<Message>> {

	private static final String MESSAGES_SETTING = "messages";
	
	// For backwards compatibility
	private static final String MESSAGE_TEXT_SETTING = "message";
	private static final String MESSAGE_CHANNEL_SETTING = "channels";
	
	private boolean consumeElements;
	
	// Parsers
	private StringListParser listParser;
	private ParameterParser<String> channelsParser = new ParameterParser<String>(new StringParser());
	
	/**
	 * Constructs a message parser with a default string list parser.
	 * @param consumeElements - whether or not to remove successfully read key-value pairs.
	 */
	public MessagesParser(boolean consumeElements) {
		this(consumeElements, new StringListParser());
	}
	
	/**
	 * Constructs a message parser with a default string list parser.
	 * @param consumeElements - whether or not to remove successfully read key-value pairs.
	 * @param listParser - a list parser used to read the channel list.
	 */
	public MessagesParser(boolean consumeElements, StringListParser listParser) {
		this.listParser = listParser;
		this.consumeElements = consumeElements;
	}
	
	/**
	 * Use the default key names to parse a configuration section with messages.
	 * @param input - the configuration section to parse.
	 * @return List of parsed messages, or an empty list if no messages could be found.
	 */
	public List<Message> parse(ConfigurationSection input) throws ParsingException {
		
		List<Message> result = new ArrayList<Message>();
		List<Message> list = parse(input, MESSAGES_SETTING);
		
		// Damn backwards compatibility
		List<Message> backwards = parse(input, MESSAGE_TEXT_SETTING, MESSAGE_CHANNEL_SETTING);
		
		if (list != null && list.size() > 0)
			result.addAll(list);
		if (backwards != null && backwards.size() > 0)
			result.addAll(backwards);

		return result;
	}
	
	// The more dynamic implementation
	@Override
	public List<Message> parse(ConfigurationSection input, String key) throws ParsingException {

		List<Message> result = new ArrayList<Message>();
		
		// Look for the key manually. This is really to preseve backwards compatbility.
		for (String sub : input.getKeys(false).toArray(new String[0])) {
			String enumed = Utility.getEnumName(sub);
		
			// Note that we may also remove the element when it has been found
			if (enumed.equalsIgnoreCase(key)) {
				
				// This should be a configuration section itself
				Object rawValue = input.get(sub);
				
				if (rawValue instanceof ConfigurationSection) {
					
					ConfigurationSection section = (ConfigurationSection) rawValue;
					
					// Add every message in the list
					for (String channelList : section.getKeys(false)) {
						List<String> channels = channelsParser.parse(channelList);
						String text = section.getString(channelList);
						
						if (text != null)
							result.add(new Message(text, channels));
						else
							throw ParsingException.fromFormat("The channel(s) %s has no message.", channelList);
					}
					
				} else {
					throw ParsingException.fromFormat("Cannot parse multimessage - must be a dictionary of values.");
				}
				
				// It has now been successfully read. Remove it?
				if (consumeElements) {
					input.set(sub, null);
				}
			}
		}
		
		// Empty list if no message can be found
		return result;
	}
	
	/**
	 * Converts the given configuration section into a message list.
	 * <p>
	 * Note that this method is used to support the old message syntax of:
	 * <pre>
 	 * {@code
	 * mobs:
	 *   zombie:
	 *     default: 10
	 *     message: 'A message'
	 *     channels: [LOCAL]
	 * }
	 * </pre>
	 * @param input - source configuration section.
	 * @param messageKey - name of the message key.
	 * @param channelKey - the channels to broadcast this message.
	 * @return A list containing the parsed message, or an empty list if no message is found.
	 * @throws ParsingException - an error occured in parsing the message.
	 */
	public List<Message> parse(ConfigurationSection input, String messageKey, String channelKey) throws ParsingException {

		// We don't care about the default value
		return parse(input, messageKey, channelKey, Collections.<Message>emptyList(), true);
	}	
	
	/**
	 * Converts the given configuration section into a message list.
	 * <p>
	 * Note that this method is used to support the old message syntax of:
	 * <pre>
 	 * {@code
	 * mobs:
	 *   zombie:
	 *     default: 10
	 *     message: 'A message'
	 *     channels: [LOCAL]
	 * }
	 * </pre>
	 * @param input - source configuration section.
	 * @param messageKey - name of the message key.
	 * @param channelKey - the channels to broadcast this message.
	 * @return A list containing the parsed message, or an empty list if no message is found.
	 * @throws ParsingException - an error occured in parsing the message.
	 */
	public List<Message> parse(ConfigurationSection input, String messageKey, String channelKey, Message defaultValue) {
		
		try { 
			return parse(input, messageKey, channelKey, Lists.newArrayList(defaultValue), false);
		} catch (ParsingException e) {
			// Should never happen
			throw new IllegalStateException("A parsing exception occured unexpectedly.", e);
		}
	}

	// Common implementation - simplifies things a bit
	private List<Message> parse(ConfigurationSection input, String messageKey, String channelKey, List<Message> defaultValue, boolean throwIfError) throws ParsingException {
		
		List<Message> result = new ArrayList<Message>();
		
		String text = null;
		List<String> channels = null;
		
		for (String sub : input.getKeys(false).toArray(new String[0])) {
			String enumed = Utility.getEnumName(sub);
			
			// Note that we also remove each element that is found
			if (enumed.equalsIgnoreCase(messageKey)) {
				text = input.getString(sub);
				
				// Distinguish between not found and invalid
				if (text == null && throwIfError)
					throw ParsingException.fromFormat("Unable to read message.");
				
			} else if (enumed.equalsIgnoreCase(channelKey)) {
				if (throwIfError)
					channels = listParser.parse(input, sub);
				else
					channels = listParser.parseSafe(input, sub);
				
			} else {
				continue;
			}
			
			// An element was successfully read. Remove it?
			if (consumeElements) {
				input.set(sub, null);
			}
		}
		
		// Handle missing message key
		if (text != null) {
			result.add(new Message(text, channels));
			return result; 
		} else {
			return defaultValue;
		}
	}
	
	/**
	 * Returns whether or not to remove successfully read key-value pairs.
	 * @return TRUE if successfully read elements are removed, FALSE otherwise.
	 */
	public boolean isConsumeElements() {
		return consumeElements;
	}
}
