package live.pvphard.practice.feature.tournament.match;

import live.pvphard.practice.bootstrap.lifecycle.Service;
import live.pvphard.practice.feature.match.model.GameParticipant;
import live.pvphard.practice.feature.match.model.internal.MatchGamePlayer;
import live.pvphard.practice.feature.tournament.model.TournamentParticipant;

/**
 * @author Remi
 * @project hard-practice
 * @date 8/08/2025
 */
public interface MatchParticipantFactory extends Service {
    /**
     * Creates a GameParticipant for a tournament team, producing a
     * TeamGameParticipant for teams (>1) or a solo participant for 1.
     *
     * @param participant The tournament team.
     * @return The constructed GameParticipant.
     */
    GameParticipant<MatchGamePlayer> buildParticipant(TournamentParticipant participant);
}