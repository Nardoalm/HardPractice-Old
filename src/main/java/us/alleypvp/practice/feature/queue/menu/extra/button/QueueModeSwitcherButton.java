package us.alleypvp.practice.feature.queue.menu.extra.button;

import us.alleypvp.practice.AlleyPractice;
import us.alleypvp.practice.library.menu.Button;
import us.alleypvp.practice.feature.kit.KitCategory;
import us.alleypvp.practice.feature.queue.QueueService;
import us.alleypvp.practice.feature.queue.QueueType;
import us.alleypvp.practice.feature.queue.menu.extra.ExtraModesMenu;
import us.alleypvp.practice.common.item.ItemBuilder;
import us.alleypvp.practice.common.text.CC;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

/**
 * @author Emmy
 * @project Alley
 * @since 01/05/2025
 */
@AllArgsConstructor
public class QueueModeSwitcherButton extends Button {
    private final QueueType queueType;
    private final KitCategory kitCategory;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.ARROW)
                .name("&b&lModos " + this.kitCategory.getName())
                .lore(
                        CC.MENU_BAR,
                        ("&f " + this.kitCategory.getDescription()),
                        "",
                        "&aClick to view.",
                        CC.MENU_BAR
                )
                .hideMeta()
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        if (clickType != ClickType.LEFT) return;

        if (this.kitCategory == KitCategory.EXTRA) {
            new ExtraModesMenu(this.queueType).openMenu(player);
            return;
        }

        AlleyPractice.getInstance().getService(QueueService.class).getQueueMenu().openMenu(player);
    }
}
