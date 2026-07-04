package live.pvphard.practice.feature.tournament.internal;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.bootstrap.annotation.Service;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.feature.cooldown.Cooldown;
import live.pvphard.practice.feature.cooldown.CooldownService;
import live.pvphard.practice.feature.cooldown.CooldownType;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.feature.match.Match;
import live.pvphard.practice.feature.tournament.TournamentService;
import live.pvphard.practice.feature.tournament.broadcast.BroadcastEvent;
import live.pvphard.practice.feature.tournament.broadcast.TournamentBroadcaster;
import live.pvphard.practice.feature.tournament.engine.TournamentConfiguration;
import live.pvphard.practice.feature.tournament.engine.TournamentEngine;
import live.pvphard.practice.feature.tournament.engine.TournamentEvent;
import live.pvphard.practice.feature.tournament.model.Tournament;
import live.pvphard.practice.feature.tournament.model.TournamentState;
import live.pvphard.practice.feature.tournament.model.TournamentType;
import live.pvphard.practice.feature.tournament.participant.ParticipantRegistry;
import live.pvphard.practice.feature.tournament.task.TournamentBroadcastTask;
import live.pvphard.practice.feature.tournament.task.TournamentCountdownService;
import live.pvphard.practice.feature.tournament.validation.TournamentValidationService;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Remi
 * @project hard-practice
 * @date 6/08/2025
 */
@Getter
@Service(provides = TournamentService.class, priority = 1000)
public class TournamentServiceImpl implements TournamentService {
    private static final UUID GLOBAL_TOURNAMENT_COOLDOWN_KEY = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private final List<Tournament> activeTournaments = new CopyOnWriteArrayList<>();
    private final AtomicInteger numericIdGenerator = new AtomicInteger(1);

    private final TournamentEngine engine;
    private final TournamentBroadcaster broadcaster;
    private final ParticipantRegistry participantRegistry;
    private final TournamentValidationService validation;
    private final ProfileService profileService;
    private final CooldownService cooldownService;

    public TournamentServiceImpl(
            TournamentEngine engine,
            TournamentBroadcaster broadcaster,
            ParticipantRegistry participantRegistry,
            TournamentValidationService validation, ProfileService profileService, CooldownService cooldownService) {
        this.engine = engine;
        this.broadcaster = broadcaster;
        this.participantRegistry = participantRegistry;
        this.validation = validation;
        this.profileService = profileService;
        this.cooldownService = cooldownService;
    }

    @Override
    public void hostTournament(Player host, TournamentType type, Kit kit) {
        if (!validation.canPlayerHostTournament(host)) return;

        int numericId = numericIdGenerator.getAndIncrement();

        TournamentConfiguration config =
                TournamentConfiguration.builder()
                        .numericId(numericId)
                        .host(host)
                        .kit(kit)
                        .displayName(type.getDisplayName())
                        .teamSize(type.getTeamSize())
                        .maxTeams(type.getMaxTeams())
                        .minTeams(type.getMinTeams())
                        .isAdminHosted(false)
                        .build();

        Tournament tournament = engine.initializeTournament(config);
        activeTournaments.add(tournament);

        scheduleBroadcastTask(tournament);
        scheduleInactivityTask(tournament);
        startGlobalHostCooldown();

        broadcaster.broadcast(new BroadcastEvent.TournamentHosted(tournament));
    }

    @Override
    public void adminHostTournament(Player host, Kit kit, int teamSize, int maxTeams) {
        int numericId = numericIdGenerator.getAndIncrement();

        TournamentConfiguration config =
                TournamentConfiguration.builder()
                        .numericId(numericId)
                        .host(host)
                        .kit(kit)
                        .displayName(teamSize + "v" + teamSize)
                        .teamSize(teamSize)
                        .maxTeams(maxTeams)
                        .minTeams(3)
                        .isAdminHosted(true)
                        .build();

        Tournament tournament = engine.initializeTournament(config);
        activeTournaments.add(tournament);

        scheduleBroadcastTask(tournament);
        scheduleInactivityTask(tournament);

        broadcaster.broadcast(new BroadcastEvent.TournamentHosted(tournament));
    }

    @Override
    public void joinTournament(Player player, Tournament tournament) {
        engine.processEvent(tournament, new TournamentEvent.PlayerJoinRequest(player));
        if (tournament.getState() == TournamentState.WAITING) {
            scheduleInactivityTask(tournament);
        }
        pruneIfEnded(tournament);
    }

    @Override
    public void handlePlayerDeparture(Player player) {
        Profile profile = profileService.getProfile(player.getUniqueId());
        if (profile != null && profile.getMatch() != null) {
            return;
        }

        Tournament tournament = getPlayerTournament(player);
        if (tournament == null) return;

        engine.processEvent(tournament, new TournamentEvent.PlayerDeparture(player));
        if (tournament.getState() == TournamentState.WAITING) {
            scheduleInactivityTask(tournament);
        }
        pruneIfEnded(tournament);
    }

    @Override
    public void handleMatchEnd(Match match) {
        Tournament tournament = findTournamentByMatch(match);
        if (tournament == null) return;

        engine.processEvent(tournament, new TournamentEvent.MatchCompletion(match));
        pruneIfEnded(tournament);
    }

