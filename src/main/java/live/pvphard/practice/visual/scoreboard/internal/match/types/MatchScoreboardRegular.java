package live.pvphard.practice.visual.scoreboard.internal.match.types;

import live.pvphard.practice.visual.scoreboard.internal.match.BaseMatchScoreboard;
import live.pvphard.practice.visual.scoreboard.internal.match.annotation.ScoreboardData;

/**
 * @author Emmy
 * @project Hard
 * @since 30/04/2025
 */
@ScoreboardData(isDefault = true)
public class MatchScoreboardRegular extends BaseMatchScoreboard {
    @Override
    protected String getSoloConfigPath() {
        return "scoreboard.lines.playing.solo.regular-match";
    }

    @Override
    protected String getTeamConfigPath() {
        return "scoreboard.lines.playing.team.regular-match";
    }
}