package live.pvphard.practice.feature.cosmetic.internal.repository;

import live.pvphard.practice.feature.cosmetic.internal.repository.impl.killeffect.*;
import lombok.Getter;

/**
 * @author Remi
 * @project Hard
 * @date 01/06/2024
 */
@Getter
public class KillEffectRepository extends BaseCosmeticRepository<BaseKillEffect> {
    public KillEffectRepository() {
        this.registerCosmetic(BloodKillEffect.class);
        this.registerCosmetic(ExplosionKillEffect.class);
        this.registerCosmetic(FireworkKillEffect.class);
        this.registerCosmetic(HeartKillEffect.class);
        this.registerCosmetic(NoneKillEffect.class);
        this.registerCosmetic(ThunderKillEffect.class);
    }
}
