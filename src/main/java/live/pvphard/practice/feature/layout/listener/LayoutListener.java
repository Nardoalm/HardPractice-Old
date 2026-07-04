package live.pvphard.practice.feature.layout.listener;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.common.InventoryUtil;
import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.feature.kit.KitService;
import live.pvphard.practice.feature.layout.data.LayoutData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * @author Emmy
 * @project Hard
 * @since 04/05/2025
 */
public class LayoutListener implements Listener {
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || item.getType() != Material.BOOK) return;
        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;

        String clickedName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
        if (clickedName == null || clickedName.trim().isEmpty()) {
            return;
        }

        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        if (profileService == null) {
            return;
        }
        Profile profile = profileService.getProfile(player.getUniqueId());
        if (profile == null
                || profile.getProfileData() == null
                || profile.getProfileData().getLayoutData() == null
                || profile.getProfileData().getLayoutData().getLayouts() == null) {
            return;
        }

        KitService kitService = HardPractice.getInstance().getService(KitService.class);

        for (java.util.Map.Entry<String, List<LayoutData>> entry : profile.getProfileData().getLayoutData().getLayouts().entrySet()) {
            List<LayoutData> layoutList = entry.getValue();
            if (layoutList == null || layoutList.isEmpty()) {
                continue;
            }

            for (LayoutData layout : layoutList) {
                if (layout == null || layout.getDisplayName() == null) {
                    continue;
                }

                String layoutName = ChatColor.stripColor(layout.getDisplayName());
                if (layoutName != null && layoutName.equalsIgnoreCase(clickedName)) {
                    if (InventoryUtil.hasAnyItem(layout.getItems())) {
                        player.getInventory().setContents(InventoryUtil.cloneItemStackArray(layout.getItems()));
                    } else {
                        Kit kit = kitService != null ? kitService.getKit(entry.getKey()) : null;
                        ItemStack[] fallback = InventoryUtil.getEditableKitItems(kit);
                        player.getInventory().setContents(InventoryUtil.cloneItemStackArray(fallback));
                    }
                    player.sendMessage(CC.translate("&aYou selected the layout &b" + layout.getDisplayName() + "&a."));
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }
}