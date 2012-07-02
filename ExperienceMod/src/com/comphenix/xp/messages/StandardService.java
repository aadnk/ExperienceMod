package com.comphenix.xp.messages;

import java.util.HashMap;
import java.util.Map;

import com.comphenix.xp.parser.Utility;

import org.bukkit.Server;
import org.bukkit.entity.Player;

public class StandardService implements ChannelService {

	public static final String NAME = "STANDARD";

	private static Map<String, Channels> channelList = new HashMap<String, Channels>();
	
	public enum Channels {
		GLOBAL,
		WORLD,
		PRIVATE;
		
		private Channels() {
			channelList.put(this.name(), this);
		}
	}
	
	private Server server;
	
	public StandardService(Server server) {
		this.server = server;
	}
	
	@Override
	public String getServiceName() {
		return NAME;
	}

	@Override
	public boolean hasChannel(String channelID) {
		return getChannel(channelID) != null;
	}

	@Override
	public void announce(String channelID, String message) {
		// Do it globally. Private messages will be lost.
		if (hasChannel(channelID)) {
			switch (getChannel(channelID)) {
			case GLOBAL:
			case WORLD:
				server.broadcastMessage(message);
				break;
			}
		}
	}

	@Override
	public void emote(String channelID, String message, Player sender) {
		// Decide what to do
		if (hasChannel(channelID)) {
			switch (getChannel(channelID)) {
			case GLOBAL:
				server.broadcastMessage(message);
				break;
				
			case WORLD:
				// Broadcast to every player located in the same world
				if (sender != null) {
					for (Player player : sender.getWorld().getPlayers()) {
						player.sendMessage(message);
					}
				}
				
				break;
				
			case PRIVATE:
				// Private message
				if (sender != null)
					sender.sendMessage(message);
				break;
			}
		}
	}

	private Channels getChannel(String channelID) {
		// Look up channel 
		return channelList.get(Utility.getEnumName(channelID));
	}
}
