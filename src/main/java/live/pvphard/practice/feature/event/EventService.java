package live.pvphard.practice.feature.event;

import live.pvphard.practice.bootstrap.lifecycle.Service;
import live.pvphard.practice.feature.event.model.ActiveEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.Collection;
import java.util.UUID;

public interface EventService extends Service {
    Collection<EventDefinition> getEventDefinitions();

    EventDefinition getEventDefinition(String key);

    ActiveEvent getActiveEvent();

    boolean hostEvent(Player host, String key);

    boolean joinActiveEvent(Player player);

    boolean leaveActiveEvent(Player player, boolean silent);

    void stopActiveEvent(String reason);

    boolean canManageActiveEvent(Player player);

    boolean isEventParticipant(UUID uuid);

    boolean isCurrentFighter(UUID uuid);

    boolean isPreparingFighter(UUID uuid);

    Location getAssignedSpawn(UUID uuid);

    boolean canPlayersFight(Player attacker, Player victim);

    void handleDisconnect(Player player);

    void eliminate(Player player, String reason);

    void handleInteract(PlayerInteractEvent event);

    void handleMove(PlayerMoveEvent event);

    void handleDamage(EntityDamageEvent event);

    void handleDamageByEntity(EntityDamageByEntityEvent event);

    void handleDeath(PlayerDeathEvent event);

    void handleRespawn(PlayerRespawnEvent event);

    void handleInventoryClick(InventoryClickEvent event);

    void handleBlockBreak(BlockBreakEvent event);

    void handleBlockPlace(BlockPlaceEvent event);

    boolean isDropLocked(Player player);

    boolean isPickupLocked(Player player);

    boolean isItemDamageLocked(Player player);
}
