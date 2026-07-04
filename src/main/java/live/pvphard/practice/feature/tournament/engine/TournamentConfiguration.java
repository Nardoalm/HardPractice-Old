package live.pvphard.practice.feature.tournament.engine;

import live.pvphard.practice.feature.kit.Kit;
import lombok.Builder;
import lombok.Getter;
import org.bukkit.entity.Player;

/**
 * @author Remi
 * @project hard-practice
 * @date 8/08/2025
 */
@Getter
@Builder
public class TournamentConfiguration {
    private final int numericId;
    private final Player host;
    private final Kit kit;
    private final String displayName;
    private final int teamSize;
    private final int maxTeams;
    private final int minTeams;
    private final boolean isAdminHosted;
}