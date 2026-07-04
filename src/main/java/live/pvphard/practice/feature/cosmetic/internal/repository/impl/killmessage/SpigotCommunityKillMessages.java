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
        name = "Spigot Community Messages",
        description = "A dive into the spigot community.",
        icon = Material.GOLD_NUGGET,
        slot = 14,
        price = 750
)
public class SpigotCommunityKillMessages extends KillMessagePack {
    @Override
    protected String getResourceFileName() {
        return "spigot_community_messages.yml";
    }
}