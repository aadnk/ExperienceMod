package com.comphenix.xp.messages;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Achievement;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Server;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.InventoryView.Property;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class MockPlayer implements Player {

	private int totalExperience;
	private boolean sprinting;
	private boolean sneaking;
	private boolean sleepingIgnored;
	private float saturation;
	private long playerTime;
	private String playerListName;
	private int level;
	private int foodLevel;
	private boolean flying;
	private float exp;
	private float exhaustion;
	private String displayName;
	private Location compassTarget;
	private Location bedSpawnLocation;
	private boolean allowFlight;
	private String rawMessage;
	private float flyspeed;
	private float walkSpeed;

	public long getPlayerTime() {
		return playerTime;
	}

	public void setPlayerTime(long playerTime) {
		this.playerTime = playerTime;
	}

	public String getRawMessage() {
		return rawMessage;
	}

	public void setRawMessage(String rawMessage) {
		this.rawMessage = rawMessage;
	}

	public int getTotalExperience() {
		return totalExperience;
	}

	public boolean isSprinting() {
		return sprinting;
	}

	public boolean isSneaking() {
		return sneaking;
	}

	public boolean isSleepingIgnored() {
		return sleepingIgnored;
	}

	public float getSaturation() {
		return saturation;
	}

	public String getPlayerListName() {
		return playerListName;
	}

	public int getLevel() {
		return level;
	}

	public int getFoodLevel() {
		return foodLevel;
	}

	public boolean isFlying() {
		return flying;
	}

	public float getExp() {
		return exp;
	}

	public float getExhaustion() {
		return exhaustion;
	}

	public String getDisplayName() {
		return displayName;
	}

	public Location getCompassTarget() {
		return compassTarget;
	}

	public Location getBedSpawnLocation() {
		return bedSpawnLocation;
	}

	public boolean isAllowFlight() {
		return allowFlight;
	}

	@Override
	public void sendMap(MapView arg0) {
	}

	@Override
	public void sendRawMessage(String arg0) {
		rawMessage = arg0;
	}

	@Override
	public void setAllowFlight(boolean arg0) {
		allowFlight = arg0;
	}

	@Override
	public void setBedSpawnLocation(Location arg0) {
		bedSpawnLocation = arg0;
	}

	@Override
	public void setCompassTarget(Location arg0) {
		compassTarget = arg0;
	}

	@Override
	public void setDisplayName(String arg0) {
		displayName = arg0;
	}

	@Override
	public void setExhaustion(float arg0) {
		exhaustion = arg0;
	}

	@Override
	public void setExp(float arg0) {
		exp = arg0;
	}

	@Override
	public void setFlying(boolean arg0) {
		flying = arg0;
	}

	@Override
	public void setFoodLevel(int arg0) {
		foodLevel = arg0;
	}

	@Override
	public void setLevel(int arg0) {
		level = arg0;
	}

	@Override
	public void setPlayerListName(String arg0) {
		playerListName = arg0;
	}

	@Override
	public void setPlayerTime(long arg0, boolean arg1) {
		playerTime = arg0;
	}

	@Override
	public void setSaturation(float arg0) {
		saturation = arg0;
	}

	@Override
	public void setSleepingIgnored(boolean arg0) {
		sleepingIgnored = arg0;
	}

	@Override
	public void setSneaking(boolean arg0) {
		sneaking = arg0;
	}

	@Override
	public void setSprinting(boolean arg0) {
		sprinting = arg0;
	}

	@Override
	public void setTotalExperience(int arg0) {
		totalExperience = arg0;
	}
	
	@Override
	public void closeInventory() {
	}

	@Override
	public GameMode getGameMode() {
		return null;
	}

	@Override
	public PlayerInventory getInventory() {
		return null;
	}

	@Override
	public ItemStack getItemInHand() {
		return null;
	}

	@Override
	public ItemStack getItemOnCursor() {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public InventoryView getOpenInventory() {
		return null;
	}

	@Override
	public int getSleepTicks() {
		return 0;
	}

	@Override
	public boolean isBlocking() {
		return false;
	}

	@Override
	public boolean isSleeping() {
		return false;
	}

	@Override
	public InventoryView openEnchanting(Location arg0, boolean arg1) {
		return null;
	}

	@Override
	public InventoryView openInventory(Inventory arg0) {
		return null;
	}

	@Override
	public void openInventory(InventoryView arg0) {

	}

	@Override
	public InventoryView openWorkbench(Location arg0, boolean arg1) {
		return null;
	}

	@Override
	public void setGameMode(GameMode arg0) {
		
		
	}

	@Override
	public void setItemInHand(ItemStack arg0) {
		
		
	}

	@Override
	public void setItemOnCursor(ItemStack arg0) {
		
		
	}

	@Override
	public boolean setWindowProperty(Property arg0, int arg1) {
		
		return false;
	}

	@Override
	public boolean addPotionEffect(PotionEffect arg0) {
		
		return false;
	}

	@Override
	public boolean addPotionEffect(PotionEffect arg0, boolean arg1) {
		
		return false;
	}

	@Override
	public boolean addPotionEffects(Collection<PotionEffect> arg0) {
		
		return false;
	}

	@Override
	public void damage(int arg0) {
		
		
	}

	@Override
	public void damage(int arg0, Entity arg1) {
		
		
	}

	@Override
	public Collection<PotionEffect> getActivePotionEffects() {
		
		return null;
	}

	@Override
	public double getEyeHeight() {
		
		return 0;
	}

	@Override
	public double getEyeHeight(boolean arg0) {
		
		return 0;
	}

	@Override
	public Location getEyeLocation() {
		
		return null;
	}

	@Override
	public int getHealth() {
		
		return 0;
	}

	@Override
	public Player getKiller() {
		
		return null;
	}

	@Override
	public int getLastDamage() {
		
		return 0;
	}

	@Override
	public List<Block> getLastTwoTargetBlocks(HashSet<Byte> arg0, int arg1) {
		
		return null;
	}

	@Override
	public List<Block> getLineOfSight(HashSet<Byte> arg0, int arg1) {
		
		return null;
	}

	@Override
	public int getMaxHealth() {
		
		return 0;
	}

	@Override
	public int getMaximumAir() {
		
		return 0;
	}

	@Override
	public int getMaximumNoDamageTicks() {
		
		return 0;
	}

	@Override
	public int getNoDamageTicks() {
		
		return 0;
	}

	@Override
	public int getRemainingAir() {
		
		return 0;
	}

	@Override
	public Block getTargetBlock(HashSet<Byte> arg0, int arg1) {
		
		return null;
	}

	@Override
	public boolean hasPotionEffect(PotionEffectType arg0) {
		
		return false;
	}

	@Override
	public <T extends Projectile> T launchProjectile(Class<? extends T> arg0) {
		
		return null;
	}

	@Override
	public void removePotionEffect(PotionEffectType arg0) {
		
		
	}

	@Override
	public void setHealth(int arg0) {
		
		
	}

	@Override
	public void setLastDamage(int arg0) {
		
		
	}

	@Override
	public void setMaximumAir(int arg0) {
		
		
	}

	@Override
	public void setMaximumNoDamageTicks(int arg0) {
		
		
	}

	@Override
	public void setNoDamageTicks(int arg0) {
		
		
	}

	@Override
	public void setRemainingAir(int arg0) {
		
		
	}

	@Override
	public Arrow shootArrow() {
		
		return null;
	}

	@Override
	public Egg throwEgg() {
		
		return null;
	}

	@Override
	public Snowball throwSnowball() {
		
		return null;
	}

	@Override
	public boolean eject() {
		
		return false;
	}

	@Override
	public int getEntityId() {
		
		return 0;
	}

	@Override
	public float getFallDistance() {
		
		return 0;
	}

	@Override
	public int getFireTicks() {
		
		return 0;
	}

	@Override
	public EntityDamageEvent getLastDamageCause() {
		
		return null;
	}

	@Override
	public Location getLocation() {
		
		return null;
	}

	@Override
	public int getMaxFireTicks() {
		
		return 0;
	}

	@Override
	public List<Entity> getNearbyEntities(double arg0, double arg1, double arg2) {
		
		return null;
	}

	@Override
	public Entity getPassenger() {
		
		return null;
	}

	@Override
	public Server getServer() {
		
		return null;
	}

	@Override
	public int getTicksLived() {
		
		return 0;
	}

	@Override
	public EntityType getType() {
		
		return null;
	}

	@Override
	public UUID getUniqueId() {
		
		return null;
	}

	@Override
	public Entity getVehicle() {
		
		return null;
	}

	@Override
	public Vector getVelocity() {
		
		return null;
	}

	@Override
	public World getWorld() {
		
		return null;
	}

	@Override
	public boolean isDead() {
		
		return false;
	}

	@Override
	public boolean isEmpty() {
		
		return false;
	}

	@Override
	public boolean isInsideVehicle() {
		
		return false;
	}

	@Override
	public boolean leaveVehicle() {
		
		return false;
	}

	@Override
	public void playEffect(EntityEffect arg0) {
		
		
	}

	@Override
	public void remove() {
		
		
	}

	@Override
	public void setFallDistance(float arg0) {
		
		
	}

	@Override
	public void setFireTicks(int arg0) {
		
		
	}

	@Override
	public void setLastDamageCause(EntityDamageEvent arg0) {
		
		
	}

	@Override
	public boolean setPassenger(Entity arg0) {
		
		return false;
	}

	@Override
	public void setTicksLived(int arg0) {
		
		
	}

	@Override
	public void setVelocity(Vector arg0) {
		
		
	}

	@Override
	public boolean teleport(Location arg0) {
		
		return false;
	}

	@Override
	public boolean teleport(Entity arg0) {
		
		return false;
	}

	@Override
	public boolean teleport(Location arg0, TeleportCause arg1) {
		
		return false;
	}

	@Override
	public boolean teleport(Entity arg0, TeleportCause arg1) {
		
		return false;
	}

	@Override
	public List<MetadataValue> getMetadata(String arg0) {
		
		return null;
	}

	@Override
	public boolean hasMetadata(String arg0) {
		
		return false;
	}

	@Override
	public void removeMetadata(String arg0, Plugin arg1) {
		
		
	}

	@Override
	public void setMetadata(String arg0, MetadataValue arg1) {
		
		
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0) {
		
		return null;
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0, int arg1) {
		
		return null;
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0, String arg1,
			boolean arg2) {
		
		return null;
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0, String arg1,
			boolean arg2, int arg3) {
		
		return null;
	}

	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		
		return null;
	}

	@Override
	public boolean hasPermission(String arg0) {
		
		return false;
	}

	@Override
	public boolean hasPermission(Permission arg0) {
		
		return false;
	}

	@Override
	public boolean isPermissionSet(String arg0) {
		
		return false;
	}

	@Override
	public boolean isPermissionSet(Permission arg0) {
		
		return false;
	}

	@Override
	public void recalculatePermissions() {
		
		
	}

	@Override
	public void removeAttachment(PermissionAttachment arg0) {
		
		
	}

	@Override
	public boolean isOp() {
		
		return false;
	}

	@Override
	public void setOp(boolean arg0) {
		
		
	}

	@Override
	public void abandonConversation(Conversation arg0) {
		
		
	}

	@Override
	public void abandonConversation(Conversation arg0,
			ConversationAbandonedEvent arg1) {
		
		
	}

	@Override
	public void acceptConversationInput(String arg0) {
		
		
	}

	@Override
	public boolean beginConversation(Conversation arg0) {
		
		return false;
	}

	@Override
	public boolean isConversing() {
		
		return false;
	}

	@Override
	public void sendMessage(String arg0) {
		
		
	}

	@Override
	public void sendMessage(String[] arg0) {
		
		
	}

	@Override
	public long getFirstPlayed() {
		
		return 0;
	}

	@Override
	public long getLastPlayed() {
		
		return 0;
	}

	@Override
	public Player getPlayer() {
		
		return null;
	}

	@Override
	public boolean hasPlayedBefore() {
		
		return false;
	}

	@Override
	public boolean isBanned() {
		
		return false;
	}

	@Override
	public boolean isOnline() {
		
		return false;
	}

	@Override
	public boolean isWhitelisted() {
		
		return false;
	}

	@Override
	public void setBanned(boolean arg0) {
		
		
	}

	@Override
	public void setWhitelisted(boolean arg0) {
		
		
	}

	@Override
	public Map<String, Object> serialize() {
		
		return null;
	}

	@Override
	public Set<String> getListeningPluginChannels() {
		
		return null;
	}

	@Override
	public void sendPluginMessage(Plugin arg0, String arg1, byte[] arg2) {
		
		
	}

	@Override
	public void awardAchievement(Achievement arg0) {
		
		
	}

	@Override
	public boolean canSee(Player arg0) {
		
		return false;
	}

	@Override
	public void chat(String arg0) {
		
		
	}
	
	@Override
	public void giveExp(int arg0) {
		
		
	}

	@Override
	public void hidePlayer(Player arg0) {
		
		
	}

	@Override
	public void incrementStatistic(Statistic arg0) {
		
		
	}

	@Override
	public void incrementStatistic(Statistic arg0, int arg1) {
		
		
	}

	@Override
	public void incrementStatistic(Statistic arg0, Material arg1) {
		
		
	}

	@Override
	public void incrementStatistic(Statistic arg0, Material arg1, int arg2) {
		
		
	}

	@Override
	public boolean isPlayerTimeRelative() {
		
		return false;
	}

	@Override
	public void kickPlayer(String arg0) {
	}

	@Override
	public boolean performCommand(String arg0) {
		return false;
	}

	@Override
	public void playEffect(Location arg0, Effect arg1, int arg2) {
	}

	@Override
	public <T> void playEffect(Location arg0, Effect arg1, T arg2) {
	}

	@Override
	public void playNote(Location arg0, byte arg1, byte arg2) {
	}

	@Override
	public void playNote(Location arg0, Instrument arg1, Note arg2) {
	}

	@Override
	public void resetPlayerTime() {
	}

	@Override
	public void saveData() {
	}

	@Override
	public void sendBlockChange(Location arg0, Material arg1, byte arg2) {
	}

	@Override
	public void sendBlockChange(Location arg0, int arg1, byte arg2) {
	}

	@Override
	public boolean sendChunkChange(Location arg0, int arg1, int arg2, int arg3,
			byte[] arg4) {
		
		return false;
	}

	@Override
	public InetSocketAddress getAddress() {
		
		return null;
	}

	@Override
	public void showPlayer(Player arg0) {
	}

	@Override
	public void updateInventory() {	
	}

	@Override
	public boolean getAllowFlight() {
		return false;
	}

	@Override
	public long getPlayerTimeOffset() {
		return 0;
	}

	@Override
	public void loadData() {
	}

	@Override
	public int getExpToLevel() {
		return 0;
	}

	@Override
	public boolean hasLineOfSight(Entity arg0) {
		return false;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public float getFlySpeed() {
		return flyspeed;
	}

	@Override
	public float getWalkSpeed() {
		return walkSpeed;
	}

	@Override
	public void setFlySpeed(float flyspeed) throws IllegalArgumentException {
		this.flyspeed = flyspeed;
	}

	@Override
	public void setWalkSpeed(float walkSpeed) throws IllegalArgumentException {
		this.walkSpeed = walkSpeed;
	}
}
