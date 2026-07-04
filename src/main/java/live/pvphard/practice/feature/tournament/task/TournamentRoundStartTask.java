package live.pvphard.practice.feature.tournament.task;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.feature.tournament.engine.TournamentEngine;
import live.pvphard.practice.feature.tournament.engine.TournamentEvent;
import live.pvphard.practice.feature.tournament.model.Tournament;
import lombok.Getter;
import org.bukkit.Sound;

/**
 * @author Remi
 * @project hard-practice
 * @date 6/08/2025
 */
@Getter
public class TournamentRoundStartTask implements Runnable {
    private final Tournament tournament;
    private final TournamentEngine engine;
    private final TournamentCountdownService countdowns;
    private int countdown = 20;

    public TournamentRoundStartTask(Tournament tournament) {
        this.tournament = tournament;
        this.engine = HardPractice.getInstance().getService(TournamentEngine.class);
        this.countdowns =
                HardPractice.getInstance().getService(TournamentCountdownService.class);
    }

    @Override
    public void run() {
        if (countdown <= 0) {
            if (tournament.getRoundStartTask() != null) {
                tournament.getRoundStartTask().cancel();
                tournament.setRoundStartTask(null);
            }

            countdowns.clearRoundStartTask();
            engine.processEvent(tournament, new TournamentEvent.RoundCountdownFinished());
            return;
        }

        if (countdown <= 5 || countdown % 5 == 0) {
            String message = CC.translate("&bRound " + tournament.getCurrentRound() + " &fstarts in &b" + countdown + "&f.");
            tournament.getAllPlayers().forEach(player ->
            {
                player.sendMessage(message);
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0f, 1.2f);
            });
        }

        countdown--;
    }
}