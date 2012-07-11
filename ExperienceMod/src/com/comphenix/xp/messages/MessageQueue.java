package com.comphenix.xp.messages;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.bukkit.entity.Player;

import com.comphenix.xp.Action;

public class MessageQueue {
	
	// Map of every message to send in the future
	private Map<Action, MessageFormatter> lookup = new HashMap<Action, MessageFormatter>();
	private Queue<Action> ordered = new LinkedList<Action>();
	
	private long messageDelay;
	private long lastMessageTime;
	
	private Player player;
	private ChannelProvider channelProvider;

	public MessageQueue(long messageDelay, Player player, ChannelProvider channelProvider) {
		this.player = player;
		this.channelProvider = channelProvider;
		this.messageDelay = messageDelay;
		this.lastMessageTime = 0;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public boolean hasPlayer() {
		return player != null;
	}
	
	public void enqueue(Action action, MessageFormatter formatter) {
		
		// Special case
		if (messageDelay == 0) {
			transmitt(action, formatter);
			return;
		}
		
		// Enqueue the message
		if (lookup.containsKey(action)) {
			lookup.put(action, MessageFormatter.add(lookup.get(action), formatter));
			ordered.add(action);
		} else {
			lookup.put(action, formatter);
		}
	}
	
	public void transmitt(Action action, MessageFormatter formatter) {
		// Send as player or as a general message
		if (hasPlayer())
			action.emoteMessages(channelProvider, formatter, player);
		else
			action.announceMessages(channelProvider, formatter);
	}
	
	/**
	 * Gets the current message delay in milliseconds.
	 * @return Message delay in milliseconds.
	 */
	public long getMessageDelay() {
		return messageDelay;
	}

	/**
	 * Sets the message delay in milliseconds.
	 * @param messageDelay The new message delay in milliseconds.
	 */
	public void setMessageDelay(long messageDelay) {
		this.messageDelay = messageDelay;
	}

	/**
	 * Whether or not a message can be sent by this player.
	 * @return TRUE if it can, FALSE if not.
	 */
	public boolean isReady() {
		return (System.currentTimeMillis() - lastMessageTime) >= messageDelay;
	}
	
	public ChannelProvider getChannelProvider() {
		return channelProvider;
	}

	public void setChannelProvider(ChannelProvider channelProvider) {
		this.channelProvider = channelProvider;
	}
	
	/**
	 * Performs message transmissions, if it's ready.
	 */
	public void onTick() {

		// See if we have any messages to transmit
		if (ordered.size() > 0 && isReady()) {
		
			// Always choose the oldest message/composite message
			Action oldest = ordered.poll();
			
			// Transmit and clean up
			transmitt(oldest, lookup.get(oldest));
			lookup.remove(oldest);
			lastMessageTime = System.currentTimeMillis();
		}
	}
}
