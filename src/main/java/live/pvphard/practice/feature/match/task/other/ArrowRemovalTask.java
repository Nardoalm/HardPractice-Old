package live.pvphard.practice.feature.match.task.other;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Emmy
 * @project Hard
 * @date 15/09/2024 - 19:24
 */
public class ArrowRemovalTask extends BukkitRunnable {
    @Override
    public void run() {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Arrow) {
                    Arrow arrow = (Arrow) entity;
                    if (arrow.isOnGround()) {
                        arrow.remove();
                    }
                }
            }
        }
    }
}