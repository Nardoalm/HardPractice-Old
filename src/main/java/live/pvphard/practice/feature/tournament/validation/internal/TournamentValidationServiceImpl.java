package live.pvphard.practice.feature.tournament.validation.internal;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.bootstrap.annotation.Service;
import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.feature.cooldown.Cooldown;
import live.pvphard.practice.feature.cooldown.CooldownService;
import live.pvphard.practice.feature.cooldown.CooldownType;
import live.pvphard.practice.feature.tournament.TournamentService;
import live.pvphard.practice.feature.tournament.model.Tournament;
import live.pvphard.practice.feature.tournament.model.TournamentState;
import live.pvphard.practice.feature.tournament.validation.TournamentValidationService;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author Remi
 * @project hard-practice
 * @date 8/08/2025
 */
@Service(provides = TournamentValidationService.class, priority = 1100)
public class TournamentValidationServiceImpl implements TournamentValidationService {
    private static final UUID GLOBAL_TOURNAMENT_COOLDOWN_KEY = UUID.fromString("00000000-0000-0000-0000-000000000000");

    private final CooldownService cooldowns;

    public TournamentValidationServiceImpl(CooldownService cooldowns) {
        this.cooldowns = cooldowns;
    }

    @Override
    public boolean canPlayerHostTournament(Player player) {
        TournamentService tournamentService = HardPractice.getInstance().getService(TournamentService.class);
        if (tournamentService.getPlayerTournament(player) != null) {
            player.sendMessage(CC.translate("&cYou cannot host a tournament while participating in one."));
            return false;
        }
        if (!tournamentService.getTournaments().isEmpty()) {
            player.sendMessage(CC.translate("&cA tournament is already active."));
            return false;
        }

        if (cooldowns != null) {
            Cooldown cooldown = cooldowns.getCooldown(GLOBAL_TOURNAMENT_COOLDOWN_KEY, CooldownType.TOURNAMENT_HOST);
            if (cooldown != null && cooldown.isActive()) {
                player.sendMessage(
                        CC.translate("&cA tournament was hosted recently. Try again in &f"
                                        + cooldown.remainingTimeInMinutes()
                                        + "&c."
                        )
                );
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean canForceStartTournament(Tournament tournament) {
        if (tournament.getState() != TournamentState.WAITING) {
            return false;
        }
        int currentTeams = tournament.getWaitingPool().size();
        return currentTeams >= tournament.getMinTeams();
    }
}