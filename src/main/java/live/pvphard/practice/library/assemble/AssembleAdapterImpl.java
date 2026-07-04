package live.pvphard.practice.library.assemble;

import live.pvphard.practice.core.config.ConfigService;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.enums.ProfileState;
import live.pvphard.practice.visual.scoreboard.internal.EventScoreboardImpl;
import live.pvphard.practice.visual.scoreboard.internal.FFAScoreboardImpl;
import live.pvphard.practice.visual.scoreboard.internal.LobbyScoreboardImpl;
import live.pvphard.practice.visual.scoreboard.internal.QueueScoreboardImpl;
import live.pvphard.practice.visual.scoreboard.internal.SpectatorScoreboardImpl;
import live.pvphard.practice.visual.scoreboard.internal.match.MatchScoreboardImpl;
import live.pvphard.practice.common.animation.AnimationService;
import live.pvphard.practice.common.animation.AnimationType;
import live.pvphard.practice.common.animation.internal.config.ScoreboardTitleAnimation;
import live.pvphard.practice.common.PlaceholderUtil;
import live.pvphard.practice.common.text.CC;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Emmy
 * @project Hard
 * @date 27/03/2024 - 14:27
 */
public class AssembleAdapterImpl implements AssembleAdapter {
    private final AnimationService animationService;
    private final ProfileService profileService;
    private final ConfigService configService;

    private final LobbyScoreboardImpl lobbyScoreboardImpl = new LobbyScoreboardImpl();
    private final QueueScoreboardImpl queueScoreboardImpl = new QueueScoreboardImpl();
    private final MatchScoreboardImpl matchScoreboardImpl = new MatchScoreboardImpl();
    private final SpectatorScoreboardImpl spectatorScoreboardImpl = new SpectatorScoreboardImpl();
    private final FFAScoreboardImpl ffaScoreboardImpl = new FFAScoreboardImpl();
    private final EventScoreboardImpl eventScoreboardImpl = new EventScoreboardImpl();

    public AssembleAdapterImpl(AnimationService animationService, ProfileService profileService, ConfigService configService) {
        this.animationService = animationService;
        this.profileService = profileService;
        this.configService = configService;
    }

    @Override
    public String getTitle(Player player) {
        String title = this.animationService.getAnimation(ScoreboardTitleAnimation.class, AnimationType.CONFIG).getText();
        return PlaceholderUtil.setPapiSafe(player, title);
    }

    /**
     * Get the lines of the scoreboard.
     *
     * @param player The player to get the lines for.
     * @return The lines of the scoreboard.
     */
    @Override
    public List<String> getLines(Player player) {
        Profile profile = this.profileService.getProfile(player.getUniqueId());

        if (profile.getProfileData().getSettingData().isScoreboardEnabled()) {

            if (profile.getState() == ProfileState.EDITING) {
                return Collections.emptyList();
            }

            List<String> lines = new ArrayList<>();

            switch (profile.getState()) {
                case LOBBY:
                    lines.addAll(this.lobbyScoreboardImpl.getLines(profile));
                    break;
                case WAITING:
                    lines.addAll(this.queueScoreboardImpl.getLines(profile));
                    break;
                case PLAYING:
                    lines.addAll(this.matchScoreboardImpl.getLines(profile, player));
                    break;
                case SPECTATING:
                    lines.addAll(this.spectatorScoreboardImpl.getLines(profile));
                    break;
                case FFA:
                    lines.addAll(this.ffaScoreboardImpl.getLines(profile, player));
                    break;
                case PLAYING_EVENT:
                    lines.addAll(this.eventScoreboardImpl.getLines(profile, player));
                    break;
            }

            List<String> footer = this.configService.getScoreboardConfig().getStringList("scoreboard.footer-addition");
            footer.forEach(line -> lines.add(CC.translate(line)));

            lines.replaceAll(line -> line.replace("{sidebar}", this.getScoreboardLines(profile)));
            return PlaceholderUtil.setPapiSafe(player, lines);
        }
        return null;
    }

    /**
     * Method to either show the scoreboard lines or not.
     *
     * @param profile The profile to get the scoreboard lines for.
     * @return The scoreboard lines.
     */
    private String getScoreboardLines(Profile profile) {
        if (profile.getProfileData().getSettingData().isShowScoreboardLines()) {
            return this.configService.getScoreboardConfig().getString("scoreboard.sidebar-format");
        }
        return "";
    }
}
