package com.comphenix.xp.messages;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.help.HelpMap;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.map.MapView;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.scheduler.BukkitScheduler;

import com.avaje.ebean.config.ServerConfig;

public class MockServer implements Server {

	private int broadcastCount;
	
	@Override
	public int broadcast(String arg0, String arg1) {
		broadcastCount++;
		return 0;
	}

	@Override
	public int broadcastMessage(String arg0) {
		broadcastCount++;
		return 0;
	}
	
	public int getBroadcastCount() {
		return broadcastCount;
	}
	
	@Override
	public Set<String> getListeningPluginChannels() {
		return null;
	}

	@Override
	public void sendPluginMessage(Plugin arg0, String arg1, byte[] arg2) {
			
	}

	@Override
	public boolean addRecipe(Recipe arg0) {
		return false;
	}

	@Override
	public void banIP(String arg0) {
		
	}

	@Override
	public void clearRecipes() {

	}

	@Override
	public void configureDbConfig(ServerConfig arg0) {
		
	}

	@Override
	public Inventory createInventory(InventoryHolder arg0, InventoryType arg1) {
		
		return null;
	}

	@Override
	public Inventory createInventory(InventoryHolder arg0, int arg1) {
		
		return null;
	}

	@Override
	public Inventory createInventory(InventoryHolder arg0, int arg1, String arg2) {
		
		return null;
	}

	@Override
	public MapView createMap(World arg0) {
		
		return null;
	}

	@Override
	public World createWorld(WorldCreator arg0) {
		
		return null;
	}

	@Override
	public boolean dispatchCommand(CommandSender arg0, String arg1)
			throws CommandException {
		
		return false;
	}

	@Override
	public boolean getAllowEnd() {
		
		return false;
	}

	@Override
	public boolean getAllowFlight() {
		
		return false;
	}

	@Override
	public boolean getAllowNether() {
		
		return false;
	}

	@Override
	public int getAnimalSpawnLimit() {
		
		return 0;
	}

	@Override
	public Set<OfflinePlayer> getBannedPlayers() {
		
		return null;
	}

	@Override
	public String getBukkitVersion() {
		
		return null;
	}

	@Override
	public Map<String, String[]> getCommandAliases() {
		
		return null;
	}

	@Override
	public long getConnectionThrottle() {
		
		return 0;
	}

	@Override
	public ConsoleCommandSender getConsoleSender() {
		
		return null;
	}

	@Override
	public GameMode getDefaultGameMode() {
		
		return null;
	}

	@Override
	public boolean getGenerateStructures() {
		
		return false;
	}

	@Override
	public HelpMap getHelpMap() {
		
		return null;
	}

	@Override
	public Set<String> getIPBans() {
		
		return null;
	}

	@Override
	public String getIp() {
		
		return null;
	}

	@Override
	public Logger getLogger() {
		
		return null;
	}

	@Override
	public MapView getMap(short arg0) {
		return null;
	}

	@Override
	public int getMaxPlayers() {
		return 0;
	}

	@Override
	public Messenger getMessenger() {
		return null;
	}

	@Override
	public int getMonsterSpawnLimit() {
		return 0;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public OfflinePlayer getOfflinePlayer(String arg0) {
		return null;
	}

	@Override
	public OfflinePlayer[] getOfflinePlayers() {
		return null;
	}

	@Override
	public boolean getOnlineMode() {
		return false;
	}

	@Override
	public Player[] getOnlinePlayers() {
		return null;
	}

	@Override
	public Set<OfflinePlayer> getOperators() {
		return null;
	}

	@Override
	public Player getPlayer(String arg0) {
		return null;
	}

	@Override
	public Player getPlayerExact(String arg0) {
		return null;
	}

	@Override
	public PluginCommand getPluginCommand(String arg0) {
		return null;
	}

	@Override
	public PluginManager getPluginManager() {
		return null;
	}

	@Override
	public int getPort() {
		return 0;
	}

	@Override
	public List<Recipe> getRecipesFor(ItemStack arg0) {
		return null;
	}

	@Override
	public BukkitScheduler getScheduler() {
		return null;
	}

	@Override
	public String getServerId() {
		return null;
	}

	@Override
	public String getServerName() {
		return null;
	}

	@Override
	public ServicesManager getServicesManager() {
		return null;
	}

	@Override
	public int getSpawnRadius() {
		return 0;
	}

	@Override
	public int getTicksPerAnimalSpawns() {
		return 0;
	}

	@Override
	public int getTicksPerMonsterSpawns() {
		return 0;
	}

	@Override
	public String getUpdateFolder() {
		return null;
	}

	@Override
	public File getUpdateFolderFile() {
		return null;
	}

	@Override
	public String getVersion() {
		return "1.2.5-R4.0";
	}

	@Override
	public int getViewDistance() {
		return 0;
	}

	@Override
	public int getWaterAnimalSpawnLimit() {
		return 0;
	}

	@Override
	public Set<OfflinePlayer> getWhitelistedPlayers() {
		return null;
	}

	@Override
	public World getWorld(String arg0) {
		return null;
	}

	@Override
	public World getWorld(UUID arg0) {
		return null;
	}

	@Override
	public File getWorldContainer() {
		return null;
	}

	@Override
	public String getWorldType() {
		return null;
	}

	@Override
	public List<World> getWorlds() {
		return null;
	}

	@Override
	public boolean hasWhitelist() {
		return false;
	}

	@Override
	public List<Player> matchPlayer(String arg0) {
		return null;
	}

	@Override
	public Iterator<Recipe> recipeIterator() {
		return null;
	}

	@Override
	public void reload() {

	}

	@Override
	public void reloadWhitelist() {

	}

	@Override
	public void resetRecipes() {

	}

	@Override
	public void savePlayers() {

	}

	@Override
	public void setDefaultGameMode(GameMode arg0) {

	}

	@Override
	public void setSpawnRadius(int arg0) {

	}

	@Override
	public void setWhitelist(boolean arg0) {
		
	}

	@Override
	public void shutdown() {

	}

	@Override
	public void unbanIP(String arg0) {
		
	}

	@Override
	public boolean unloadWorld(String arg0, boolean arg1) {
		return false;
	}

	@Override
	public boolean unloadWorld(World arg0, boolean arg1) {
		return false;
	}

	@Override
	public boolean useExactLoginLocation() {
		return false;
	}

	@Override
	public String getMotd() {
		return null;
	}

	@Override
	public boolean isPrimaryThread() {
		return true;
	}

}
