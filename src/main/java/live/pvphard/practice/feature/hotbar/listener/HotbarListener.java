package live.pvphard.practice.feature.hotbar.listener;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.library.menu.Menu;
import live.pvphard.practice.feature.hotbar.HotbarItem;
import live.pvphard.practice.feature.hotbar.HotbarService;
import live.pvphard.practice.feature.hotbar.HotbarAction;
import live.pvphard.practice.feature.hotbar.HotbarType;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.Profile;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class HotbarListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack clickedItem = player.getItemInHand();

        if (clickedItem == null || !clickedItem.hasItemMeta() || !clickedItem.getItemMeta().hasDisplayName()) {
            return;
        }

        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        HotbarService hotbarService = HardPractice.getInstance().getService(HotbarService.class);

        Profile profile = profileService.getProfile(player.getUniqueId());
        HotbarType currentHotbarType = hotbarService.getCorrespondingType(profile);
        if (currentHotbarType == null) return;

        HotbarItem hotbarItem = hotbarService.getHotbarItem(clickedItem, currentHotbarType);
        if (hotbarItem == null) {
            return;
        }

        if (hotbarItem.getName().equalsIgnoreCase("RAINBOW_BLOCKS")) {
            return;
        }

        if (hotbarItem.getActionData() == null) {
            return;
        }

        HotbarAction actionType = hotbarItem.getActionData().getAction();
        if (actionType == null) {
            return;
        }

        switch (actionType) {
            case RUN_COMMAND:
                String cmd = hotbarItem.getActionData().getCommand();
                if (cmd != null && !cmd.isEmpty()) {
                    player.performCommand(cmd);
                }
                break;
            case OPEN_MENU:
                String menuName = hotbarItem.getActionData().getMenuName();
                if (menuName != null && !menuName.isEmpty()) {
                    Menu menu = hotbarService.getMenuInstanceFromName(menuName, player);
                    if (menu != null) {
                        menu.openMenu(player);
                    }
                }
                break;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Player)) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();

        if (item != null && item.getType() == Material.BLAZE_ROD) {
            Player clicked = (Player) event.getRightClicked();
            player.performCommand("duel " + clicked.getName());
            event.setCancelled(true);
        }
    }
}