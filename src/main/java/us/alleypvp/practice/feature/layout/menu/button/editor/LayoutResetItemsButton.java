package us.alleypvp.practice.feature.layout.menu.button.editor;

import us.alleypvp.practice.common.InventoryUtil;
import us.alleypvp.practice.library.menu.Button;
import us.alleypvp.practice.feature.kit.Kit;
import us.alleypvp.practice.common.item.ItemBuilder;
import us.alleypvp.practice.common.text.CC;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class LayoutResetItemsButton extends Button {
    private final Kit kit;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.WOOL)
                .name("&b&lReset Items")
                .durability(4)
                .lore(
                        CC.MENU_BAR,
                        "&7Resets items to default.",
                        "",
                        "&aClick to reset.",
                        CC.MENU_BAR
                )
                .hideMeta()
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        if (clickType != ClickType.LEFT) return;
        if (this.kit == null) {
            player.sendMessage(CC.translate("&cCould not reset items for this layout."));
            return;
        }

        player.getInventory().clear();
        player.getInventory().setContents(InventoryUtil.getEditableKitItems(this.kit));
        player.updateInventory();
    }
}