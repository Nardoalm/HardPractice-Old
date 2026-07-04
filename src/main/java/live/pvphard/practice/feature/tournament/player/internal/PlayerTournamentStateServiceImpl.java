package live.pvphard.practice.feature.tournament.player.internal;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.bootstrap.annotation.Service;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.enums.ProfileState;
import live.pvphard.practice.feature.hotbar.HotbarService;
import live.pvphard.practice.feature.tournament.model.Tournament;
import live.pvphard.practice.feature.tournament.player.PlayerTournamentStateService;
import org.bukkit.entity.Player;

/**
 * @author Remi
 * @project hard-practice
 * @date 8/08/2025
 */

@Service(provides = PlayerTournamentStateService.class, priority = 1100)
public class PlayerTournamentStateServiceImpl implements PlayerTournamentStateService {
    private final ProfileService profileService;
    private final HotbarService hotbarService;

    public PlayerTournamentStateServiceImpl() {
        this.profileService = HardPractice.getInstance().getService(ProfileService.class);
        this.hotbarService = HardPractice.getInstance().getService(HotbarService.class);
    }

    @Override
    public void setPlayerTournamentState(Player player, Tournament tournament) {
        if (player == null || !player.isOnline()) return;

        Profile profile = profileService.getProfile(player.getUniqueId());
        if (profile == null) return;

        profile.setState(ProfileState.TOURNAMENT_LOBBY);
        profile.setTournament(tournament);
        hotbarService.applyHotbarItems(player);
    }

    @Override
    public void resetPlayerStateToLobby(Player player) {
        if (player == null || !player.isOnline()) return;

        Profile profile = profileService.getProfile(player.getUniqueId());
        if (profile == null) return;

        profile.setState(ProfileState.LOBBY);
        profile.setTournament(null);
        hotbarService.applyHotbarItems(player);
    }
}