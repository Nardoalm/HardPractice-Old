package live.pvphard.practice.visual.scoreboard.internal.match.types.type;

import live.pvphard.practice.feature.match.internal.types.LivesMatch;
import live.pvphard.practice.feature.match.model.internal.MatchGamePlayer;
import live.pvphard.practice.feature.match.model.GameParticipant;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.visual.scoreboard.internal.match.BaseMatchScoreboard;
import live.pvphard.practice.visual.scoreboard.internal.match.annotation.ScoreboardData;
import org.bukkit.entity.Player;

/**
 * @author Remi
 * @project Hard
 * @since 26/06/2025
 */
@ScoreboardData(match = LivesMatch.class)
public class MatchScoreboardLives extends BaseMatchScoreboard {
    @Override
    protected String getSoloConfigPath() {
        return "scoreboard.lines.playing.solo.lives-match";
    }

    @Override
    protected String getTeamConfigPath() {
        return "scoreboard.lines.playing.team.lives-match";
    }

    @Override
    protected String replacePlaceholders(String line, Profile profile, Player player, GameParticipant<MatchGamePlayer> you, GameParticipant<MatchGamePlayer> opponent) {
        String baseLine = super.replacePlaceholders(line, profile, player, you, opponent);

        int yourTeamLives = you.getPlayers().stream()
                .mapToInt(p -> p.getData().getLives())
                .sum();

        int opponentTeamLives = opponent.getPlayers().stream()
                .mapToInt(p -> p.getData().getLives())
                .sum();

        return baseLine
                .replace("{player-lives}", String.valueOf(you.getLeader().getData().getLives()))
                .replace("{opponent-lives}", String.valueOf(opponent.getLeader().getData().getLives()))
                .replace("{your-team-lives}", String.valueOf(yourTeamLives))
                .replace("{opponent-team-lives}", String.valueOf(opponentTeamLives));
    }
}