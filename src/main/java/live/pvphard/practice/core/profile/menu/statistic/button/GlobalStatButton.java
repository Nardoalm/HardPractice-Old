package live.pvphard.practice.core.profile.menu.statistic.button;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.adapter.core.CoreAdapter;
import live.pvphard.practice.library.menu.Button;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.data.types.ProfileFFAData;
import live.pvphard.practice.common.item.ItemBuilder;
import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.common.text.LevelBadgeUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class GlobalStatButton extends Button {
    private final OfflinePlayer target;

    @Override
    public ItemStack getButtonItem(Player player) {
        Profile profile = HardPractice.getInstance().getService(ProfileService.class).getProfile(target.getUniqueId());
        if (profile == null || profile.getProfileData() == null) {
            return new ItemBuilder(Material.BARRIER)
                    .name("&cData unavailable")
                    .lore("&7Could not load player data.")
                    .build();
        }

        CoreAdapter coreAdapter = HardPractice.getInstance().getService(CoreAdapter.class);

        int ffaKills = profile.getProfileData().getFfaData().values().stream()
                .mapToInt(ProfileFFAData::getKills)
                .sum();
        int ffaDeaths = profile.getProfileData().getFfaData().values().stream()
                .mapToInt(ProfileFFAData::getDeaths)
                .sum();

        Player targetPlayer = target.getPlayer();
        String levelDisplay = LevelBadgeUtil.getDisplayBadge(targetPlayer, profile.getProfileData().getExperience());
        String rankDisplay = "&7N/A";
        if (coreAdapter != null && coreAdapter.getCore() != null && targetPlayer != null) {
            rankDisplay = String.valueOf(coreAdapter.getCore().getRankColor(targetPlayer)) + coreAdapter.getCore().getRankName(targetPlayer);
        }

        return new ItemBuilder(Material.SKULL_ITEM)
                .setSkull(target.getName())
                .name("&b&l" + target.getName() + " &r&7┃ &fStatistics")
                .lore(
                        CC.MENU_BAR,
                        "&7Displaying global data.",
                        "",
                        "&b&lUnranked",
                        "&b│ &fWins: &b" + profile.getProfileData().getUnrankedWins(),
                        "&b│ &fLosses: &b" + profile.getProfileData().getUnrankedLosses(),
                        "",
                        "&b&lRanked",
                        "&b│ &fWins: &b" + profile.getProfileData().getRankedWins(),
                        "&b│ &fLosses: &b" + profile.getProfileData().getRankedLosses(),
                        "&b│ &fElo: &b" + profile.getProfileData().getElo(),
                        "",
                        "&b&lFFA",
                        "&b│ &fKills: &b" + ffaKills,
                        "&b│ &fDeaths: &b" + ffaDeaths,
                        "",
                        "&b&lAccount",
                        "&b│ &fRank: &b" + rankDisplay,
                        "&b│ &fCoins: &b$" + profile.getProfileData().getCoins(),
                        "&b│ &fLevel: &b" + levelDisplay,
                        CC.MENU_BAR

                )
                .build();
    }
}