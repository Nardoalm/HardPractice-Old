package live.pvphard.practice.feature.tournament.formation.assembly;

import live.pvphard.practice.feature.tournament.model.TournamentParticipant;

import java.util.List;

public interface PartyMatcher {
    TournamentParticipant createTeamOfSize(List<TournamentParticipant> availableParties,
                                           int targetSize,
                                           int maxTeamSize);
}