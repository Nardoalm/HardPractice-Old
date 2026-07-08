package live.pvphard.practice.feature.cosmetic.internal.repository.impl.trail;

import live.pvphard.practice.feature.cosmetic.annotation.CosmeticData;
import live.pvphard.practice.feature.cosmetic.model.CosmeticType;
import org.bukkit.Material;

@CosmeticData(
        type = CosmeticType.TRAIL,
        name = "Rainbow Trail",
        description = "A colorful rainbow wool trail",
        icon = Material.WOOL,
        slot = 11,
        price = 1000
)
public class RainbowTrail extends Trail {
    private static final byte[] WOOL_COLORS = {14, 1, 4, 5, 9, 3, 11, 10, 2, 6};

    @Override
    public Material getBlockMaterial() {
        return Material.WOOL;
    }

    @Override
    public byte getBlockData(int ticks) {
        int index = ticks % WOOL_COLORS.length;
        if (index < 0) index = 0;
        return WOOL_COLORS[index];
    }
}
