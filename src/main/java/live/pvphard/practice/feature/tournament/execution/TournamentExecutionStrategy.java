package live.pvphard.practice.feature.tournament.execution;

import live.pvphard.practice.bootstrap.lifecycle.Service;
import live.pvphard.practice.feature.tournament.engine.TournamentEvent;
import live.pvphard.practice.feature.tournament.model.Tournament;

/**
 * @author Remi
 * @project hard-practice
 * @date 8/08/2025
 */
public interface TournamentExecutionStrategy extends Service {
    /**
     * Handles an external event (join, leave, match end, timers) during
     * tournament execution.
     *
     * @param tournament The tournament receiving the event.
     * @param event      The event to process.
     * @return The result of event processing.
     */
    ExecutionResult handleEvent(Tournament tournament, TournamentEvent event);
}