package us.alleypvp.practice.core.profile.menu.match.button;

import us.alleypvp.practice.AlleyPractice;
import us.alleypvp.practice.library.menu.Button;
import us.alleypvp.practice.feature.kit.Kit;
import us.alleypvp.practice.feature.match.data.MatchData;
import us.alleypvp.practice.core.profile.ProfileService;
import us.alleypvp.practice.core.profile.Profile;
import us.alleypvp.practice.core.profile.menu.match.MatchHistoryViewMenu;
import us.alleypvp.practice.common.item.ItemBuilder;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * @author Emmy
 * @project Alley
 * @since 06/04/2025
 */
@AllArgsConstructor
public class MatchHistorySelectKitButton extends Button {
    protected final Kit kit;

    @Override
    public ItemStack getButtonItem(Player player) {
        Profile profile = AlleyPractice.getInstance().getService(ProfileService.class).getProfile(player.getUniqueId());

        List<MatchData> matchDataList = profile.getProfileData().getPreviousMatches();

        int count = (int) matchDataList.stream()
                .filter(matchData -> matchData.getKit().equals(this.kit.getName()))
                .count();

        int unrankedCount = (int) matchDataList.stream()
                .filter(matchData -> matchData.getKit().equals(this.kit.getName()) && !matchData.isRanked())
                .count();

        int rankedCount = count - unrankedCount;

        return new ItemBuilder(this.kit.getIcon())
                .name("&b&l" + this.kit.getDisplayName())
                .lore(
                        " &f● Total: &b" + count,
                        " &f● Ranked: &b" + rankedCount,
                        " &f● Unranked: &b" + unrankedCount,
                        "",
                        "&aClick to view!"
                )
                .durability(this.kit.getDurability())
                .hideMeta()
                .amount(count > 0 ? count : 1)
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        if (clickType != ClickType.LEFT) return;

        new MatchHistoryViewMenu(this.kit).openMenu(player);
    }
}