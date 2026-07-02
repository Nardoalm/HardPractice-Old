package us.alleypvp.practice.feature.layout.menu.button.editor;

import us.alleypvp.practice.common.constants.MessageConstant;
import us.alleypvp.practice.common.item.ItemBuilder;
import us.alleypvp.practice.common.text.CC;
import us.alleypvp.practice.feature.layout.data.LayoutData;
import us.alleypvp.practice.library.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class LayoutRenameButton extends Button {
    private final LayoutData layout;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.NAME_TAG)
                .name("&b&lRename Layout")
                .lore(
                        CC.MENU_BAR,
                        "&7Changes the display",
                        "&7name of the layout.",
                        "",
                        "&aClick to rename",
                        CC.MENU_BAR
                )
                .hideMeta()
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        if (clickType != ClickType.LEFT) return;

        player.sendMessage(MessageConstant.IN_DEVELOPMENT);
    }
}