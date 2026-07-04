package live.pvphard.practice.feature.cosmetic.internal.repository;

import live.pvphard.practice.feature.cosmetic.internal.repository.impl.killmessage.*;

/**
 * @author Remi
 * @project hard-practice
 * @date 27/06/2025
 */
public class KillMessageRepository extends BaseCosmeticRepository<KillMessagePack> {
    public KillMessageRepository() {
        this.registerCosmetic(NoneKillMessages.class);
        this.registerCosmetic(SaltyKillMessages.class);
        this.registerCosmetic(YeetKillMessages.class);
        this.registerCosmetic(NerdKillMessages.class);
        this.registerCosmetic(SpigotCommunityKillMessages.class);
    }
}
