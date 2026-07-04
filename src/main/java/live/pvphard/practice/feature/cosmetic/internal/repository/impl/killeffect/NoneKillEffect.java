package live.pvphard.practice.feature.cosmetic.internal.repository.impl.killeffect;

import live.pvphard.practice.feature.cosmetic.model.CosmeticType;
import live.pvphard.practice.feature.cosmetic.annotation.CosmeticData;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * @author Remi
 * @project Hard
 * @date 01/06/2024
 */
@CosmeticData(type = CosmeticType.KILL_EFFECT, name = "None", description = "Remova seu efeito de kill", icon = Material.BARRIER, slot = 10)
public class NoneKillEffect extends BaseKillEffect {
    @Override
    public void execute(Player player) {

    }
}
