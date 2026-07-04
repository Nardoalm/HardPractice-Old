package live.pvphard.practice.feature.tournament.state;

import live.pvphard.practice.bootstrap.lifecycle.Service;
import live.pvphard.practice.feature.tournament.model.Tournament;
import live.pvphard.practice.feature.tournament.model.TournamentState;

/**
 * @author Remi
 * @project hard-practice
 * @date 8/08/2025
 */
public interface TournamentStateManager extends Service {
    /**
     * Validates if a state transition is allowed.
     *
     * @param tournament The tournament to check.
     * @param newState The target state.
     * @return True if transition is valid.
     */
    boolean canTransitionTo(Tournament tournament, TournamentState newState);

    /**
     * Executes a validated state transition, mutating the tournament.
     *
     * @param tournament The tournament to transition.
     * @param newState The target state.
     * @return The updated tournament.
     */
    Tournament transitionState(Tournament tournament, TournamentState newState);
}