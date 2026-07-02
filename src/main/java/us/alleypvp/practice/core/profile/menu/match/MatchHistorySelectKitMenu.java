package us.alleypvp.practice.core.profile.menu.match;

import us.alleypvp.practice.AlleyPractice;
import us.alleypvp.practice.library.menu.Button;
import us.alleypvp.practice.library.menu.Menu;
import us.alleypvp.practice.feature.kit.KitService;
import us.alleypvp.practice.feature.kit.Kit;
import us.alleypvp.practice.feature.match.data.MatchData;
import us.alleypvp.practice.core.profile.ProfileService;
import us.alleypvp.practice.core.profile.Profile;
import us.alleypvp.practice.core.profile.menu.match.button.MatchHistorySelectKitButton;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Emmy
 * @project Alley
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

        Profile profile = AlleyPractice.getInstance().getService(ProfileService.class).getProfile(player.getUniqueId());

        List<MatchData> matchDataList = profile.getProfileData().getPreviousMatches();

        List<Kit> kits = this.plugin.getService(KitService.class).getKits();
        List<Kit> matchedKitsWithData = kits.stream()
                .filter(kit -> matchDataList.stream().anyMatch(matchData -> matchData.getKit().equals(kit.getName())))
                .collect(Collectors.toList());

        matchedKitsWithData.forEach(kit -> buttons.put(buttons.size(), new MatchHistorySelectKitButton(kit)));

        return buttons;
    }
}
