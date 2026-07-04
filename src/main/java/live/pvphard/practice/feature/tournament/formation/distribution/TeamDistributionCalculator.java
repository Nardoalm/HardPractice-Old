package live.pvphard.practice.feature.tournament.formation.distribution;

import live.pvphard.practice.feature.tournament.formation.model.TeamDistribution;
import live.pvphard.practice.feature.tournament.model.TournamentParticipant;

import java.util.List;

public interface TeamDistributionCalculator {
    TeamDistribution calculateDistribution(List<TournamentParticipant> participantPool, int maxTeamSize);
}

