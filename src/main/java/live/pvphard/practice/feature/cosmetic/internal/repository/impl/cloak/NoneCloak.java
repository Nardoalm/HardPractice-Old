package live.pvphard.practice.feature.cosmetic.internal.repository.impl.cloak;

import live.pvphard.practice.feature.cosmetic.annotation.CosmeticData;
import live.pvphard.practice.feature.cosmetic.model.CosmeticType;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * @author Remi
 * @project hard-practice
 * @date 4/08/2025
 */
@CosmeticData(
        type = CosmeticType.CLOAK,
        name = "None",
        description = "Remover your cloak",
        icon = Material.BARRIER,
        slot = 10
)
public class NoneCloak extends BaseCloak{
    @Override
    public void render(Player player) {

    }
}
