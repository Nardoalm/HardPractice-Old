package live.pvphard.practice.feature.tournament.broadcast;

import live.pvphard.practice.bootstrap.lifecycle.Service;
import live.pvphard.practice.feature.tournament.model.Tournament;
import live.pvphard.practice.feature.tournament.model.TournamentParticipant;

/**
 * @author Remi
 * @project hard-practice
 * @date 8/08/2025
 */
public interface TournamentBroadcaster extends Service {
    /**
     * Broadcasts a tournament event to all relevant players.
     *
     * @param event The broadcast event to send.
     */
    void broadcast(BroadcastEvent event);

    /**
     * Sends a targeted message to specific participants of a tournament.
     *
     * @param tournament   The tournament context.
     * @param participants The participants to message.
     * @param message      The message to send.
     */
    void sendTargetedMessage(Tournament tournament, Iterable<TournamentParticipant> participants, String message);

    /**
     * Broadcasts the final tournament results and winner (top 3), using the
     * exact format of the original system.
     *
     * @param tournament The completed tournament.
     * @param placements The final participant placements.
     */
    void broadcastResults(Tournament tournament, TournamentPlacements placements);
}