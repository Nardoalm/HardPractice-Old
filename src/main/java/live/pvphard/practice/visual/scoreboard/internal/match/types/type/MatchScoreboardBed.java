package live.pvphard.practice.visual.scoreboard.internal.match.types.type;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.core.config.ConfigService;
import live.pvphard.practice.feature.match.internal.types.BedMatch;
import live.pvphard.practice.feature.match.internal.types.DefaultMatch;
import live.pvphard.practice.feature.match.model.internal.MatchGamePlayer;
import live.pvphard.practice.feature.match.model.GameParticipant;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.visual.scoreboard.internal.match.BaseMatchScoreboard;
import live.pvphard.practice.visual.scoreboard.internal.match.annotation.ScoreboardData;
import live.pvphard.practice.visual.scoreboard.utility.ScoreboardUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author Remi
 * @project Hard
 * @since 26/06/2025
 */
@ScoreboardData(match = BedMatch.class)
public class MatchScoreboardBed extends BaseMatchScoreboard {
    @Override
    protected String getSoloConfigPath() {
        return "scoreboard.lines.playing.solo.bed-match";
    }

    @Override
    protected String getTeamConfigPath() {
        return "scoreboard.lines.playing.team.bed-match";
    }

    @Override
    protected String replacePlaceholders(String line, Profile profile, Player player, GameParticipant<MatchGamePlayer> you, GameParticipant<MatchGamePlayer> opponent) {
        String baseLine = super.replacePlaceholders(line, profile, player, you, opponent);
        DefaultMatch match = (DefaultMatch) profile.getMatch();

        String yourColorDisplay = getTeamDisplay(match.getTeamColor(you));
        String opponentColorDisplay = getTeamDisplay(match.getTeamColor(opponent));

        return baseLine
                .replace("{your-bed}", ScoreboardUtil.visualizeBed(you.isBedBroken()))
                .replace("{opponent-bed}", ScoreboardUtil.visualizeBed(opponent.isBedBroken()))
                .replace("{your-color-display}", yourColorDisplay)
                .replace("{opponent-color-display}", opponentColorDisplay);
    }

    private String getTeamDisplay(ChatColor teamColor) {
        if (teamColor == null) return "";
        String colorName = teamColor.name().toLowerCase();
        ConfigService configService = HardPractice.getInstance().getService(ConfigService.class);
        if (configService != null && configService.getScoreboardConfig() != null) {
            String configured = configService.getScoreboardConfig().getString("scoreboard.team-displays." + colorName, "");
            if (configured != null && !configured.trim().isEmpty()) {
                return configured;
            }
        }

        return teamColor + colorName.replace('_', ' ');
    }
}
