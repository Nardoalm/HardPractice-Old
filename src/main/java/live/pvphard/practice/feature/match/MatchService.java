package live.pvphard.practice.feature.match;

import live.pvphard.practice.bootstrap.lifecycle.Service;
import live.pvphard.practice.feature.arena.Arena;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.feature.match.model.GameParticipant;
import live.pvphard.practice.feature.match.model.internal.MatchGamePlayer;
import live.pvphard.practice.feature.tournament.model.Tournament;

import java.util.List;

public interface MatchService extends Service {
    List<Match> getMatches();

    void addMatch(Match match);

    void removeMatch(Match match);

    void createTournamentMatch(Tournament tournament, Kit kit, Arena arena, GameParticipant<MatchGamePlayer> pA, GameParticipant<MatchGamePlayer> pB);

    void createAndStartMatch(Kit kit, Arena arena, GameParticipant<MatchGamePlayer> participantA, GameParticipant<MatchGamePlayer> participantB, boolean teamMatch, boolean affectStatistics, boolean isRanked);
}