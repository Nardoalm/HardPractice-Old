package live.pvphard.practice.feature.tournament.formation.strategy;


import live.pvphard.practice.feature.tournament.formation.TeamFormationStrategy;
import live.pvphard.practice.feature.tournament.formation.assembly.TeamAssembler;
import live.pvphard.practice.feature.tournament.formation.assembly.internal.PartyAwareTeamAssembler;
import live.pvphard.practice.feature.tournament.formation.distribution.TeamDistributionCalculator;
import live.pvphard.practice.feature.tournament.formation.distribution.internal.OptimalDistributionCalculator;
import live.pvphard.practice.feature.tournament.formation.model.TeamDistribution;
import live.pvphard.practice.feature.tournament.model.TournamentParticipant;

import java.util.List;

public class BalancedTeamFormationStrategy implements TeamFormationStrategy {
    private final TeamDistributionCalculator distributionCalculator;
    private final TeamAssembler teamAssembler;

    public BalancedTeamFormationStrategy() {
        this.distributionCalculator = new OptimalDistributionCalculator();
        this.teamAssembler = new PartyAwareTeamAssembler();
    }

    @Override
    public List<TournamentParticipant> formTeams(List<TournamentParticipant> participantPool, int maxTeamSize) {
        TeamDistribution distribution = distributionCalculator.calculateDistribution(participantPool, maxTeamSize);

        return teamAssembler.assembleTeams(participantPool, distribution, maxTeamSize);
    }
}