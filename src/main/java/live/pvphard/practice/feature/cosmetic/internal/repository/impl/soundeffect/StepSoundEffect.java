package live.pvphard.practice.feature.cosmetic.internal.repository.impl.soundeffect;

import live.pvphard.practice.feature.cosmetic.model.CosmeticType;
import live.pvphard.practice.feature.cosmetic.annotation.CosmeticData;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * @author Remi
 * @project Hard
 * @date 01/06/2024
 */
@CosmeticData(type = CosmeticType.SOUND_EFFECT, name = "Step", description = "Play step sound upon kill", permission = "step", icon = Material.REDSTONE, slot = 11)
public class StepSoundEffect extends BaseSoundEffect {
    @Override
    public void execute(Player player) {
        player.playEffect(player.getLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
    }
}
