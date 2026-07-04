package live.pvphard.practice.feature.cosmetic.internal.repository;

import live.pvphard.practice.feature.cosmetic.internal.repository.impl.cloak.*;

/**
 * @author Remi
 * @project hard-practice
 * @date 4/08/2025
 */
public class CloakRepository extends BaseCosmeticRepository<BaseCloak> {
    public CloakRepository() {
        registerCosmetic(NoneCloak.class);
        registerCosmetic(AngelWingsCloak.class);
        registerCosmetic(HaloCloak.class);
        registerCosmetic(VailCloak.class);
    }
}
