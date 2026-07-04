package live.pvphard.practice.core.profile.menu.statistic.button;

import live.pvphard.practice.library.menu.Button;
import live.pvphard.practice.feature.leaderboard.menu.LeaderboardMenu;
import live.pvphard.practice.common.item.ItemBuilder;
import live.pvphard.practice.common.text.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

/**
 * @author Remi
 * @project Hard
 * @date 5/26/2024
 */
public class LeaderboardButton extends Button {

    /**
     * Gets the item to display in the menu.
     *
     * @param player the player viewing the menu
     * @return the item to display
     */
    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.EYE_OF_ENDER)
                .name("&b&lLeaderboards")
                .lore(
                        CC.MENU_BAR,
                        "&7All rankings are displayed here.",
                        "&7You can view the top wins, losses, and more.",
                        "",
                        "&aClick to view the leaderboards.",
                        CC.MENU_BAR
                )
                .build();
    }

    /**
     * Handles the click event for the button.
     *
     * @param player    the player who clicked the button
     * @param clickType the type of click
     */
    @Override
    public void clicked(Player player, ClickType clickType) {
        if (clickType == ClickType.MIDDLE || clickType == ClickType.RIGHT || clickType == ClickType.NUMBER_KEY || clickType == ClickType.DROP || clickType == ClickType.SHIFT_LEFT || clickType == ClickType.SHIFT_RIGHT) {
            return;
        }
        this.playNeutral(player);
        new LeaderboardMenu().openMenu(player);
    }
}