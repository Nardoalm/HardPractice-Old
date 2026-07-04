package live.pvphard.practice.feature.tournament.engine;

import live.pvphard.practice.feature.match.Match;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * @author Remi
 * @project hard-practice
 * @date 8/08/2025
 */
public abstract class TournamentEvent {
    @Getter
    @RequiredArgsConstructor
    public static class PlayerJoinRequest extends TournamentEvent {
        private final Player player;
    }

    @Getter
    @RequiredArgsConstructor
    public static class PlayerDeparture extends TournamentEvent {
        private final Player player;
    }

    @Getter
    @RequiredArgsConstructor
    public static class MatchCompletion extends TournamentEvent {
        private final Match match;
    }

    @Getter
    @RequiredArgsConstructor
    public static class ForceStart extends TournamentEvent {
        private final Player requester;
    }

    @Getter
    @RequiredArgsConstructor
    public static class AdminCancellation extends TournamentEvent {
        private final String reason;
    }

    /**
     * Signals the start countdown reached zero and the tournament should begin.
     */
    public static class StartCountdownFinished extends TournamentEvent {
    }

    /**
     * Signals the round intermission countdown reached zero and the next round
     * should begin.
     */
    public static class RoundCountdownFinished extends TournamentEvent {
    }
}
