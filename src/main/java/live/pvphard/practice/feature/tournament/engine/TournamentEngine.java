package live.pvphard.practice.feature.tournament.engine;

import live.pvphard.practice.bootstrap.lifecycle.Service;
import live.pvphard.practice.feature.tournament.model.Tournament;

/**
 * @author Remi
 * @project hard-practice
 * @date 8/08/2025
 */
public interface TournamentEngine extends Service {
    /**
     * Initializes and returns a new tournament from configuration.
     *
     * @param configuration The tournament setup configuration.
     * @return The initialized tournament instance.
     */
    Tournament initializeTournament(TournamentConfiguration configuration);

    /**
     * Processes an external event against a tournament, advancing or mutating
     * its lifecycle via the configured strategy.
     *
     * @param tournament The tournament to mutate.
     * @param event      The event that occurred.
     */
    void processEvent(Tournament tournament, TournamentEvent event);
}