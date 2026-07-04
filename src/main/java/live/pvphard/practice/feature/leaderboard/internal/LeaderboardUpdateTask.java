package live.pvphard.practice.feature.leaderboard.internal;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.feature.hologram.manager.HologramManager;
import live.pvphard.practice.feature.hologram.leaderboard.KitLeaderboardHologram;
import org.bukkit.scheduler.BukkitRunnable;

public class LeaderboardUpdateTask extends BukkitRunnable {

    @Override
    public void run() {
        HologramManager manager = HardPractice.getInstance().getService(HologramManager.class);
        if (manager == null) return;

        for (KitLeaderboardHologram holo : manager.getActiveHolograms()) {
            holo.update();
        }
    }
}