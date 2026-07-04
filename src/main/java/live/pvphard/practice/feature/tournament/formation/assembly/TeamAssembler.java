package live.pvphard.practice.feature.tournament.formation.assembly;

import live.pvphard.practice.feature.tournament.formation.model.TeamDistribution;
import live.pvphard.practice.feature.tournament.model.TournamentParticipant;

import java.util.List;

public interface TeamAssembler {
    List<TournamentParticipant> assembleTeams(List<TournamentParticipant> participantPool,
                                              TeamDistribution distribution,
                                              int maxTeamSize);
}