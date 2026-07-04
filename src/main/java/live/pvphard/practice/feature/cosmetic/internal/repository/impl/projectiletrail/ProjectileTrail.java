package live.pvphard.practice.feature.cosmetic.internal.repository.impl.projectiletrail;

import live.pvphard.practice.feature.cosmetic.model.BaseCosmetic;
import org.bukkit.Location;

/**
 * @author Remi
 * @project Hard
 * @date 6/23/2025
 */
public abstract class ProjectileTrail extends BaseCosmetic {
    /**
     * This method is called repeatedly by a trail-tracking task.
     * Implementations should define what particle to spawn at the projectile's location.
     *
     * @param location The current location of the projectile.
     */
    public abstract void spawnTrailParticle(Location location);
}