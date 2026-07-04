package live.pvphard.practice.feature.match.listener.types;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.feature.match.Match;
import live.pvphard.practice.feature.match.MatchState;
import live.pvphard.practice.feature.match.MatchService;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.enums.ProfileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class MatchDisconnectListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlayerQuit(PlayerQuitEvent event) {
        this.handleDisconnect(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlayerKick(PlayerKickEvent event) {
        this.handleDisconnect(event.getPlayer());
    }

    private void handleDisconnect(Player player) {
        if (player == null) {
            return;
        }

        org.bukkit.scoreboard.Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard != null) {
            org.bukkit.scoreboard.Objective objective = scoreboard.getObjective("showhealth");
            if (objective != null) {
                objective.unregister();
            }
        }

        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService.getProfiles().get(player.getUniqueId());
        Match match = profile != null ? profile.getMatch() : this.findActiveMatchByPlayer(player);
        if (match == null) {
            if (profile != null) {
                profile.setState(ProfileState.LOBBY);
                profile.setMatch(null);
            }
            return;
        }

        if (match.getSpectators().contains(player.getUniqueId())) {
            match.removeSpectator(player, true);
            if (profile != null) {
                profile.setState(ProfileState.LOBBY);
                profile.setMatch(null);
            }
            return;
        }

        if (match.getGamePlayer(player) == null || match.getGamePlayer(player).isDisconnected()) {
            if (profile != null) {
                profile.setState(ProfileState.LOBBY);
                profile.setMatch(null);
            }
            return;
        }

        if (match.getState() == MatchState.STARTING || match.getState() == MatchState.RUNNING) {
            match.handleDisconnect(player);
        }

        if (profile != null) {
            profile.setState(ProfileState.LOBBY);
            profile.setMatch(null);
        }
    }

    private Match findActiveMatchByPlayer(Player player) {
        if (player == null) {
            return null;
        }

        MatchService matchService = HardPractice.getInstance().getService(MatchService.class);
        if (matchService == null) {
            return null;
        }

        return matchService.getMatches().stream()
                .filter(match -> match != null
                        && (match.getGamePlayer(player) != null || match.getSpectators().contains(player.getUniqueId())))
                .findFirst()
                .orElse(null);
    }
}