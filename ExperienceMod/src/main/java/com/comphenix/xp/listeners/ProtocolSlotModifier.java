package com.comphenix.xp.listeners;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.injector.GamePhase;
import com.comphenix.xp.Debugger;
import com.comphenix.xp.Presets;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;

import static com.comphenix.protocol.PacketType.Play.Server.*;

/**
 * Represents a slot modifier that uses ProtocolLib.
 * @author Kristian
 */
public class ProtocolSlotModifier extends AbstractSlotModifier {
	/**
	 * Represents a specific cost list modification.
	 * @author Kristian
	 */
	private static class CostListModification {
		public final int[] original;
		public final int[] modification;
		
		public CostListModification(int[] original, int[] modification) {
			Preconditions.checkNotNull(original, "original cannot be NULL");
			Preconditions.checkNotNull(modification, "modification cannot be NULL");
			this.original = original.clone();
			this.modification = modification.clone();
		}
	}
	
	/**
	 * Inventory type for enchanting tables.
	 */
    private static final int ENCHANTING_INVENTORY = 4;
	
	// Current modifications
	private final Map<String, CostListModification> modifications = Maps.newHashMap();

	// Type of the last opened window
	private final Map<String, Integer> openWindowType = Maps.newHashMap(); 
	
	// Packet listener
	private SlotListener slotListener;
	
	/**
	 * Construct a new slot modifier that uses ProtocolLib.
	 * @param plugin - the parent plugin (ExperienceMod)
	 * @param debugger - the current debugger.
	 * @param presets - configuration root.
	 */
	public ProtocolSlotModifier(Plugin plugin, Debugger debugger, Presets presets) {
		super(debugger, presets);
		slotListener = new SlotListener(plugin);
	}

	@Override
	public void onPreparedEnchanting(Player player) {
		// Ignore
	}

	@Override
	public void modifyCostList(Player player, int[] output, int[] modified) {
		// TODO Auto-generated method stub
		modifications.put(player.getName(), new CostListModification(output, modified));
	}
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onEnchantItemEvent(EnchantItemEvent event) {
		CostListModification modification = modifications.get(event.getEnchanter().getName());
		
		// Set the actual level it will cost
		if (modification != null) {
			int index = Ints.asList(modification.original).indexOf(event.getExpLevelCost());
		
			// Change cost
			if (index >= 0) {
				event.setExpLevelCost(modification.modification[index]);
			}
		}
	}
	
	private void handlePacket(PacketEvent event) {
		// Handle open window
		final String name = event.getPlayer().getName();
		
		if (event.getPacketType() == OPEN_WINDOW) {
			int type = event.getPacket().getIntegers().read(1);
			openWindowType.put(name, type);
		}
		
		// Handle progress
		if (event.getPacketType() == CRAFT_PROGRESS_BAR) {
			CostListModification modification = modifications.get(name);
			Integer lastType = openWindowType.get(name);
			
			if (lastType != null && lastType == ENCHANTING_INVENTORY && modification != null) {
				PacketContainer packet = event.getPacket();
				int property = packet.getIntegers().read(1);
				int value = packet.getIntegers().read(2);

				// Only modify expected values
				if (modification.original[property] == value) {
					packet.getIntegers().write(2, modification.modification[property]);
				} 
			}
		}
	}
	
	public void onPlayerQuit(PlayerQuitEvent e) {
		// Clean up
		modifications.remove(e.getPlayer().getName());
		openWindowType.remove(e.getPlayer().getName());
	}

	/**
	 * Register the current modifier.
	 */
	public void register() {
		ProtocolLibrary.getProtocolManager().addPacketListener(slotListener);
	}
	
	private class SlotListener implements PacketListener {
		/**
		 * Parent ExpreienceMod plugin.
		 */
		private final Plugin plugin;
		private final ListeningWhitelist sending;
		private final ListeningWhitelist recieving;
		
		public SlotListener(Plugin plugin) {
			this.plugin = plugin;
			this.sending = ListeningWhitelist.newBuilder().
				types(CRAFT_PROGRESS_BAR, OPEN_WINDOW).
				gamePhase(GamePhase.PLAYING).
				low().
				build();
			this.recieving = ListeningWhitelist.EMPTY_WHITELIST;
		}
		
		@Override
		public void onPacketSending(PacketEvent event) {
			handlePacket(event);
		}
		
		@Override
		public void onPacketReceiving(PacketEvent event) {
			// Ignore
		}
		
		@Override
		public ListeningWhitelist getSendingWhitelist() {
			return sending;
		}

		@Override
		public ListeningWhitelist getReceivingWhitelist() {
			return recieving;
		}

		@Override
		public Plugin getPlugin() {
			return plugin;
		}

	}
}
