package com.comphenix.xp.messages;

import org.bukkit.command.CommandSender;

import com.comphenix.xp.extra.Service;

public interface ChannelService extends Service {
	
	/**
	 * Determines whether or not the channel with the given identifier exists.
	 * @param channelID - channel identifier.
	 * @return TRUE if the channel exists, FALSE otherwise.
	 */
	public boolean hasChannel(String channelID);
	
	/**
	 * Broadcast a message on a given channel.
	 * @param channelID - channel identifier. 
	 * @param message - message to broadcast.
	 */
	public void announce(String channelID, String message);
	
	/**
	 * Sends a message with a sender on a given channel.
	 * @param channelID - channel identifier. 
	 * @param message - message to broadcast.
	 * @param sender - sender to include in the message.
	 */
	public void emote(String channelID, String message, CommandSender sender);
}
