package com.comphenix.xp.listeners;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.InventoryView;

import com.comphenix.xp.Configuration;
import com.comphenix.xp.Debugger;
import com.comphenix.xp.Presets;
import com.comphenix.xp.reflect.FieldUtils;
import com.comphenix.xp.reflect.MethodUtils;

/**
 * Alter the enchanting level in 
 * @author Kristian
 */
public class ReflectionSlotModifier extends AbstractSlotModifier {
	// If we encounter any problem at all with our reflection trickery, we'll disable the maximum
	// enchant level at once. This could happen if CraftBukkit changes, or we're installed on a server
	// that is Bukkit-compatible only.
	private boolean disableEnchantingTrickery;
	
	// Reflection helpers
	private Field costsField;
	private Method containerHandle;
	private Method entityMethod;
	private Method enchantItemMethod;
	
	// Used by item enchant to swallow events
	private Map<String, Integer> overrideEnchant = new HashMap<String, Integer>();	
	
	public ReflectionSlotModifier(Debugger debugger, Presets presets) {
		super(debugger, presets);
	}

	@Override
	public void onPreparedEnchanting(Player player) {
		// Just in case this hasn't already been done
		overrideEnchant.remove(player.getName());
	}
	
	@Override
	public void modifyCostList(Player player, int[] output, int[] modified) {
		// Modify the actual cost list
		System.arraycopy(modified, 0, output, 0, modified.length);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEnchantItemEvent(EnchantItemEvent event) {
		int maxEnchant = 0;
		
		try {
			InventoryView view = event.getView();
			Integer slot = event.whichButton();
			
			Player player = event.getEnchanter();
			String name = player.getName();
			
			// Prevent infinite recursion and revert the temporary cost change
			if (overrideEnchant.containsKey(name)) {
				event.setExpLevelCost(overrideEnchant.get(name));
				return;
				
			} else if (disableEnchantingTrickery) {
				// Prevent too many errors from occurring
				return;
			}
			
			Configuration config = getConfiguration(player);
			maxEnchant = config.getMaximumEnchantLevel();
	
			double reverseFactor = (double)Configuration.DEFAULT_MAXIMUM_ENCHANT_LEVEL / (double)maxEnchant;
			 
			// Don't do anything if we're at the default enchanting level
			if (maxEnchant == Configuration.DEFAULT_MAXIMUM_ENCHANT_LEVEL) {
				return;
			}

			// Read the container-field in CraftInventoryView
			if (containerHandle == null)
				containerHandle = MethodUtils.getAccessibleMethod(view.getClass(), "getHandle", null);
			Object result = containerHandle.invoke(view);
					
			// Container should be of type net.minecraft.server.ContainerEnchantTable
			if (result != null) {
				// Cancel the original event
				event.setCancelled(true);
				
				Class<? extends Object> containerEnchantTable = result.getClass();
				
				// Read the cost-table
				if (costsField == null)
					costsField = FieldUtils.getField(containerEnchantTable, "costs");
				Object cost = FieldUtils.readField(costsField, result);
				
				// Get the real Minecraft player entity
				if (entityMethod == null)
					entityMethod = MethodUtils.getAccessibleMethod(player.getClass(), "getHandle", null);
				
				Object entity = entityMethod.invoke(player);
				
				if (cost instanceof int[]) {
					int[] ref = (int[]) cost;
					int oldCost = ref[slot];
					
					// Change the cost at the last second
					ref[slot] = (int) (ref[slot] * reverseFactor);
					
					if (hasDebugger()) {
						debugger.printDebug(this, "Modified slot %s from %s to %s.", slot, oldCost, ref[slot]);
					}
					
					// We have to ignore the next enchant event
					overrideEnchant.put(name, oldCost);
					
					// Run the method again
					if (enchantItemMethod == null) {
						enchantItemMethod = getEnchantMethod(result, entity, "a");
					}
						
					// Attempt to call this method
					if (enchantItemMethod != null) {
						enchantItemMethod.invoke(result, entity, slot);
					} else {
						debugger.printWarning(this, "Unable to modify slot %s cost. Reflection failed.", slot);
					}
					
					// OK, it's over
					overrideEnchant.remove(name);
				}
			}
			
			// A bunch or problems could occur
		} catch (Exception e) {
			ErrorReporting.DEFAULT.reportError(debugger, this, e, event, maxEnchant);
			disableEnchantingTrickery = true;
		}
	}
	
	private Method getEnchantMethod(Object container, Object entity, String methodName) {
		Method guess = MethodUtils.getMatchingAccessibleMethod(
						container.getClass(), methodName, new Class[] { entity.getClass(), int.class });
		
		if (guess != null) {
			// Great, got it on the first try
			return guess;
		} else {
			if (hasDebugger()) {
				debugger.printDebug(this, "Using fallback method to detect correct Minecraft method.");
			}
				
			// Damn, something's wrong. The method name must have changed. Try again.
			methodName = lastMinecraftMethod();
			guess = MethodUtils.getMatchingAccessibleMethod(
						container.getClass(), methodName, new Class[] { entity.getClass(), int.class });
			
			if (guess != null)
				return guess;
			else
				debugger.printWarning(this, "Could not find method '%s' in ContainerEnchantTable.", methodName);
			return null;
		}
	}
	
	/**
	 * Determine the name of the last calling Minecraft method in the call stack.
	 * <p>
	 * A Minecraft method is any method in a class found in net.minecraft.* and below.
	 * @return The name of this method, or NULL if not found.
	 */
    private static String lastMinecraftMethod() {
        try {
            throw new Exception();
        } catch (Exception e) {
            // Determine who called us
            StackTraceElement[] elements = e.getStackTrace();
            
            for (StackTraceElement element : elements) {
            	if (element.getClassName().startsWith("net.minecraft")) {
            		return element.getMethodName();
            	}
            }
            
            // If none is found (very unlikely though)
            return null;
        }
    }
    
	private boolean hasDebugger() {
		return debugger != null && debugger.isDebugEnabled();
	}
}
