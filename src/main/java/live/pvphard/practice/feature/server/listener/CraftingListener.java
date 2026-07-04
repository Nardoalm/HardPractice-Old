package live.pvphard.practice.feature.server.listener;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.feature.server.ServerService;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.enums.ProfileState;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author Emmy
 * @project hard-practice
 * @since 15/07/2025
 */
public class CraftingListener implements Listener {
    @EventHandler
    private void onPrepareItemCraft(PrepareItemCraftEvent event) {
        Player player = (Player) event.getView().getPlayer();
        if (player == null) {
            return;
        }

        Profile profile = HardPractice.getInstance().getService(ProfileService.class).getProfile(player.getUniqueId());
        if (profile.getState() != ProfileState.PLAYING && profile.getState() != ProfileState.FFA && profile.getState() != ProfileState.SPECTATING) {
            return;
        }

        ItemStack result = event.getInventory().getResult();
        if (result == null || result.getType() == Material.AIR) {
            return;
        }

        if (HardPractice.getInstance().getService(ServerService.class).getBlockedCraftingItems().contains(result.getType())) {
            event.getInventory().setResult(null);
        }
    }
}