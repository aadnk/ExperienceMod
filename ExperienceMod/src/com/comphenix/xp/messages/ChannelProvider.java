package com.comphenix.xp.messages;

import com.comphenix.xp.extra.ServiceProvider;

/**
 * Contains every channel messaging channel plugins known and enabled on the server. The default plugin will
 * be used to send messages.
 * 
 * @author Kristian
 */
public class ChannelProvider extends ServiceProvider<ChannelService> {

	public ChannelProvider(String defaultService) {
		super(defaultService);
	}
}
