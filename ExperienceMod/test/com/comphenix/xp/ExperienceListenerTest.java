package com.comphenix.xp;

import static org.junit.Assert.*;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;

public class ExperienceListenerTest {

	@Test
	public void testCraftingCount() {
	
		ExperienceListener listener = new ExperienceListener(null, new MockDebugger(), null);
		
		ItemStack store = null;
		ItemStack crafted = new ItemStack(Material.IRON_INGOT, 2);
		
		int count = listener.getStorageCount(store, crafted, true);
		
		assertEquals(2, count);
	}

}
