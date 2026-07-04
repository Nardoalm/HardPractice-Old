package live.pvphard.practice.feature.queue.listener;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.enums.ProfileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author Remi
 * @project Hard
 * @date 5/25/2024
 */
public class QueueListener implements Listener {
    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());

        if (profile.getState() == ProfileState.WAITING
                && profile.getQueueProfile() != null
                && profile.getQueueProfile().getQueue() != null) {
            profile.getQueueProfile().getQueue().removePlayer(profile.getQueueProfile());
        }
    }

    @EventHandler
    private void onPlayerKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());

        if (profile.getState() == ProfileState.WAITING
                && profile.getQueueProfile() != null
                && profile.getQueueProfile().getQueue() != null) {
            profile.getQueueProfile().getQueue().removePlayer(profile.getQueueProfile());
        }
    }
}
