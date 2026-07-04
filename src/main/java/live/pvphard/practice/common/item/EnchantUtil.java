package live.pvphard.practice.common.item;

import lombok.experimental.UtilityClass;
import org.bukkit.enchantments.Enchantment;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Emmy
 * @project Hard
 * @date 28/05/2024 - 20:53
 */
@UtilityClass
public class EnchantUtil {
    private final Map<String, Enchantment> enchantment = new HashMap<>();

    static {
        enchantment.put("sharpness", Enchantment.DAMAGE_ALL);
        enchantment.put("efficiency", Enchantment.DIG_SPEED);
        enchantment.put("unbreaking", Enchantment.DURABILITY);
        enchantment.put("fortune", Enchantment.LOOT_BONUS_BLOCKS);
        enchantment.put("power", Enchantment.ARROW_DAMAGE);
        enchantment.put("punch", Enchantment.ARROW_KNOCKBACK);
        enchantment.put("flame", Enchantment.ARROW_FIRE);
        enchantment.put("infinity", Enchantment.ARROW_INFINITE);
        enchantment.put("knockback", Enchantment.KNOCKBACK);
        enchantment.put("protection", Enchantment.PROTECTION_ENVIRONMENTAL);
        enchantment.put("fire_protection", Enchantment.PROTECTION_FIRE);
        enchantment.put("blast_protection", Enchantment.PROTECTION_EXPLOSIONS);
        enchantment.put("projectile_protection", Enchantment.PROTECTION_PROJECTILE);
        enchantment.put("thorns", Enchantment.THORNS);
        enchantment.put("respiration", Enchantment.OXYGEN);
        enchantment.put("aqua_affinity", Enchantment.WATER_WORKER);
        enchantment.put("depth_strider", Enchantment.DEPTH_STRIDER);
        enchantment.put("smite", Enchantment.DAMAGE_UNDEAD);
        enchantment.put("bane_of_arthropods", Enchantment.DAMAGE_ARTHROPODS);
        enchantment.put("fire_aspect", Enchantment.FIRE_ASPECT);
        enchantment.put("looting", Enchantment.LOOT_BONUS_MOBS);
        enchantment.put("silk_touch", Enchantment.SILK_TOUCH);
        enchantment.put("luck_of_the_sea", Enchantment.LUCK);
        enchantment.put("lure", Enchantment.LURE);
    }

    /**
     * Get an enchantment by name.
     *
     * @param name The name of the enchantment.
     * @return The enchantment.
     */
    public Enchantment getEnchantment(String name) {
        return enchantment.get(name);
    }

    /**
     * Get a list of all enchantments.
     *
     * @return The list of enchantments.
     */
    public String getSortedEnchantments() {
        return String.join(", ", enchantment.keySet());
    }
}