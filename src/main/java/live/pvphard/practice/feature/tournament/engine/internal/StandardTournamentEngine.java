package live.pvphard.practice.feature.tournament.engine.internal;

import live.pvphard.practice.bootstrap.annotation.Service;
import live.pvphard.practice.feature.tournament.engine.TournamentConfiguration;
import live.pvphard.practice.feature.tournament.engine.TournamentEngine;
import live.pvphard.practice.feature.tournament.engine.TournamentEvent;
import live.pvphard.practice.feature.tournament.execution.TournamentExecutionStrategy;
import live.pvphard.practice.feature.tournament.model.Tournament;

/**
 * @author Remi
 * @project hard-practice
 * @date 8/08/2025
 */
@Service(provides = TournamentEngine.class, priority = 1000)
public class StandardTournamentEngine implements TournamentEngine {
    private final TournamentExecutionStrategy strategy;

    public StandardTournamentEngine(TournamentExecutionStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public Tournament initializeTournament(TournamentConfiguration configuration) {
        return new Tournament(
                configuration.getNumericId(),
                configuration.getHost().getName(),
                configuration.getKit(),
                configuration.getDisplayName(),
                configuration.getTeamSize(),
                configuration.getMaxTeams(),
                configuration.getMinTeams());
    }

    @Override
    public void processEvent(Tournament tournament, TournamentEvent event) {
        strategy.handleEvent(tournament, event);
    }
}