package com.comphenix.xp.messages;

import org.bukkit.entity.Player;

import com.dthielke.herochat.Channel;
import com.dthielke.herochat.Chatter;
import com.dthielke.herochat.Herochat;

public class HeroService implements ChannelService {

	public HeroService() {

		try {
			// Make sure
			if (!Herochat.getPlugin().isEnabled())
				throw new Exception("Not enabled");
			
		} catch (Exception e) {
			throw new IllegalArgumentException("HeroChat hasn't been enabled.");
		}
	}
	
	@Override
	public boolean hasChannel(String channelID) {
		// See if this channel exists
		return Herochat.getChannelManager().hasChannel(channelID);
	}

	@Override
	public void announce(String channelID, String message) {
	
		// Stores channels in a HashMap, so it should return NULL if the channel doesn't exist 
		Channel channel = Herochat.getChannelManager().getChannel(channelID);
			
		if (channel == null) {
			throw new IllegalArgumentException("Channel doesn't exist.");
		}
		
		channel.announce(message);
	}

	@Override
	public void emote(String channelID, String message, Player sender) {

		Chatter playerChatter = Herochat.getChatterManager().getChatter(sender);
		
		// Emote for this character
		getChannel(channelID).emote(playerChatter, message);
	}

	private Channel getChannel(String channelID) {
		
		// Stores channels in a HashMap, so it should return NULL if the channel doesn't exist 
		Channel channel = Herochat.getChannelManager().getChannel(channelID);
			
		if (channel == null) {
			throw new IllegalArgumentException("Channel doesn't exist.");
		} else {
			return channel;
		}
	}
	
	@Override
	public String getServiceName() {
		return "HEROCHAT";
	}
}
