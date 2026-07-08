package live.pvphard.practice.feature.cosmetic.internal.repository.impl.trail;

import live.pvphard.practice.feature.cosmetic.annotation.CosmeticData;
import live.pvphard.practice.feature.cosmetic.model.CosmeticType;
import org.bukkit.Material;

@CosmeticData(
        type = CosmeticType.TRAIL,
        name = "Redstone Trail",
        description = "A glowing redstone block trail",
        icon = Material.REDSTONE_BLOCK,
        slot = 16,
        price = 800
)
public class RedstoneTrail extends Trail {
    @Override
    public Material getBlockMaterial() {
        return Material.REDSTONE_BLOCK;
    }

    @Override
    public byte getBlockData(int ticks) {
        return 0;
    }
}