    @Override
    public void forceStartTournament(Tournament tournament) {
        if (!validation.canForceStartTournament(tournament)) return;

        engine.processEvent(tournament, new TournamentEvent.ForceStart(null));
        pruneIfEnded(tournament);
    }

    @Override
    public void cancelTournament(Tournament tournament, String reason) {
        engine.processEvent(tournament, new TournamentEvent.AdminCancellation(reason));
        clearGlobalHostCooldown();
        disposeTournament(tournament);
    }

    @Override
    public Tournament getTournament(UUID tournamentId) {
        return activeTournaments.stream()
                .filter(t -> t.getTournamentId().equals(tournamentId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Tournament getPlayerTournament(Player player) {
        return activeTournaments.stream()
                .filter(t -> t.getState() != TournamentState.ENDED)
                .filter(t -> participantRegistry.findParticipantByPlayer(player, t) != null)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Tournament> getTournaments() {
        return new ArrayList<>(activeTournaments);
    }

    /**
     * Starts the global tournament host cooldown window.
     * The cooldown duration is defined by CooldownType.TOURNAMENT_HOST.
     */
    private void startGlobalHostCooldown() {
        if (cooldownService == null) return;

        Cooldown cooldown = new Cooldown(CooldownType.TOURNAMENT_HOST, () -> {
        });
        cooldown.resetCooldown();
        cooldownService.addCooldown(
                GLOBAL_TOURNAMENT_COOLDOWN_KEY, CooldownType.TOURNAMENT_HOST, cooldown);
    }

    /**
     * Clears the global tournament host cooldown (e.g., on cancel/inactivity).
     */
    private void clearGlobalHostCooldown() {
        if (cooldownService == null) return;

        cooldownService.removeCooldown(
                GLOBAL_TOURNAMENT_COOLDOWN_KEY, CooldownType.TOURNAMENT_HOST);
    }

    /**
     * Disposes of a tournament by canceling all associated tasks and clearing its data.
     * This is called when a tournament ends or is canceled.
     *
     * @param tournament The tournament to dispose of.
     */
    private void disposeTournament(Tournament tournament) {
        if (tournament.getStartingTask() != null) {
            tournament.getStartingTask().cancel();
            tournament.setStartingTask(null);
        }
        if (tournament.getRoundStartTask() != null) {
            tournament.getRoundStartTask().cancel();
            tournament.setRoundStartTask(null);
        }
        if (tournament.getInactivityTask() != null) {
            tournament.getInactivityTask().cancel();
            tournament.setInactivityTask(null);
        }
        if (tournament.getBroadcastTask() != null) {
            tournament.getBroadcastTask().cancel();
            tournament.setBroadcastTask(null);
        }

        TournamentCountdownService countdowns = HardPractice.getInstance().getService(TournamentCountdownService.class);

        if (countdowns != null) {
            countdowns.clearStartTask();
            countdowns.clearRoundStartTask();
        }

        participantRegistry.purgeTournament(tournament);

        tournament.getActiveMatches().clear();
        tournament.getWaitingPool().clear();
        tournament.getParticipants().clear();
        tournament.getRoundParticipants().clear();
        tournament.getPlacementList().clear();

        activeTournaments.remove(tournament);
    }

    /**
     * Schedules the periodic broadcast task for a tournament.
     *
     * @param tournament The tournament to schedule broadcasts for.
     */
    private void scheduleBroadcastTask(Tournament tournament) {
        TournamentBroadcastTask task = new TournamentBroadcastTask(tournament);
        tournament.setBroadcastTask(
                Bukkit.getScheduler()
                        .runTaskTimer(HardPractice.getInstance(), task, 0L, 300L));
    }

    /**
     * Schedules a task to cancel the tournament after 5 minutes of inactivity.
     * This is to prevent tournaments from lingering indefinitely without activity.
     *
     * @param tournament The tournament to monitor for inactivity.
     */
    private void scheduleInactivityTask(Tournament tournament) {
        if (tournament.getInactivityTask() != null) {
            tournament.getInactivityTask().cancel();
        }
        tournament.setInactivityTask(Bukkit.getScheduler().runTaskLater(HardPractice.getInstance(),
                () -> {
                    engine.processEvent(tournament, new TournamentEvent.AdminCancellation("Inactivity"));
                    clearGlobalHostCooldown();
                    disposeTournament(tournament);
                },
                6000L));
    }

    /**
     * Removes a tournament from active list if it has ended.
     *
     * @param tournament The tournament to evaluate.
     */
    private void pruneIfEnded(Tournament tournament) {
        if (tournament.getState() == TournamentState.ENDED) {
            disposeTournament(tournament);
        }
    }

    /**
     * Finds the tournament containing a specific match.
     *
     * @param match The match to search for.
     * @return The tournament or null if not found.
     */
    private Tournament findTournamentByMatch(Match match) {
        return activeTournaments.stream()
                .filter(t -> t.getActiveMatches().contains(match))
                .findFirst()
                .orElse(null);
    }
}
