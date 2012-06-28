package com.comphenix.xp.messages;

import java.util.ArrayList;
import java.util.List;

import com.comphenix.xp.extra.ServiceProvider;

/**
 * Contains every channel messaging channel plugins known and enabled on the server. The default plugin will
 * be used to send messages.
 * 
 * @author Kristian
 */
public class ChannelProvider extends ServiceProvider<ChannelService> {

	private List<String> defaultChannels = new ArrayList<String>();

	public ChannelProvider() {
		super(HeroService.NAME);
	}
	
	public ChannelProvider(ChannelProvider other) {
		super(other.getDefaultName());
		setDefaultChannels(other.getDefaultChannels());
	}
	
	public ChannelProvider(String defaultService) {
		super(defaultService);
	}
	
	public List<String> getDefaultChannels() {
		return defaultChannels;
	}

	public void setDefaultChannels(List<String> defaultChannels) {
		this.defaultChannels = defaultChannels;
	}
	
	/**
	 * Creates a copy of this channel provider with shallow references to the same list of channel services, except with a different
	 * internal default reward type. 
	 * @param config Configuration settings for the different services.
	 * @return A shallow copy of this reward service provider.
	 */
	public ChannelProvider createView() {
		return new ChannelProvider(this);
	}
}
