package live.pvphard.practice.core.profile.menu.statistic.button;

import live.pvphard.practice.library.menu.Button;
import live.pvphard.practice.feature.division.menu.DivisionsMenu;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.common.item.ItemBuilder;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

/**
 * @author Emmy
 * @project Hard
 * @since 04/21/2025
 */
@AllArgsConstructor
public class DivisionViewButton extends Button {

    //TODO: when implementing global levels, profile field is gonna be required to get the level and so on...

    private final Profile profile;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.DIAMOND)
                .name("&b&lDivisions")
                .lore(
                        "&aClick to view your division progress."
                )
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        new DivisionsMenu().openMenu(player);
    }
}