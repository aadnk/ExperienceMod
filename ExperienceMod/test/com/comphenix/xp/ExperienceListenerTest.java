package com.comphenix.xp;

import static org.junit.Assert.*;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;

import com.comphenix.xp.listeners.ExperienceItemListener;

public class ExperienceListenerTest {

	@Test
	public void testCraftingCount() {
	
		ExperienceItemListener listener = new ExperienceItemListener(new MockDebugger(), null, null, null);
		
		ItemStack store = null;
		ItemStack crafted = new ItemStack(Material.IRON_INGOT, 2);
		
		int count = listener.getStorageCount(store, crafted, true);
		
		assertEquals(2, count);
	}
}
