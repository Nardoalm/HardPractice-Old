package us.alleypvp.practice.feature.layout.menu.button.editor;

import us.alleypvp.practice.library.menu.Button;
import us.alleypvp.practice.feature.layout.data.LayoutData;
import us.alleypvp.practice.common.item.ItemBuilder;
import us.alleypvp.practice.common.text.CC;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class LayoutDeleteButton extends Button {
    private final LayoutData layout;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.INK_SACK)
                .name("&c&lDelete Layout")
                .durability(1)
                .lore(
                        CC.MENU_BAR,
                        "&7Warning: permanent!",
                        "",
                        "&aClick to delete.",
                        CC.MENU_BAR
                )
                .hideMeta()
                .build();
    }
}