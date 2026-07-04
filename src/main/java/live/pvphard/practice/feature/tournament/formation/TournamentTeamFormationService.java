package live.pvphard.practice.feature.tournament.formation;

import live.pvphard.practice.bootstrap.lifecycle.Service;
import live.pvphard.practice.feature.tournament.model.Tournament;
import live.pvphard.practice.feature.tournament.model.TournamentParticipant;

import java.util.List;

public interface TournamentTeamFormationService extends Service {
    /**
     * Forms balanced teams for a tournament.
     *
     * @param tournament The tournament to form teams for
     * @return List of formed teams
     */
    List<TournamentParticipant> formTeamsForTournament(Tournament tournament);
}