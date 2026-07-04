package live.pvphard.practice.feature.leaderboard.menu.button;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.library.menu.Button;
import live.pvphard.practice.feature.leaderboard.LeaderboardType;
import live.pvphard.practice.feature.leaderboard.menu.LeaderboardMenu;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.common.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Remi
 * @project Hard
 * @date 5/26/2024
 */
public class DisplayTypeButton extends Button {
    protected final HardPractice plugin = HardPractice.getInstance();

    /**
     * Gets the item to display in the menu.
     *
     * @param player the player viewing the menu
     * @return the item to display
     */
    @Override
    public ItemStack getButtonItem(Player player) {
        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());
        LeaderboardType currentType = profile.getLeaderboardType();

        List<String> lore = new ArrayList<>();
        for (LeaderboardType type : LeaderboardType.values()) {
            lore.add((currentType == type ? "&f● &b" : "&f● &7") + type.getName());
        }
        lore.add("");
        lore.add("&aClick to toggle type of exhibition");

        return new ItemBuilder(Material.EYE_OF_ENDER)
                .name("&b&lType of exhibition")
                .lore(lore)
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
        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());

        LeaderboardType currentType = profile.getLeaderboardType();
        LeaderboardType[] types = LeaderboardType.values();
        int currentIndex = currentType.ordinal();

        switch (clickType) {
            case LEFT:
                currentIndex = (currentIndex + 1) % types.length;
                break;
            case RIGHT:
                currentIndex = (currentIndex - 1 + types.length) % types.length;
                break;
        }

        LeaderboardType newType = types[currentIndex];
        profile.setLeaderboardType(newType);
        new LeaderboardMenu().openMenu(player);
        this.playNeutral(player);
    }
}
