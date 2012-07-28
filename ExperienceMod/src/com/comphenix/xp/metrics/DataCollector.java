package com.comphenix.xp.metrics;

import java.io.IOException;

import org.apache.commons.lang.NullArgumentException;

import com.comphenix.xp.ExperienceMod;
import com.comphenix.xp.extra.Service;
import com.comphenix.xp.extra.ServiceProvider;
import com.comphenix.xp.history.HawkeyeService;
import com.comphenix.xp.history.LogBlockService;
import com.comphenix.xp.messages.HeroService;
import com.comphenix.xp.metrics.Metrics.Graph;
import com.comphenix.xp.rewards.RewardTypes;

public class DataCollector {

	private Metrics metrics;
	private ExperienceMod mod;
	
	public DataCollector(ExperienceMod mod) {
		
		if (mod == null)
			throw new NullArgumentException("mod");
		
		try {
			// Initialize metrics
			metrics = new Metrics(mod);
			addOptionalMods();
			addWarningCount();
			
			metrics.start();
			
		} catch (IOException e) {
			mod.printWarning(this, "Unable to load metrics: %s", e.getMessage());
		}
	}
	
	// Whether or not any of the optional mods are present
	protected void addOptionalMods() {
		
		Graph optionalMods = metrics.createGraph("Optional Mods Enabled");

		optionalMods.addPlotter(new Metrics.Plotter("LogBlock") {
			public int getValue() {
				return isServiceEnabled(mod.getHistoryProviders(), LogBlockService.NAME) ? 1 : 0;
			}
		});
		
		optionalMods.addPlotter(new Metrics.Plotter("HawkEye") {
			public int getValue() {
				return isServiceEnabled(mod.getHistoryProviders(), HawkeyeService.NAME) ? 1 : 0;
			}
		});
		
		optionalMods.addPlotter(new Metrics.Plotter("Vault") {
			public int getValue() {
				// Vault is enabled if we have an economy reward
				return isServiceEnabled(mod.getRewardProvider(), RewardTypes.ECONOMY.name()) ? 1 : 0;
			}
		});
		
		optionalMods.addPlotter(new Metrics.Plotter("HeroChat") {
			public int getValue() {
				return isServiceEnabled(mod.getChannelProvider(), HeroService.NAME) ? 1 : 0;
			}
		});
		
		// And, finally, ExperienceBridgeMod
		optionalMods.addPlotter(new Metrics.Plotter("ExperienceBridgeMod") {
			public int getValue() {
				return mod.getServer().getPluginManager().getPlugin("ExperienceBridgeMod") != null ? 1 : 0;
			}
		});
	}

	protected void addWarningCount() {
		
		Graph optionalMods = metrics.createGraph("Warnings Count");
		
		optionalMods.addPlotter(new Metrics.Plotter("Warnings") {
			public int getValue() {
				return mod.getInformer().messageCount();
			}
		});
	}
	
	/**
	 * Determines if a given service is present and enabled.
	 * @param provider - service provider.
	 * @param name - name of the service.
	 * @return TRUE if the service is present and enabled, FALSE otherwise
	 */
	protected <T extends Service> boolean isServiceEnabled(ServiceProvider<T> provider, String name) {
		return provider != null && provider.containsService(name) && provider.isEnabled(name);
	}
	
 	/**
	 * Gets the current instance of the Metrics service.
	 * @return Metrics service.
	 */
	public Metrics getMetrics() {
		return metrics;
	}

	public ExperienceMod getMod() {
		return mod;
	}
}
