package live.pvphard.practice.feature.tournament.formation.assembly.internal;

import live.pvphard.practice.feature.tournament.formation.assembly.PartyMatcher;
import live.pvphard.practice.feature.tournament.formation.assembly.TeamAssembler;
import live.pvphard.practice.feature.tournament.formation.model.TeamDistribution;
import live.pvphard.practice.feature.tournament.model.TournamentParticipant;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PartyAwareTeamAssembler implements TeamAssembler {
    private final PartyMatcher partyMatcher;

    public PartyAwareTeamAssembler() {
        this.partyMatcher = new BestFitPartyMatcher();
    }

    @Override
    public List<TournamentParticipant> assembleTeams(List<TournamentParticipant> participantPool,
                                                     TeamDistribution distribution,
                                                     int maxTeamSize) {
        List<TournamentParticipant> teams = new ArrayList<>();
        List<TournamentParticipant> availableParties = new ArrayList<>(participantPool);

        availableParties.sort(Comparator.comparingInt(TournamentParticipant::getSize).reversed());

        for (int targetSize : distribution.getTeamSizes()) {
            TournamentParticipant team = partyMatcher.createTeamOfSize(availableParties, targetSize, maxTeamSize);
            if (team != null) {
                teams.add(team);
            }
        }

        return teams;
    }
}