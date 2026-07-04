package live.pvphard.practice.feature.cosmetic.internal.repository;

import live.pvphard.practice.feature.cosmetic.internal.repository.impl.projectiletrail.ProjectileTrail;
import live.pvphard.practice.feature.cosmetic.internal.repository.impl.projectiletrail.FlameTrail;

/**
 * @author Remi
 * @project Hard
 * @date 6/23/2025
 */
public class ProjectileTrailRepository extends BaseCosmeticRepository<ProjectileTrail> {
    public ProjectileTrailRepository() {
        this.registerCosmetic(FlameTrail.class);
    }
}