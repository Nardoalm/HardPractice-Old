package live.pvphard.practice.feature.cosmetic.task;

import live.pvphard.practice.feature.cosmetic.listener.TrailListener;
import live.pvphard.practice.feature.cosmetic.listener.TrailListener.TrailBlockState;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.Map;

public class TrailTask extends BukkitRunnable {
    private static final long DECAY_DURATION_MS = 1000L; // 0.8 seconds decay

    @Override
    public void run() {
        if (TrailListener.ACTIVE_TRAILS.isEmpty()) {
            return;
        }

        long now = System.currentTimeMillis();
        Iterator<Map.Entry<Location, TrailBlockState>> iterator = TrailListener.ACTIVE_TRAILS.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Location, TrailBlockState> entry = iterator.next();
            Location loc = entry.getKey();
            TrailBlockState state = entry.getValue();

            Block block = loc.getBlock();

            // Revert if past decay duration
            if (now - state.timestamp >= DECAY_DURATION_MS) {
                if (block.getType() == state.trail.getBlockMaterial()) {
                    block.setType(state.originalMaterial);
                    block.setData(state.originalData);
                }
                iterator.remove();
            } else {
                // Update dynamic trail states (e.g. rainbow color cycling)
                state.ticks++;
                if (block.getType() == state.trail.getBlockMaterial()) {
                    block.setData(state.trail.getBlockData(state.ticks));
                }
            }
        }
    }
}
