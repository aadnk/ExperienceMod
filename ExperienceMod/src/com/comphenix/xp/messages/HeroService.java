package com.comphenix.xp.messages;

import org.bukkit.command.CommandSender;

public class HeroService implements ChannelService {

	@Override
	public boolean hasChannel(String channelID) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void announce(String channelID, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void emote(String channelID, String message, CommandSender sender) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getServiceName() {
		return "HEROCHAT";
	}
}
