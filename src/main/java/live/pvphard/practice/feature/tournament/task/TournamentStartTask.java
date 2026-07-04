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
public class TournamentStartTask implements Runnable {
    private final Tournament tournament;
    private final TournamentEngine engine;
    private final TournamentCountdownService countdowns;

    private int countdown = 20;

    public TournamentStartTask(Tournament tournament) {
        this.tournament = tournament;
        this.engine = HardPractice.getInstance().getService(TournamentEngine.class);
        this.countdowns = HardPractice.getInstance().getService(TournamentCountdownService.class);
    }

    @Override
    public void run() {
        if (countdown <= 0) {
            if (tournament.getStartingTask() != null) {
                tournament.getStartingTask().cancel();
                tournament.setStartingTask(null);
            }
            countdowns.clearStartTask();
            engine.processEvent(tournament, new TournamentEvent.StartCountdownFinished());
            return;
        }

        if (countdown <= 5 || countdown % 5 == 0 || countdown >= 20) {
            String message = CC.translate("&bRound 1 &fstarts in &b" + countdown + "&f.");
            tournament
                    .getAllPlayers()
                    .forEach(
                            p -> {
                                p.sendMessage(message);
                                p.playSound(p.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
                            });
        }

        countdown--;
    }
}