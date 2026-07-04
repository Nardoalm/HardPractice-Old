package live.pvphard.practice.feature.cosmetic.internal.repository.impl.killeffect;

import live.pvphard.practice.feature.cosmetic.model.BaseCosmetic;
import org.bukkit.entity.Player;

/**
 * @author Remi
 * @project hard-practice
 * @date 6/08/2025
 */
public abstract class BaseKillEffect extends BaseCosmetic {
    /**
     * Executes the kill effect for the specified player.
     * This method is called when the player gets a kill.
     *
     * @param player The player who executed the kill.
     */
    public abstract void execute(Player player);
}