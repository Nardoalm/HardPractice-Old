package us.alleypvp.practice.feature.spawn.listener;

import us.alleypvp.practice.AlleyPractice;
import us.alleypvp.practice.core.profile.ProfileService;
import us.alleypvp.practice.core.profile.Profile;
import us.alleypvp.practice.core.profile.enums.ProfileState;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class SpawnListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ProfileService profileService = AlleyPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());

        if (profile.getState().equals(ProfileState.LOBBY) || profile.getState().equals(ProfileState.EDITING) || profile.getState().equals(ProfileState.WAITING)) {
            if (player.getGameMode() == GameMode.CREATIVE || player.hasPermission("kaoscore.admin")) {
                return;
            }

            ItemStack itemInHand = player.getItemInHand();
            boolean isWoolBlock = event.getBlockPlaced().getType() == Material.WOOL;
            boolean isWoolItem = itemInHand != null && itemInHand.getType() == Material.WOOL;

            if ((isWoolBlock || isWoolItem) && player.hasPermission("kaoscore.pro")) {
                event.setCancelled(false);
                return;
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ProfileService profileService = AlleyPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());

        if (player.getGameMode() == GameMode.SURVIVAL
                && (profile.getState().equals(ProfileState.LOBBY)
                || profile.getState().equals(ProfileState.EDITING)
                || profile.getState().equals(ProfileState.WAITING))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemPickUp(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        ProfileService profileService = AlleyPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());

        if (player.getGameMode() == GameMode.SURVIVAL
                && (profile.getState().equals(ProfileState.LOBBY)
                || profile.getState().equals(ProfileState.EDITING)
                || profile.getState().equals(ProfileState.WAITING))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ProfileService profileService = AlleyPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());

        if (profile.getState().equals(ProfileState.LOBBY) || profile.getState().equals(ProfileState.EDITING) || profile.getState().equals(ProfileState.WAITING)) {
            if (player.getGameMode() == GameMode.CREATIVE || player.hasPermission("kaoscore.admin")) {
                return;
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onHunger(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            ProfileService profileService = AlleyPractice.getInstance().getService(ProfileService.class);
            Profile profile = profileService.getProfile(player.getUniqueId());

            if (profile.getState().equals(ProfileState.LOBBY)
                    || profile.getState().equals(ProfileState.EDITING)
                    || profile.getState().equals(ProfileState.WAITING)
                    || profile.getState().equals(ProfileState.SPECTATING)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onMoveItem(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            ProfileService profileService = AlleyPractice.getInstance().getService(ProfileService.class);
            Profile profile = profileService.getProfile(player.getUniqueId());

            if (profile.getState() == ProfileState.EDITING) {
                return;
            }

            if (player.getGameMode() == GameMode.SURVIVAL
                    && (profile.getState().equals(ProfileState.LOBBY)
                    || profile.getState().equals(ProfileState.WAITING))) {

                if (event.getClickedInventory() != null && event.getClickedInventory().equals(player.getInventory())) {
                    event.setCancelled(true);
                    return;
                }

                if (event.getSlotType() == InventoryType.SlotType.CRAFTING || event.isShiftClick()) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    private void onHangingPlace(HangingPlaceEvent event) {
        Player player = event.getPlayer();
        ProfileService profileService = AlleyPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());

        if (profile.getState().equals(ProfileState.LOBBY) || profile.getState().equals(ProfileState.EDITING) || profile.getState().equals(ProfileState.WAITING)) {
            if (player.getGameMode() == GameMode.CREATIVE || player.hasPermission("kaoscore.admin")) {
                return;
            }
            event.setCancelled(true);
        }
    }
}