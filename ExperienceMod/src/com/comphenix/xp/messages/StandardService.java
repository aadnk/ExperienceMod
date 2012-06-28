package com.comphenix.xp.messages;

import org.bukkit.Server;
import org.bukkit.entity.Player;

public class StandardService implements ChannelService {

	public static final String NAME = "STANDARD";

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
		return true;
	}

	@Override
	public void announce(String channelID, String message) {
		// Do it globally
		server.broadcastMessage(message);
	}

	@Override
	public void emote(String channelID, String message, Player sender) {
		// And here too
		server.broadcastMessage(message);
	}

}
