package com.comphenix.xp.messages;

import org.bukkit.entity.Player;

import com.feildmaster.channelchat.channel.Channel;
import com.feildmaster.channelchat.channel.ChannelManager;

/**
 * Handles the ChannelChat plugin:
 * <blockquote>http://dev.bukkit.org/server-mods/channel-chat/</blockquote>
 *  
 * @author Kristian
 *
 */
public class ChannelChatService implements ChannelService {

	public static final String NAME = "CHANNELCHAT";
	
	@Override
	public String getServiceName() {
		return NAME;
	}

	/**
	 * Determines whether or not the ChannelChat plugin is loaded AND enabled.
	 * @return TRUE if it is, FALSE otherwise.
	 */
	public static boolean exists() {
		try {
			// Make sure it exists
			if (ChannelManager.getManager() != null)
				return true;
			else
				return false;
			
			// Cannot load plugin
		} catch (NullPointerException e)  {
			return false;
		} catch (NoClassDefFoundError e) {
			return false;
		}
	}
	
	@Override
	public boolean hasChannel(String channelID) {
		try {
			// See if this channel exists
			return ChannelManager.getManager().channelExists(channelID);
		} catch (NullPointerException e) {
			return false;
		}
	}

	@Override
	public void announce(String channelID, String message) {
		try {
			getChannel(channelID).sendMessage(" " + message);
			
			// Handle this too
		} catch (NullPointerException e) {
			throw new IllegalArgumentException("Cannot find ChannelChat channel manager.");
		}
	}

	@Override
	public void emote(String channelID, String message, Player sender) {

		// We only want to send the message, not act as if we're the player
		announce(channelID, message);
	}
	
	private Channel getChannel(String channelID) {
		
		// Stores channels in a HashMap, so it should return NULL if the channel doesn't exist 
		Channel channel = ChannelManager.getManager().getChannel(channelID);
			
		if (channel == null) {
			throw new IllegalArgumentException("Channel doesn't exist.");
		} else {
			return channel;
		}
	}
}
