package com.comphenix.xp.messages;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import com.comphenix.xp.Action;

public class MessagePlayerQueue {
	
	private Map<Player, MessageQueue> queues = new HashMap<Player, MessageQueue>();
	
	private long messageDelay;
	private ChannelProvider channelProvider;

	public MessagePlayerQueue(long messageDelay, ChannelProvider channelProvider) {
		this.messageDelay = messageDelay;
		this.channelProvider = channelProvider;
	}

	public ChannelProvider getChannelProvider() {
		return channelProvider;
	}

	public void setChannelProvider(ChannelProvider channelProvider) {
		this.channelProvider = channelProvider;
	}
	
	/**
	 * Enqueues a message transmitted by a player (or environment, in which case player should be NULL), 
	 * ensuring that there aren't too many messages sent at once.
	 * @param player - a player (or NULL) that sent this message.
	 * @param action - the action that contains the sent message.
	 * @param formatter - a message formatter that contains all the parameters.
	 */
	public void enqueue(Player player, Action action, MessageFormatter formatter) {
		
		MessageQueue queue = queues.get(player);
		
		// Construct queue if it hasn't been already
		if (queue == null) {
			queue = new MessageQueue(messageDelay, player, channelProvider);
			queues.put(player, queue);
		}
		
		// Let the queue handle the rest
		queue.enqueue(action, formatter);
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
	 * Creates a shallow copy of this queue. Enqueued messages are not copied.
	 * @return A copy of the current queue.
	 */
	public MessagePlayerQueue createView() {
		return new MessagePlayerQueue(messageDelay, channelProvider);
	}
	
	public void onTick() {
		for (MessageQueue queue : queues.values()) 
			queue.onTick();
	}
}
