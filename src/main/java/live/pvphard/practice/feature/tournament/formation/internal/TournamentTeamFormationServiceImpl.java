package live.pvphard.practice.feature.tournament.formation.internal;


import live.pvphard.practice.bootstrap.annotation.Service;
import live.pvphard.practice.feature.tournament.formation.TeamFormationStrategy;
import live.pvphard.practice.feature.tournament.formation.TeamFormationStrategyFactory;
import live.pvphard.practice.feature.tournament.formation.TournamentTeamFormationService;
import live.pvphard.practice.feature.tournament.model.Tournament;
import live.pvphard.practice.feature.tournament.model.TournamentParticipant;

import java.util.ArrayList;
import java.util.List;

@Service(provides = TournamentTeamFormationService.class, priority = 1100)
public class TournamentTeamFormationServiceImpl implements TournamentTeamFormationService {
    private final TeamFormationStrategy teamFormationStrategy;

    public TournamentTeamFormationServiceImpl() {
        // todo: make type configurable in config
        this.teamFormationStrategy = TeamFormationStrategyFactory.createStrategy(
                TeamFormationStrategyFactory.StrategyType.BALANCED
        );
    }

    @Override
    public List<TournamentParticipant> formTeamsForTournament(Tournament tournament) {
        List<TournamentParticipant> participantPool = new ArrayList<>(tournament.getWaitingPool());
        int maxTeamSize = tournament.getTeamSize();

        return teamFormationStrategy.formTeams(participantPool, maxTeamSize);
    }
}