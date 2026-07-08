package live.pvphard.practice.feature.cosmetic.internal.repository.impl.trail;

import live.pvphard.practice.feature.cosmetic.annotation.CosmeticData;
import live.pvphard.practice.feature.cosmetic.model.CosmeticType;
import org.bukkit.Material;

@CosmeticData(
        type = CosmeticType.TRAIL,
        name = "None",
        description = "Remove your trail",
        icon = Material.BARRIER,
        slot = 10,
        price = 0
)
public class NoneTrail extends Trail {
    @Override
    public Material getBlockMaterial() {
        return null;
    }

    @Override
    public byte getBlockData(int ticks) {
        return 0;
    }
}
