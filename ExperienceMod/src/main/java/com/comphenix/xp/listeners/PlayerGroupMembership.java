package com.comphenix.xp.listeners;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.entity.Player;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

import net.milkbowl.vault.chat.Chat;

/**
 * Used to determine which groups a member belongs to.
 * 
 * @author Kristian
 */
public class PlayerGroupMembership {

	private static final int MAXIMUM_CACHE_SIZE = 1000;

	// Lookup of group membership
	private Cache<Player, String[]> groupCache;
	private Chat chat;
	
	// Null string array
	private String[] empty = new String[0];
	
	public PlayerGroupMembership(Chat chat) {
		this.chat = chat;
		
		// Use the default timeout of ten seconds
		initializeCache(10); 
	}
	
	protected void initializeCache(int timeout) {
		
		// Initialize cache if it's needed
		if (timeout > 0 && chat != null) {
			groupCache = CacheBuilder.newBuilder().
					weakKeys().
					weakValues().
					maximumSize(MAXIMUM_CACHE_SIZE).
					expireAfterWrite(timeout, TimeUnit.SECONDS).
					build(new CacheLoader<Player, String[]>() {
						@Override
						public String[] load(Player player) throws Exception {
							return chat.getPlayerGroups(player);
						}
					});
		}
	}
	
	public String[] getPlayerGroups(Player player) {
		
		if (player == null)
			throw new NullArgumentException("player");
		
		// Use the cached values if possible
		if (chat != null)
			return groupCache.apply(player);
		else
			return empty;
	}

	public Chat getChat() {
		return chat;
	}
}
