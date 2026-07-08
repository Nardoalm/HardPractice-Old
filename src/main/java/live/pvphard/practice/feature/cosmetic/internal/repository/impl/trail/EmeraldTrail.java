package live.pvphard.practice.feature.cosmetic.internal.repository.impl.trail;

import live.pvphard.practice.feature.cosmetic.annotation.CosmeticData;
import live.pvphard.practice.feature.cosmetic.model.CosmeticType;
import org.bukkit.Material;

@CosmeticData(
        type = CosmeticType.TRAIL,
        name = "Emerald Trail",
        description = "A vibrant emerald block trail",
        icon = Material.EMERALD_BLOCK,
        slot = 13,
        price = 1500
)
public class EmeraldTrail extends Trail {
    @Override
    public Material getBlockMaterial() {
        return Material.EMERALD_BLOCK;
    }

    @Override
    public byte getBlockData(int ticks) {
        return 0;
    }
}
