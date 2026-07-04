package live.pvphard.practice.feature.tournament.match;

import live.pvphard.practice.feature.match.Match;
import live.pvphard.practice.feature.tournament.model.TournamentParticipant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author Remi
 * @project hard-practice
 * @date 8/08/2025
 */
@Getter
@RequiredArgsConstructor
public class MatchOrchestrationResult {
    private final List<Match> createdMatches;
    private final List<TournamentParticipant> byeParticipants;
    private final boolean successful;
    private final String errorMessage;

    /**
     * Creates a success result with created matches and byes.
     *
     * @param matches Created matches.
     * @param byes    Bye participants.
     * @return Success result.
     */
    public static MatchOrchestrationResult success(List<Match> matches, List<TournamentParticipant> byes) {
        return new MatchOrchestrationResult(matches, byes, true, null);
    }

    /**
     * Creates a failure result with error message.
     *
     * @param error The error message.
     * @return Failure result.
     */
    public static MatchOrchestrationResult failure(String error) {
        return new MatchOrchestrationResult(null, null, false, error);
    }
}