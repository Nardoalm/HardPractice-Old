package live.pvphard.practice.visual.scoreboard;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.enums.ProfileState;
import live.pvphard.practice.common.animation.AnimationService;
import live.pvphard.practice.common.animation.AnimationType;
import live.pvphard.practice.common.animation.internal.types.DotAnimation;
import live.pvphard.practice.common.reflect.utility.ReflectionUtility;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author Emmy
 * @project Hard
 * @since 30/04/2025
 */
public interface Scoreboard {
    /**
     * Gets the scoreboard lines for the given profile.
     *
     * @param profile The profile to get the scoreboard lines for.
     * @return The scoreboard lines.
     */
    List<String> getLines(Profile profile);

    /**
     * Gets the scoreboard lines for the given profile.
     *
     * @param profile The profile to get the scoreboard lines for.
     * @param player  The player to get the scoreboard lines for.
     * @return The scoreboard lines.
     */
    List<String> getLines(Profile profile, Player player);

    /**
     * Gets the dot animation for the scoreboard.
     *
     * @return The dot animation.
     */
    default DotAnimation getDotAnimation() {
        return HardPractice.getInstance().getService(AnimationService.class).getAnimation(DotAnimation.class, AnimationType.INTERNAL);
    }

    /**
     * Gets the ping of the player by using reflect.
     *
     * @param player The player to get the ping for.
     * @return The ping of the player.
     */
    default int getPing(Player player) {
        if (player == null) {
            return 0;
        }

        return ReflectionUtility.getPing(player);
    }

    /**
     * Safely counts the number of profiles in a given state.
     *
     * @param service The ProfileService to use for counting.
     * @param state   The ProfileState to count.
     * @return The count of profiles in the specified state, or 0 if an error occurs.
     */
    default int safeCountState(ProfileService service, ProfileState state) {
        try {
            return (int) service.getProfiles().values().parallelStream()
                    .filter(p -> p.getState() == state)
                    .count();
        } catch (Exception e) {
            return 0;
        }
    }
}