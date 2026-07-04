package live.pvphard.practice.feature.leaderboard.menu;

import com.google.common.collect.Maps;
import live.pvphard.practice.HardPractice;
import live.pvphard.practice.library.menu.Button;
import live.pvphard.practice.library.menu.Menu;
import live.pvphard.practice.feature.kit.KitService;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.feature.kit.setting.types.mode.KitSettingRanked;
import live.pvphard.practice.feature.leaderboard.LeaderboardService;
import live.pvphard.practice.feature.leaderboard.data.LeaderboardPlayerData;
import live.pvphard.practice.feature.leaderboard.LeaderboardType;
import live.pvphard.practice.feature.leaderboard.menu.button.DisplayTypeButton;
import live.pvphard.practice.feature.leaderboard.menu.button.LeaderboardKitButton;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.menu.statistic.button.StatisticsButton;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class LeaderboardMenu extends Menu {
    protected final HardPractice plugin = HardPractice.getInstance();

    @Override
    public String getTitle(Player player) {
        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());

        switch (profile.getLeaderboardType()) {
            case RANKED:
                return "&b&lRanked Leaderboards";
            case UNRANKED:
                return "&b&lUnranked Leaderboards";
            case UNRANKED_MONTHLY:
                return "&b&lMonthly Leaderboards";
            case FFA:
                return "&b&lFFA Leaderboards";
            case TOURNAMENT:
                return "&b&lTournament Leaderboards";
            case WIN_STREAK:
                return "&b&lWinstreak Leaderboards";
            default:
                return "&b&lLeaderboards";
        }
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> buttons = Maps.newHashMap();
        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());
        LeaderboardType currentType = profile.getLeaderboardType();
        LeaderboardService leaderboardService = HardPractice.getInstance().getService(LeaderboardService.class);

        buttons.put(2, new StatisticsButton());
        buttons.put(6, new DisplayTypeButton());

        int slot = 10;

        for (Kit kit : HardPractice.getInstance().getService(KitService.class).getKits()) {
            if (!kit.isEnabled() || kit.getIcon() == null) continue;

            List<LeaderboardPlayerData> leaderboard = leaderboardService.getLeaderboardEntries(kit, currentType);

            switch (currentType) {
                case RANKED:
                    if (!kit.isSettingEnabled(KitSettingRanked.class)) {
                        break;
                    }
                case UNRANKED:
                case UNRANKED_MONTHLY:
                case TOURNAMENT:
                case WIN_STREAK:
                case FFA:
                    slot = this.skipIfSlotCrossingBorder(slot);
                    buttons.put(slot++, new LeaderboardKitButton(kit, leaderboard, currentType));
                    break;
            }
        }

        this.addBorder(buttons, 15, 5);

        return buttons;
    }

    @Override
    public int getSize() {
        return 5 * 9;
    }
}