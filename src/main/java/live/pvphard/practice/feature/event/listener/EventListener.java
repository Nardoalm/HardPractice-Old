package live.pvphard.practice.feature.event.listener;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.enums.ProfileState;
import live.pvphard.practice.feature.event.EventService;
import live.pvphard.practice.feature.event.model.ActiveEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class EventListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.getEventService().handleDisconnect(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (this.isInEvent(event.getPlayer())) {
            this.getEventService().handleInteract(event);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        EventService eventService = this.getEventService();
        ActiveEvent activeEvent = eventService.getActiveEvent();

        if (activeEvent != null && activeEvent.getArena() != null && player.getWorld().equals(activeEvent.getArena().getCenter().getWorld())) {
            Material feet = player.getLocation().getBlock().getType();
            Material below = player.getLocation().clone().subtract(0.0D, 1.0D, 0.0D).getBlock().getType();
            boolean inWater = feet == Material.WATER || feet == Material.STATIONARY_WATER || below == Material.WATER || below == Material.STATIONARY_WATER;

            if (inWater) {
                if (activeEvent.isParticipant(player.getUniqueId()) && activeEvent.isCurrentFighter(player.getUniqueId())) {
                    eventService.handleMove(event);
                    return;
                }

                org.bukkit.Location waitingLoc = activeEvent.getDefinition().getLocation("waiting");
                if (waitingLoc == null && activeEvent.getArena() != null) {
                    waitingLoc = activeEvent.getArena().getCenter();
                }
                if (waitingLoc != null) {
                    player.teleport(waitingLoc);
                }
                return;
            }
        }

        if (this.isInEvent(player)) {
            eventService.handleMove(event);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && this.isInEvent((Player) event.getEntity())) {
            this.getEventService().handleDamage(event);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && this.isInEvent((Player) event.getEntity())) {
            this.getEventService().handleDamageByEntity(event);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (this.isInEvent(event.getEntity())) {
            this.getEventService().handleDeath(event);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (this.isInEvent(event.getPlayer())) {
            this.getEventService().handleRespawn(event);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player && this.isInEvent((Player) event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player && this.isInEvent((Player) event.getWhoClicked())) {
            this.getEventService().handleInventoryClick(event);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (this.isInEvent(event.getPlayer()) && this.getEventService().isDropLocked(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (this.isInEvent(event.getPlayer()) && this.getEventService().isPickupLocked(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (this.isInEvent(event.getPlayer())) {
            this.getEventService().handleBlockBreak(event);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (this.isInEvent(event.getPlayer())) {
            this.getEventService().handleBlockPlace(event);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemDamage(PlayerItemDamageEvent event) {
        if (this.isInEvent(event.getPlayer()) && this.getEventService().isItemDamageLocked(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    private boolean isInEvent(Player player) {
        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());
        return profile != null && profile.getState() == ProfileState.PLAYING_EVENT;
    }

    private EventService getEventService() {
        return HardPractice.getInstance().getService(EventService.class);
    }
}