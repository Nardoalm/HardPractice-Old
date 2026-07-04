package live.pvphard.practice.feature.cosmetic.internal.repository;

import live.pvphard.practice.feature.cosmetic.internal.repository.impl.soundeffect.BaseSoundEffect;
import live.pvphard.practice.feature.cosmetic.internal.repository.impl.soundeffect.ExplosionSoundEffect;
import live.pvphard.practice.feature.cosmetic.internal.repository.impl.soundeffect.NoneSoundEffect;
import live.pvphard.practice.feature.cosmetic.internal.repository.impl.soundeffect.StepSoundEffect;
import lombok.Getter;

/**
 * @author Remi
 * @project Hard
 * @date 01/06/2024
 */
@Getter
public class SoundEffectRepository extends BaseCosmeticRepository<BaseSoundEffect> {
    public SoundEffectRepository() {
        this.registerCosmetic(ExplosionSoundEffect.class);
        this.registerCosmetic(NoneSoundEffect.class);
        this.registerCosmetic(StepSoundEffect.class);
    }
}