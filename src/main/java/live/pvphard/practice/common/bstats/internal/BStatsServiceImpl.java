package live.pvphard.practice.common.bstats.internal;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.bootstrap.HardContext;
import live.pvphard.practice.bootstrap.annotation.Service;
import live.pvphard.practice.common.bstats.BStatsService;
import org.bstats.bukkit.Metrics;

/**
 * @author Remi
 * @project hard-practice
 * @date 27/07/2025
 */
@Service(provides = BStatsService.class, priority = 1)
public class BStatsServiceImpl implements BStatsService {
    @Override
    public void setup(HardContext context) {
        Metrics metrics = new Metrics(HardPractice.getInstance(), 26678);
    }
}
