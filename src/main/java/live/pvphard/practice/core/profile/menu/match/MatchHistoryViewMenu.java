package live.pvphard.practice.core.profile.menu.match;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.library.menu.Button;
import live.pvphard.practice.library.menu.impl.BackButton;
import live.pvphard.practice.library.menu.pagination.PaginatedMenu;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.feature.match.data.MatchData;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.menu.match.button.MatchHistoryViewButton;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class MatchHistoryViewMenu extends PaginatedMenu {
    protected final Kit kit;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "&b&lHistórico de Partidas - " + this.kit.getName();
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        this.addGlassHeader(buttons, 15);
        buttons.put(4, new BackButton(new MatchHistorySelectKitMenu()));

        return buttons;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        Profile profile = HardPractice.getInstance().getService(ProfileService.class).getProfile(player.getUniqueId());

        List<MatchData> matchDataList = profile.getProfileData().getPreviousMatches().stream()
                .sorted((m1, m2) -> Long.compare(m2.getCreationTime(), m1.getCreationTime()))
                .collect(Collectors.toList());

        List<MatchData> filteredMatches = matchDataList.stream()
                .filter(matchData -> matchData.getKit().equals(this.kit.getName()))
                .collect(Collectors.toList());

        filteredMatches.forEach(
                matchData -> buttons.put(buttons.size(), new MatchHistoryViewButton(matchData))
        );

        return buttons;
    }
}
