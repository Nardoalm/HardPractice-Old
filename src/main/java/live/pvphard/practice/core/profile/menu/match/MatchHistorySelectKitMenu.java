package live.pvphard.practice.core.profile.menu.match;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.library.menu.Button;
import live.pvphard.practice.library.menu.Menu;
import live.pvphard.practice.feature.kit.KitService;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.feature.match.data.MatchData;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.menu.match.button.MatchHistorySelectKitButton;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Emmy
 * @project Hard
 * @since 04/06/2025
 */
public class MatchHistorySelectKitMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "&b&lMatch History";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        Profile profile = HardPractice.getInstance().getService(ProfileService.class).getProfile(player.getUniqueId());

        List<MatchData> matchDataList = profile.getProfileData().getPreviousMatches();

        List<Kit> kits = this.plugin.getService(KitService.class).getKits();
        List<Kit> matchedKitsWithData = kits.stream()
                .filter(kit -> matchDataList.stream().anyMatch(matchData -> matchData.getKit().equals(kit.getName())))
                .collect(Collectors.toList());

        matchedKitsWithData.forEach(kit -> buttons.put(buttons.size(), new MatchHistorySelectKitButton(kit)));

        return buttons;
    }
}
