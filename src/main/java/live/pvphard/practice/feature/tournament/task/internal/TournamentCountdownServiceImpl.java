package live.pvphard.practice.feature.tournament.task.internal;

import live.pvphard.practice.bootstrap.annotation.Service;
import live.pvphard.practice.feature.tournament.task.TournamentCountdownService;
import live.pvphard.practice.feature.tournament.task.TournamentRoundStartTask;
import live.pvphard.practice.feature.tournament.task.TournamentStartTask;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Remi
 * @project hard-practice
 * @date 8/08/2025
 */
@Setter
@Getter
@Service(provides = TournamentCountdownService.class, priority = 1100)
public class TournamentCountdownServiceImpl implements TournamentCountdownService {
    private volatile TournamentStartTask startTask;
    private volatile TournamentRoundStartTask roundStartTask;

    @Override
    public void clearStartTask() {
        this.startTask = null;
    }

    @Override
    public void clearRoundStartTask() {
        this.roundStartTask = null;
    }
}
