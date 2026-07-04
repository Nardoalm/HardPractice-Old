package live.pvphard.practice.feature.tournament.listener;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.feature.tournament.TournamentService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

/**
 * @author Remi
 * @project hard-practice
 * @date 6/08/2025
 */
public class TournamentListener implements Listener {
    private final TournamentService tournamentService = HardPractice.getInstance().getService(TournamentService.class);
    private final ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (isInMatch(event.getPlayer().getUniqueId())) {
            return;
        }

        if (tournamentService.getPlayerTournament(event.getPlayer()) != null) {
            tournamentService.handlePlayerDeparture(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        if (isInMatch(event.getPlayer().getUniqueId())) {
            return;
        }

        if (tournamentService.getPlayerTournament(event.getPlayer()) != null) {
            tournamentService.handlePlayerDeparture(event.getPlayer());
        }
    }

    /**
     * Checks if the player is currently in a match.
     *
     * @param uuid The UUID of the player.
     * @return true if the player is in a match, false otherwise.
     */
    private boolean isInMatch(UUID uuid) {
        Profile profile = profileService.getProfile(uuid);
        return profile != null && profile.getMatch() != null;
    }
}