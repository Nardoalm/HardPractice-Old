package live.pvphard.practice.visual.scoreboard.internal.match.types.type;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.feature.kit.setting.types.mode.KitSettingBoxing;
import live.pvphard.practice.core.config.ConfigService;
import live.pvphard.practice.feature.match.model.internal.MatchGamePlayer;
import live.pvphard.practice.feature.match.model.GameParticipant;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.visual.scoreboard.internal.match.BaseMatchScoreboard;
import live.pvphard.practice.visual.scoreboard.internal.match.annotation.ScoreboardData;
import live.pvphard.practice.common.text.CC;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;


@ScoreboardData(kit = KitSettingBoxing.class)
public class MatchScoreboardBoxing extends BaseMatchScoreboard {
    @Override
    protected String getSoloConfigPath() {
        return "scoreboard.lines.playing.solo.boxing-match";
    }

    @Override
    protected String getTeamConfigPath() {
        return "scoreboard.lines.playing.team.boxing-match";
    }

    @Override
    protected String replacePlaceholders(String line, Profile profile, Player player, GameParticipant<MatchGamePlayer> you, GameParticipant<MatchGamePlayer> opponent) {
        String baseLine = super.replacePlaceholders(line, profile, player, you, opponent);
        FileConfiguration config = HardPractice.getInstance().getService(ConfigService.class).getScoreboardConfig();

        int playerHits = profile.getMatch().isTeamMatch() ? you.getTeamHits() : you.getLeader().getData().getHits();
        int opponentHits = profile.getMatch().isTeamMatch() ? opponent.getTeamHits() : opponent.getLeader().getData().getHits();

        int playerCombo = you.getLeader().getData().getCombo();
        int opponentCombo = opponent.getLeader().getData().getCombo();

        String hitDifference = formatHitDifference(playerHits, opponentHits, config);
        String combo = formatCombo(playerCombo, opponentCombo, config);

        return baseLine
                .replace("{player-hits}", String.valueOf(playerHits))
                .replace("{opponent-hits}", String.valueOf(opponentHits))
                .replace("{difference}", hitDifference)
                .replace("{combo}", combo);
    }

    private String formatHitDifference(int playerHits, int opponentHits, FileConfiguration config) {
        int difference = playerHits - opponentHits;
        String format;
        if (difference > 0) {
            format = config.getString("boxing-hit-difference.positive-difference", "&a(+{difference})").replace("{difference}", String.valueOf(difference));
        } else if (difference < 0) {
            format = config.getString("boxing-hit-difference.negative-difference", "&c(-{difference})").replace("{difference}", String.valueOf(Math.abs(difference)));
        } else {
            format = config.getString("boxing-hit-difference.no-difference", "&a(+0)");
        }
        return CC.translate(format);
    }

    private String formatCombo(int playerCombo, int opponentCombo, FileConfiguration config) {
        String format;
        if (playerCombo > 1) {
            format = config.getString("boxing-combo-display.positive-combo", "&a{combo} Combo").replace("{combo}", String.valueOf(playerCombo));
        } else if (opponentCombo > 1) {
            format = config.getString("boxing-combo-display.negative-combo", "&c{combo} Combo").replace("{combo}", String.valueOf(opponentCombo));
        } else {
            format = config.getString("boxing-combo-display.no-combo", "&fSem Combo");
        }
        return CC.translate(format);
    }
}
