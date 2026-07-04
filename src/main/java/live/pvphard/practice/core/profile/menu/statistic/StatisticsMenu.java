package live.pvphard.practice.core.profile.menu.statistic;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.library.menu.Button;
import live.pvphard.practice.library.menu.Menu;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.data.types.ProfileFFAData;
import live.pvphard.practice.core.profile.data.types.ProfileRankedKitData;
import live.pvphard.practice.core.profile.data.types.ProfileUnrankedKitData;
import live.pvphard.practice.core.profile.menu.statistic.button.GlobalStatButton;
import live.pvphard.practice.core.profile.menu.statistic.button.LeaderboardButton;
import live.pvphard.practice.core.profile.menu.statistic.button.StatisticsButton;
import live.pvphard.practice.feature.division.model.DivisionTier;
import live.pvphard.practice.common.item.ItemBuilder;
import live.pvphard.practice.common.text.CC;
import lombok.AllArgsConstructor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@AllArgsConstructor
public class StatisticsMenu extends Menu {
    private OfflinePlayer target;

    @Override
    public String getTitle(Player player) {
        return this.target == player ? "&b&lYour Statistics" : "&b&l" + this.target.getName() + "'s Statistics";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(2, new StatisticsButton());
        buttons.put(4, new GlobalStatButton(target));
        buttons.put(6, new LeaderboardButton());

        Profile profile = HardPractice.getInstance().getService(ProfileService.class).getProfile(this.target == player ? player.getUniqueId() : this.target.getUniqueId());
        List<Kit> sortedKits = profile.getSortedKits();

        int slot = 10;
        for (Kit kit : sortedKits) {
            buttons.put(slot++, new KitStatButton(this.target.getUniqueId(), kit));
            if (slot == 17 || slot == 26 || slot == 35 || slot == 44 || slot == 53) {
                slot += 2;
            }
        }

        this.addBorder(buttons, 15, 5);

        return buttons;
    }

    @Override
    public int getSize() {
        return 5 * 9;
    }

    @AllArgsConstructor
    private static class KitStatButton extends Button {
        private static final int DEFAULT_ELO = 1000;

        private final UUID targetUuid;
        private final Kit kit;

        @Override
        public ItemStack getButtonItem(Player player) {
            ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
            Profile targetProfile = profileService != null ? profileService.getProfile(this.targetUuid) : null;

            ProfileRankedKitData profileRankedKitData = null;
            ProfileUnrankedKitData profileUnrankedKitData = null;
            ProfileFFAData profileFFAData = null;

            if (targetProfile != null && targetProfile.getProfileData() != null) {
                if (targetProfile.getProfileData().getRankedKitData() != null) {
                    profileRankedKitData = targetProfile.getProfileData().getRankedKitData().get(this.kit.getName());
                }
                if (targetProfile.getProfileData().getUnrankedKitData() != null) {
                    profileUnrankedKitData = targetProfile.getProfileData().getUnrankedKitData().get(this.kit.getName());
                }
                if (targetProfile.getProfileData().getFfaData() != null) {
                    profileFFAData = targetProfile.getProfileData().getFfaData().get(this.kit.getName());
                }
            }

            int unrankedWins = profileUnrankedKitData != null ? profileUnrankedKitData.getWins() : 0;
            int rankedWins = profileRankedKitData != null ? profileRankedKitData.getWins() : 0;
            int rankedElo = profileRankedKitData != null ? profileRankedKitData.getElo() : DEFAULT_ELO;
            int currentWinStreak = profileUnrankedKitData != null ? profileUnrankedKitData.getWinstreak() : 0;
            int bestWinStreak = profileUnrankedKitData != null ? profileUnrankedKitData.getBestWinstreak() : 0;

            String divisionName = "Unranked";
            String tierName = "I";
            if (profileUnrankedKitData != null) {
                if (profileUnrankedKitData.getDivision() != null) {
                    if (profileUnrankedKitData.getDivision().getDisplayName() != null && !profileUnrankedKitData.getDivision().getDisplayName().isEmpty()) {
                        divisionName = profileUnrankedKitData.getDivision().getDisplayName();
                    } else if (profileUnrankedKitData.getDivision().getName() != null) {
                        divisionName = profileUnrankedKitData.getDivision().getName();
                    }
                }
                DivisionTier tier = profileUnrankedKitData.getTier();
                if (tier != null && tier.getName() != null) {
                    tierName = tier.getName();
                }
            }

            List<String> lore = new ArrayList<>(Arrays.asList(
                    CC.MENU_BAR,
                    "&b&lUnranked &b⭐" + divisionName + " " + tierName,
                    "&b│ &fWins: &b" + unrankedWins,
                    "",
                    "&b│ &fWin Streak: &b" + currentWinStreak,
                    "    &fBest: &b" + bestWinStreak
            ));

            if (targetProfile != null && targetProfile.hasParticipatedInRanked()) {
                lore.addAll(Arrays.asList(
                        "",
                        "&b&lRanked",
                        "&b│ &fWins: &b" + rankedWins,
                        "&b│ &fElo: &b" + rankedElo
                ));
            }

            if (targetProfile != null && targetProfile.hasParticipatedInTournament()) {
                lore.addAll(Arrays.asList(
                        "",
                        "&b&lTournament",
                        "&f● &bWins: &f" + "N/A",
                        "&f● &bLosses: &f" + "N/A"
                ));
            }

            if (this.kit.isFfaEnabled() && targetProfile != null && targetProfile.hasParticipatedInFFA()) {
                int kills = profileFFAData != null ? profileFFAData.getKills() : 0;
                int deaths = profileFFAData != null ? profileFFAData.getDeaths() : 0;
                String kd = profileFFAData != null ? profileFFAData.getKillDeathRatio() : "0.0x";
                lore.addAll(Arrays.asList(
                        "",
                        "&b&lFFA",
                        "&f● &bKills: &f" + kills + " &7(" + kd + ")",
                        "&f● &bDeaths: &f" + deaths
                ));
            }

            lore.add(CC.MENU_BAR);

            return new ItemBuilder(this.kit.getIcon())
                    .name("&b&l" + this.kit.getDisplayName())
                    .durability(this.kit.getDurability())
                    .lore(lore)
                    .hideMeta()
                    .build();
        }
    }
}