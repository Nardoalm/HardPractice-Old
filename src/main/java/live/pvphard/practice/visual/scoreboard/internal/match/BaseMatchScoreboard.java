package live.pvphard.practice.visual.scoreboard.internal.match;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.core.config.ConfigService;
import live.pvphard.practice.feature.match.Match;
import live.pvphard.practice.feature.match.model.internal.MatchGamePlayer;
import live.pvphard.practice.feature.match.model.GameParticipant;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.common.text.CC;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Remi
 * @project Hard
 * @since 26/06/2025
 */
public abstract class BaseMatchScoreboard implements MatchScoreboard {

    /**
     * Gets the path to the solo version of the scoreboard in the config.
     *
     * @return The configuration path.
     */
    protected abstract String getSoloConfigPath();

    /**
     * Gets the path to the team version of the scoreboard in the config.
     *
     * @return The configuration path.
     */
    protected abstract String getTeamConfigPath();

    @Override
    public List<String> getLines(Profile profile, Player player, GameParticipant<MatchGamePlayer> you, GameParticipant<MatchGamePlayer> opponent) {
        List<String> scoreboardLines = new ArrayList<>();
        Match match = profile.getMatch();
        if (match == null || opponent == null || opponent.getLeader() == null) {
            return scoreboardLines;
        }

        ConfigService configService = HardPractice.getInstance().getService(ConfigService.class);
        if (configService == null || configService.getScoreboardConfig() == null) {
            return scoreboardLines;
        }
        String configPath = match.isTeamMatch() ? getTeamConfigPath() : getSoloConfigPath();

        for (String line : configService.getScoreboardConfig().getStringList(configPath)) {
            scoreboardLines.add(replacePlaceholders(line, profile, player, you, opponent));
        }

        return scoreboardLines;
    }

    /**
     * Replaces all placeholders in a given line of the scoreboard.
     * Child classes should override this to add their own specific placeholders.
     *
     * @param line     The line with placeholders.
     * @param profile  The player's profile.
     * @param player   The player.
     * @param you      The player's game participant.
     * @param opponent The opponent's game participant.
     * @return The line with all placeholders replaced.
     */
    protected String replacePlaceholders(String line, Profile profile, Player player, GameParticipant<MatchGamePlayer> you, GameParticipant<MatchGamePlayer> opponent) {
        Match match = profile.getMatch();

        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);

        String opponentName = match.isTeamMatch() ? getColoredName(profileService.getProfile(opponent.getLeader().getUuid())) + " Time" : getColoredName(profileService.getProfile(opponent.getLeader().getUuid()));

        return CC.translate(line)
                .replace("{opponent}", opponentName)
                .replace("{player-ping}", String.valueOf(getPing(player)))
                .replace("{opponent-ping}", String.valueOf(getPing(opponent.getLeader().getTeamPlayer())))
                .replace("{duration}", match.getDuration())
                .replace("{arena}", match.getArena().getDisplayName() == null ? "&c&lINDEFINIDO" : match.getArena().getDisplayName())
                .replace("{kit}", match.getKit().getDisplayName())
                .replace("{your-players}", String.valueOf(you.getPlayerSize()))
                .replace("{opponent-players}", String.valueOf(opponent.getPlayerSize()))
                .replace("{your-alive}", String.valueOf(you.getAlivePlayerSize()))
                .replace("{opponent-alive}", String.valueOf(opponent.getAlivePlayerSize()));
    }
}
