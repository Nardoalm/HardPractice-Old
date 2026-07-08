package live.pvphard.practice.feature.cosmetic.internal.repository.impl.trail;

import live.pvphard.practice.feature.cosmetic.annotation.CosmeticData;
import live.pvphard.practice.feature.cosmetic.model.CosmeticType;
import org.bukkit.Material;

@CosmeticData(
        type = CosmeticType.TRAIL,
        name = "Gold Trail",
        description = "A golden block trail",
        icon = Material.GOLD_BLOCK,
        slot = 14,
        price = 800
)
public class GoldTrail extends Trail {
    @Override
    public Material getBlockMaterial() {
        return Material.GOLD_BLOCK;
    }

    @Override
    public byte getBlockData(int ticks) {
        return 0;
    }
}
