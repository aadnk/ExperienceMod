package com.comphenix.xp;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.ObjectUtils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.comphenix.xp.listeners.PlayerCleanupListener;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;

/**
 * Schedules actions for different players.
 * 
 * @author Kristian
 */
public class PlayerScheduler implements PlayerCleanupListener {

	private SetMultimap<String, PlayerRunnable> tasks = Multimaps.synchronizedSetMultimap(createMap());

	private int defaultTicks;
	private BukkitScheduler scheduler;
	private Plugin plugin;

	public PlayerScheduler(BukkitScheduler scheduler, Plugin plugin) {
		this.scheduler = scheduler;
		this.plugin = plugin;
	}

	// Creates our multimap
	private static SetMultimap<String, PlayerRunnable> createMap() {
		return HashMultimap.create();
	}
	
	/**
	 * Schedules a task for execution on behalf of a player.
	 * @param player - player to execute on behalf of.
	 * @param tag - unique tag or task name.
	 */
	public void schedule(Player player, String tag, Runnable runnable) {

		if (runnable == null)
			throw new NullArgumentException("runnable");
		if (player == null)
			throw new NullArgumentException("player");
		if (tag == null)
			throw new NullArgumentException("tag");
		
		PlayerRunnable playerTask = new PlayerRunnable(player.getName(), tag, runnable);
		Integer taskID = scheduler.scheduleSyncDelayedTask(plugin, playerTask, defaultTicks);
		
		// Now, update the task ID
		playerTask.setTaskID(taskID);
		tasks.put(player.getName(), playerTask);
	}
	
	private Set<PlayerRunnable> getTasks(Player player) {
		return tasks.get(player.getName());
	}
	
	public Set<PlayerRunnable> getTasks(Player player, String tag) {
		
		Set<PlayerRunnable> copy = new HashSet<PlayerRunnable>();
		
		// Filter out runnables with different tags
		for (PlayerRunnable runnable : getTasks(player)) {
			if (ObjectUtils.equals(runnable.getTag(), tag)) {
				copy.add(runnable);
			}
		}
		return copy;
	}

	@Override
	public void removePlayerCache(Player player) {
		
		// Stop all associated tasks
		for (PlayerRunnable runnable : getTasks(player)) {
			scheduler.cancelTask(runnable.getTaskID());
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
	public class PlayerRunnable implements Runnable {

		// Name and task ID
		private String name;
		private String tag;
		private int taskID;
		
		// The task to execute
		private Runnable task;
		
		public PlayerRunnable(String name, String tag, Runnable task) {
			this.name = name;
			this.tag = tag;
			this.task = task;
		}
		
		@Override
		public void run() {
			task.run();
			
			// Clean up after ourself
			tasks.remove(name, this);
		}
		
		public String getName() {
			return name;
		}

		public Runnable getTask() {
			return task;
		}

		public String getTag() {
			return tag;
		}
		
		public int getTaskID() {
			return taskID;
		}

		public void setTaskID(int taskID) {
			this.taskID = taskID;
		}
	}
}
