package live.pvphard.practice.feature.cosmetic.internal.repository.impl.trail;

import live.pvphard.practice.feature.cosmetic.model.BaseCosmetic;
import live.pvphard.practice.feature.cosmetic.model.CosmeticType;
import org.bukkit.Material;

public abstract class Trail extends BaseCosmetic {
    public Trail() {
        super();
    }

    public abstract Material getBlockMaterial();
    
    public abstract byte getBlockData(int ticks);
}
