package live.pvphard.practice.feature.match.task.other;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.common.text.LevelBadgeUtil;
import live.pvphard.practice.feature.cooldown.Cooldown;
import live.pvphard.practice.feature.cooldown.CooldownService;
import live.pvphard.practice.feature.cooldown.CooldownType;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.enums.ProfileState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;

/**
 * @author Remi
 * @project hard-practice
 * @date 26/06/2025
 */
public class MatchPearlCooldownTask extends BukkitRunnable {
    @Override
    public void run() {
        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        CooldownService cooldownService = HardPractice.getInstance().getService(CooldownService.class);
        if (profileService == null || cooldownService == null) {
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            Profile profile = profileService.getProfile(player.getUniqueId());
            if (profile == null) {
                continue;
            }

            if (profile.getState() == ProfileState.PLAYING || profile.getState() == ProfileState.FFA) {
                Optional<Cooldown> optionalCooldown = Optional.ofNullable(cooldownService.getCooldown(player.getUniqueId(), CooldownType.ENDER_PEARL));

                if (optionalCooldown.isPresent() && optionalCooldown.get().isActive()) {

                    Cooldown cooldown = optionalCooldown.get();

                    long remainingMillis = cooldown.remainingTimeMillis();
                    long totalDuration = cooldown.getType().getCooldownDuration();
                    int remainingTime = optionalCooldown.get().remainingTime();

                    player.setLevel(remainingTime);

                    if (totalDuration > 0) {
                        player.setExp((float) remainingMillis / totalDuration);
                    }
                } else {
                    updatePlayerLevelProgressBar(player, profile);
                }
            } else {
                updatePlayerLevelProgressBar(player, profile);
            }
        }
    }

    private void updatePlayerLevelProgressBar(Player player, Profile profile) {
        int experience = 0;
        if (profile != null && profile.getProfileData() != null) {
            experience = Math.max(0, profile.getProfileData().getExperience());
        }

        int level = LevelBadgeUtil.getLevel(experience);
        float progressRatio = LevelBadgeUtil.getProgressRatio(experience);

        if (player.getLevel() != level) {
            player.setLevel(level);
        }

        if (Math.abs(player.getExp() - progressRatio) > 0.0001F) {
            player.setExp(progressRatio);
        }
    }
}
