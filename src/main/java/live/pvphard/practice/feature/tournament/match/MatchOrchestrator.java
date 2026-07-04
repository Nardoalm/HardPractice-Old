package live.pvphard.practice.feature.tournament.match;

import live.pvphard.practice.bootstrap.lifecycle.Service;
import live.pvphard.practice.feature.match.Match;
import live.pvphard.practice.feature.tournament.model.Tournament;
import live.pvphard.practice.feature.tournament.model.TournamentParticipant;

import java.util.List;

/**
 * @author Remi
 * @project hard-practice
 * @date 8/08/2025
 */
public interface MatchOrchestrator extends Service {
    /**
     * Creates matches for the current round of participants.
     *
     * @param tournament The tournament to create matches for.
     * @param participants The participants to match up.
     * @return The orchestration result.
     */
    MatchOrchestrationResult createRoundMatches(
            Tournament tournament, List<TournamentParticipant> participants);

    /**
     * Processes the completion of a tournament match.
     *
     * @param match The completed match.
     * @param tournament The tournament the match belongs to.
     * @return The processing result.
     */
    MatchProcessingResult processMatchResult(Match match, Tournament tournament);

    /**
     * Checks if all active matches in a tournament round are complete.
     *
     * @param tournament The tournament to check.
     * @return True if all matches are complete.
     */
    boolean isRoundComplete(Tournament tournament);
}