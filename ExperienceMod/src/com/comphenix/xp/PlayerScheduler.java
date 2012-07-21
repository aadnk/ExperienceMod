package com.comphenix.xp;

import java.util.Set;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.comphenix.xp.listeners.PlayerCleanupListener;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

/**
 * Schedules actions for different players.
 * 
 * @author Kristian
 */
public class PlayerScheduler implements PlayerCleanupListener {

	private SetMultimap<String, Integer> tasks = HashMultimap.create();

	private int defaultTicks;
	private BukkitScheduler scheduler;
	private Plugin plugin;

	public PlayerScheduler(BukkitScheduler scheduler, Plugin plugin) {
		this.scheduler = scheduler;
		this.plugin = plugin;
	}
	
	/**
	 * Schedules a task for execution on behalf of a player.
	 * @param player - player to execute on behalf of.
	 */
	public void schedule(Player player, Runnable runnable) {

		if (runnable == null)
			throw new NullArgumentException("runnable");
		if (player == null)
			throw new NullArgumentException("player");
		
		PlayerRunnable playerTask = new PlayerRunnable(player.getName(), runnable);
		Integer taskID = scheduler.scheduleSyncDelayedTask(plugin, playerTask, defaultTicks);
		
		// Now, update the task ID
		playerTask.setTaskID(taskID);
		tasks.put(player.getName(), taskID);
	}
	
	public Set<Integer> getTasks(Player player) {
		return tasks.get(player.getName());
	}

	@Override
	public void removePlayerCache(Player player) {
		
		// Stop all associated tasks
		for (int id : getTasks(player)) {
			scheduler.cancelTask(id);
		}
		
		// Then remove them
		tasks.removeAll(player.getName());
	}
	
	public BukkitScheduler getScheduler() {
		return scheduler;
	}

	public Plugin getPlugin() {
		return plugin;
	}
	
	/**
	 * Retrieves the current default number of ticks (50 ms per tick) until a task is executed.
	 * @return Number of ticks until a task is executed.
	 */
	public int getDefaultTicks() {
		return defaultTicks;
	}

	/**
	 * Sets the current default number of server ticks (50 ms per tick) until a task is executed.
	 * @param defaultTicks
	 */
	public void setDefaultTicks(int defaultTicks) {
		this.defaultTicks = defaultTicks;
	}
	
	// Our runnable that cleans up after itself
	private class PlayerRunnable implements Runnable {

		// Name and task ID
		private String name;
		private int taskID;
		
		// The task to execute
		private Runnable task;
		
		public PlayerRunnable(String name, Runnable task) {
			this.name = name;
			this.task = task;
		}
		
		@Override
		public void run() {
			task.run();
			
			// Clean up after ourself
			tasks.remove(name, taskID);
		}

		public void setTaskID(int taskID) {
			this.taskID = taskID;
		}
	}
}
