package live.pvphard.practice.feature.cosmetic.internal.repository.impl.killmessage;

import live.pvphard.practice.feature.cosmetic.model.CosmeticType;
import live.pvphard.practice.feature.cosmetic.annotation.CosmeticData;
import org.bukkit.Material;

/**
 * @author Remi
 * @project hard-practice
 * @date 27/06/2025
 */
@CosmeticData(
        type = CosmeticType.KILL_MESSAGE,
        name = "None",
        description = "Remover your kill message",
        icon = Material.BARRIER,
        slot = 10,
        price = 0
)
public class NoneKillMessages extends KillMessagePack {
    @Override
    protected String getResourceFileName() {
        return null;
    }
}