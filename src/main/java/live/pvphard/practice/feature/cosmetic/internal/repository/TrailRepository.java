package live.pvphard.practice.feature.cosmetic.internal.repository;

import live.pvphard.practice.feature.cosmetic.internal.repository.impl.trail.*;

public class TrailRepository extends BaseCosmeticRepository<Trail> {
    public TrailRepository() {
        this.registerCosmetic(NoneTrail.class);
        this.registerCosmetic(RainbowTrail.class);
        this.registerCosmetic(DiamondTrail.class);
        this.registerCosmetic(EmeraldTrail.class);
        this.registerCosmetic(GoldTrail.class);
        this.registerCosmetic(IronTrail.class);
        this.registerCosmetic(RedstoneTrail.class);
    }
}
