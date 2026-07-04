package live.pvphard.practice.visual.scoreboard.internal.match.types.state;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.core.config.ConfigService;
import live.pvphard.practice.feature.match.model.internal.MatchGamePlayer;
import live.pvphard.practice.feature.match.model.GameParticipant;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.visual.scoreboard.internal.match.MatchScoreboard;
import live.pvphard.practice.common.animation.internal.types.DotAnimation;
import live.pvphard.practice.common.text.CC;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MatchScoreboardStartingImpl implements MatchScoreboard {
    private final DotAnimation dotAnimation;

    public MatchScoreboardStartingImpl() {
        this.dotAnimation = new DotAnimation();
    }

    @Override
    public List<String> getLines(Profile profile, Player player, GameParticipant<MatchGamePlayer> you, GameParticipant<MatchGamePlayer> opponent) {
        if (profile == null || profile.getProfileData() == null) {
            return Collections.emptyList();
        }
        if (player == null || !player.isOnline()) {
            return Collections.emptyList();
        }
        ConfigService configService = HardPractice.getInstance().getService(ConfigService.class);
        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        if (configService == null || configService.getScoreboardConfig() == null || profileService == null || profile.getMatch() == null) {
            return Collections.emptyList();
        }
        if (opponent == null || opponent.getLeader() == null) {
            return Collections.emptyList();
        }

        List<String> scoreboardLines = new ArrayList<>();
        List<String> template = configService.getScoreboardConfig().getStringList("scoreboard.lines.starting");

        int secondsLeft = Math.max(0, 3 - (int) (profile.getMatch().getElapsedTime() / 1000));

        for (String line : template) {
            scoreboardLines.add(CC.translate(line)
                    .replace("{opponent}", this.getColoredName(profileService.getProfile(opponent.getLeader().getUuid())))
                    .replace("{opponent-ping}", String.valueOf(this.getPing(opponent.getLeader().getTeamPlayer())))
                    .replace("{player-ping}", String.valueOf(this.getPing(player)))
                    .replace("{duration}", profile.getMatch().getDuration())
                    .replace("{arena}", profile.getMatch().getArena().getDisplayName() == null ? "&c&lINDEFINIDO" : profile.getMatch().getArena().getDisplayName())
                    .replace("{dot-animation}", this.dotAnimation.getCurrentFrame())
                    .replace("{countdown}", String.valueOf(secondsLeft))
                    .replace("{kit}", profile.getMatch().getKit().getDisplayName()));
        }

        return scoreboardLines;
    }
}