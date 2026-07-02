package us.alleypvp.practice.feature.layout.menu.button;

import us.alleypvp.practice.library.menu.Button;
import us.alleypvp.practice.feature.kit.KitCategory;
import us.alleypvp.practice.feature.layout.menu.LayoutMenu;
import us.alleypvp.practice.common.item.ItemBuilder;
import us.alleypvp.practice.common.text.CC;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class LayoutModeSwitcherButton extends Button {
    private KitCategory kitCategory;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.ARROW)
                .name("&b&l" + this.kitCategory.getName() + " Modes")
                .lore(
                        CC.MENU_BAR,
                        ("&f " + this.kitCategory.getDescription()),
                        "",
                        "&aClick to view!",
                        CC.MENU_BAR
                )
                .hideMeta()
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        if (clickType != ClickType.LEFT) return;

        if (this.kitCategory == KitCategory.EXTRA) {
            new LayoutMenu(KitCategory.EXTRA).openMenu(player);
            return;
        }

        new LayoutMenu(KitCategory.NORMAL).openMenu(player);
    }
}