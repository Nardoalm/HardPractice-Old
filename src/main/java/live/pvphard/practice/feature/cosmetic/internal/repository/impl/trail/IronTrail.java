package live.pvphard.practice.feature.cosmetic.internal.repository.impl.trail;

import live.pvphard.practice.feature.cosmetic.annotation.CosmeticData;
import live.pvphard.practice.feature.cosmetic.model.CosmeticType;
import org.bukkit.Material;

@CosmeticData(
        type = CosmeticType.TRAIL,
        name = "Iron Trail",
        description = "An iron block trail",
        icon = Material.IRON_BLOCK,
        slot = 15,
        price = 500
)
public class IronTrail extends Trail {
    @Override
    public Material getBlockMaterial() {
        return Material.IRON_BLOCK;
    }

    @Override
    public byte getBlockData(int ticks) {
        return 0;
    }
}
