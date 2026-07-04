package live.pvphard.practice.common;

import lombok.experimental.UtilityClass;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffectType;

/**
 * @date 03/11/2024 - 20:56
 */
@UtilityClass
public class PotionUtil {
    /**
     * Get the potion effect type of item stack.
     *
     * @param item The item stack.
     * @return The potion effect type.
     */
    public PotionEffectType getPotionEffectType(ItemStack item) {
        Potion potion = Potion.fromItemStack(item);
        if (!potion.getEffects().isEmpty()) {
            return potion.getEffects().iterator().next().getType();
        }
        return null;
    }

    /**
     * Get the potion effect amplifier of an item stack.
     *
     * @param item The item stack.
     * @return The potion effect amplifier.
     */
    public int getPotionEffectAmplifier(ItemStack item) {
        Potion potion = Potion.fromItemStack(item);
        if (!potion.getEffects().isEmpty()) {
            return potion.getEffects().iterator().next().getAmplifier();
        }
        return 0;
    }
}