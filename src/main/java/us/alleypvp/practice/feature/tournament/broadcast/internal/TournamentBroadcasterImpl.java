package us.alleypvp.practice.feature.tournament.broadcast.internal;


import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import us.alleypvp.practice.bootstrap.annotation.Service;
import us.alleypvp.practice.common.text.CC;
import us.alleypvp.practice.feature.tournament.broadcast.BroadcastEvent;
import us.alleypvp.practice.feature.tournament.broadcast.TournamentBroadcaster;
import us.alleypvp.practice.feature.tournament.broadcast.TournamentPlacements;
import us.alleypvp.practice.feature.tournament.internal.helpers.TournamentMessageBuilder;
import us.alleypvp.practice.feature.tournament.model.Tournament;
import us.alleypvp.practice.feature.tournament.model.TournamentParticipant;

/**
 * @author Remi
 * @project alley-practice
 * @date 8/08/2025
 */
@Service(provides = TournamentBroadcaster.class, priority = 1100)
public class TournamentBroadcasterImpl implements TournamentBroadcaster {
    @Override
    public void broadcast(BroadcastEvent event) {
        if (event instanceof BroadcastEvent.TournamentHosted) {
            handleTournamentHosted((BroadcastEvent.TournamentHosted) event);
        } else if (event instanceof BroadcastEvent.ParticipantJoined) {
            handleParticipantJoined((BroadcastEvent.ParticipantJoined) event);
        } else if (event instanceof BroadcastEvent.RoundStarting) {
            handleRoundStarting((BroadcastEvent.RoundStarting) event);
        } else if (event instanceof BroadcastEvent.ParticipantEliminated) {
            handleParticipantEliminated((BroadcastEvent.ParticipantEliminated) event);
        }
    }

    @Override
    public void sendTargetedMessage(Tournament tournament, Iterable<TournamentParticipant> participants, String message) {
        String translated = CC.translate(message);
        for (TournamentParticipant participant : participants) {
            for (Player player : participant.getOnlinePlayers()) {
                player.sendMessage(translated);
            }
        }
    }

    @Override
    public void broadcastResults(Tournament tournament, TournamentPlacements placements) {
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(CC.translate("&b&lTournament &7(" + tournament.getDisplayName() + ")"));
        Bukkit.broadcastMessage(
                CC.translate(
                        " &b│ &f"
                                + tournament.getKit()
                                + " &7(&b"
                                + tournament.getInitialPlayerCount()
                                + "&7)"));

        String[] prefixes = {"&b&l✫1", "&7&l✫2", "&c&l✫3"};

        int limit = Math.min(3, placements.getOrderedPlacements().size());
        for (int i = 0; i < limit; i++) {
            TournamentParticipant participant = placements.getPlacement(i + 1);
            if (participant != null) {
                String teamNameString = TournamentMessageBuilder.getNaturalTeamNameListWithProfileColors(participant);
                Bukkit.broadcastMessage(CC.translate("   " + prefixes[i] + " " + teamNameString));
            }
        }
        Bukkit.broadcastMessage("");

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.FIREWORK_BLAST, 1.0f, 1.0f);
        }
    }

    /**
     * Handles global broadcast for a newly hosted tournament.
     *
     * @param event The tournament hosted event.
     */
    private void handleTournamentHosted(BroadcastEvent.TournamentHosted event) {
        Tournament tournament = event.getTournament();
        String message =
                CC.translate(
                        "&b"
                                + tournament.getHostName()
                                + " &fhosted a &b"
                                + tournament.getDisplayName()
                                + " "
                                + tournament.getKit()
                                + " &ftournament! &7(/tournament join "
                                + tournament.getNumericId()
                                + ")");
        Bukkit.broadcastMessage(message);
    }

    /**
     * Handles waiting-pool broadcast when a participant joins.
     *
     * @param event The participant joined event.
     */
    private void handleParticipantJoined(BroadcastEvent.ParticipantJoined event) {
        String joinMessage = TournamentMessageBuilder.generateParticipantBroadcast(
                event.getParticipant(), "has joined", "have joined");

        sendTargetedMessage(event.getTournament(), event.getTournament().getWaitingPool(), joinMessage);

        int currentPlayers = event.getTournament().getWaitingPool().stream()
                .mapToInt(TournamentParticipant::getSize)
                .sum();
        int maxPlayers = event.getTournament().getMaxTeams() * event.getTournament().getTeamSize();
        if (currentPlayers >= maxPlayers) {
            sendTargetedMessage(
                    event.getTournament(),
                    event.getTournament().getWaitingPool(),
                    "&aThe tournament is now full and will begin shortly!");
        }
    }

    /**
     * Handles per-round broadcast when a round starts.
     *
     * @param event The round starting event.
     */
    private void handleRoundStarting(BroadcastEvent.RoundStarting event) {
        String message = "&bRound " + event.getRoundNumber() + " &fhas started!";
        sendTargetedMessage(event.getTournament(), event.getTournament().getParticipants(), message);

        event.getTournament().getAllPlayers()
                .forEach(player -> player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0f, 1.5f));
    }

    /**
     * Handles broadcast when a participant is eliminated.
     *
     * @param event The eliminated event.
     */
    private void handleParticipantEliminated(BroadcastEvent.ParticipantEliminated event) {
        Tournament tournament = event.getTournament();
        TournamentParticipant loser = event.getEliminated();
        TournamentParticipant winner = event.getWinner();

        String loserNames = TournamentMessageBuilder.getNaturalTeamNameListWithProfileColors(loser);
        String verb = loser.getSize() > 1 ? "were" : "was";

        int remainingPlayers = tournament.getRoundParticipants().stream().mapToInt(TournamentParticipant::getSize).sum();
        int initial = tournament.getInitialPlayerCount();

        String message;
        if (winner != null) {
            String winnerNames = TournamentMessageBuilder.getNaturalTeamNameListWithProfileColors(winner);
            message = String.format(
                    "&c%s &f%s eliminated by %s&f. (&b%d&f/&b%d&f)",
                    loserNames, verb, winnerNames, remainingPlayers, initial
            );
        } else {
            message = String.format(
                    "&c%s &f%s eliminated. (&b%d&f/&b%d&f)",
                    loserNames, verb, remainingPlayers, initial
            );
        }

        sendTournamentMessage(tournament, message);
    }

    private void sendTournamentMessage(Tournament tournament, String message) {
        String translated = CC.translate(message);
        tournament.getAllPlayers().forEach(player -> player.sendMessage(translated));
    }
}