package live.pvphard.practice.feature.cosmetic.internal.repository.impl.suit;

import live.pvphard.practice.common.constants.TexturesConstant;
import live.pvphard.practice.common.item.ItemBuilder;
import live.pvphard.practice.feature.cosmetic.annotation.CosmeticData;
import live.pvphard.practice.feature.cosmetic.model.CosmeticType;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Remi
 * @project hard-practice
 * @date 6/08/2025
 */
@CosmeticData(
        type = CosmeticType.SUIT,
        name = "Monkey",
        description = "Unleash your inner primate as a monkey.",
        icon = Material.VINE,
        slot = 19,
        price = 1000
)
public class MonkeySuit extends BaseSuit {
    @Override
    public Map<EquipmentSlot, ItemStack> getArmorPieces() {
        Map<EquipmentSlot, ItemStack> armorPieces = new HashMap<>();

        ItemStack monkeyHead = new ItemBuilder(Material.SKULL_ITEM)
                .durability(3)
                .setSkullTexture(TexturesConstant.MONKEY_SKIN)
                .build();
        armorPieces.put(EquipmentSlot.HEAD, monkeyHead);

        Color brown = Color.fromRGB(139, 69, 19);
        armorPieces.put(EquipmentSlot.CHEST, createColoredArmor(Material.LEATHER_CHESTPLATE, brown));
        armorPieces.put(EquipmentSlot.LEGS, createColoredArmor(Material.LEATHER_LEGGINGS, brown));
        armorPieces.put(EquipmentSlot.FEET, createColoredArmor(Material.LEATHER_BOOTS, brown));

        return armorPieces;
    }

    @Override
    public Map<PotionEffectType, Integer> getPassiveEffects() {
        Map<PotionEffectType, Integer> effects = new HashMap<>();
        effects.put(PotionEffectType.SPEED, 2);
        effects.put(PotionEffectType.JUMP, 2);
        return effects;
    }
}
