package live.pvphard.practice.visual.scoreboard.internal.match.types.type;

import live.pvphard.practice.feature.kit.setting.types.mode.KitSettingStickFight;
import live.pvphard.practice.feature.match.Match;
import live.pvphard.practice.feature.match.internal.types.RoundsMatch;
import live.pvphard.practice.feature.match.model.internal.MatchGamePlayer;
import live.pvphard.practice.feature.match.model.GameParticipant;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.visual.scoreboard.internal.match.BaseMatchScoreboard;
import live.pvphard.practice.visual.scoreboard.internal.match.annotation.ScoreboardData;
import live.pvphard.practice.visual.scoreboard.utility.ScoreboardUtil;
import org.bukkit.entity.Player;

@ScoreboardData(kit = KitSettingStickFight.class)
public class MatchScoreboardStickFight extends BaseMatchScoreboard {

    @Override
    protected String getSoloConfigPath() {
        return "scoreboard.lines.playing.solo.stickfight-match";
    }

    @Override
    protected String getTeamConfigPath() {
        return "scoreboard.lines.playing.team.stickfight-match";
    }

    @Override
    protected String replacePlaceholders(String line, Profile profile, Player player, GameParticipant<MatchGamePlayer> you, GameParticipant<MatchGamePlayer> opponent) {
        String baseLine = super.replacePlaceholders(line, profile, player, you, opponent);
        Match match = profile.getMatch();

        if (!(match instanceof RoundsMatch)) {
            return baseLine
                    .replace("{goals}", "0")
                    .replace("{opponent-goals}", "0")
                    .replace("{current-round}", "1")
                    .replace("{color}", "&f")
                    .replace("{opponent-color}", "&f");
        }

        RoundsMatch roundsMatch = (RoundsMatch) match;

        return baseLine
                .replace("{goals}", ScoreboardUtil.visualizeGoals(roundsMatch.getParticipantA().getLeader().getData().getScore(), 5))
                .replace("{opponent-goals}", ScoreboardUtil.visualizeGoals(roundsMatch.getParticipantB().getLeader().getData().getScore(), 5))
                .replace("{current-round}", String.valueOf(roundsMatch.getCurrentRound()))
                .replace("{color}", String.valueOf(roundsMatch.getTeamAColor()))
                .replace("{opponent-color}", String.valueOf(roundsMatch.getTeamBColor()));
    }
}