package live.pvphard.practice.visual.scoreboard.internal.match.types.state;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.common.PlayerDisplayUtil;
import live.pvphard.practice.core.config.ConfigService;
import live.pvphard.practice.feature.match.Match;
import live.pvphard.practice.feature.match.model.internal.MatchGamePlayer;
import live.pvphard.practice.feature.match.model.GameParticipant;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.visual.scoreboard.internal.match.MatchScoreboard;
import live.pvphard.practice.common.text.CC;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Emmy
 * @project Hard
 * @since 30/04/2025
 */
public class MatchScoreboardEndingImpl implements MatchScoreboard {
    @Override
    public List<String> getLines(Profile profile, Player player, GameParticipant<MatchGamePlayer> you, GameParticipant<MatchGamePlayer> opponent) {
        if (profile == null || profile.getProfileData() == null) {
            return Collections.emptyList();
        }
        if (player == null || !player.isOnline()) {
            return Collections.emptyList();
        }

        ConfigService configService = HardPractice.getInstance().getService(ConfigService.class);
        if (configService == null || configService.getScoreboardConfig() == null || opponent == null || opponent.getLeader() == null || you == null || you.getLeader() == null) {
            return Collections.emptyList();
        }

        List<String> scoreboardLines = new ArrayList<>();
        Match match = profile.getMatch();
        if (match == null) return scoreboardLines;

        String opponentName = PlayerDisplayUtil.resolveCurrentNick(
                opponent.getLeader().getTeamPlayer(),
                opponent.getLeader().getUsername()
        );
        String winnerName = opponent.getLeader().isDead()
                ? PlayerDisplayUtil.resolveCurrentNick(you.getLeader().getTeamPlayer(), you.getLeader().getUsername())
                : opponentName;

        for (String line : configService.getScoreboardConfig().getStringList("scoreboard.lines.ending")) {
            scoreboardLines.add(CC.translate(line)
                    .replace("{opponent}", opponentName)
                    .replace("{duration}", match.getDuration())
                    .replace("{winner}", winnerName)
                    .replace("{end-result}", opponent.getLeader().isDead() ? "&a&lVICTORY!" : "&c&lDEFEAT!"));
        }

        return scoreboardLines;
    }
}