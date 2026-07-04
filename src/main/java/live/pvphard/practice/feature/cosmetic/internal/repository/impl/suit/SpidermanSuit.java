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
 * @date 4/08/2025
 */
@CosmeticData(
        type = CosmeticType.SUIT,
        name = "Spiderman",
        description = "Seu amigável Homem-Aranha da vizinhança!",
        icon = Material.WEB,
        slot = 12,
        price = 1000
)
public class SpidermanSuit extends BaseSuit {
    @Override
    public Map<EquipmentSlot, ItemStack> getArmorPieces() {
        Map<EquipmentSlot, ItemStack> armorPieces = new HashMap<>();

        ItemStack spidermanHead = new ItemBuilder(Material.SKULL_ITEM)
                .durability(3)
                .setSkullTexture(TexturesConstant.SPIDERMAN_SKIN)
                .build();
        armorPieces.put(EquipmentSlot.HEAD, spidermanHead);
        armorPieces.put(EquipmentSlot.CHEST, createColoredArmor(Material.LEATHER_CHESTPLATE, Color.RED));
        armorPieces.put(EquipmentSlot.LEGS, createColoredArmor(Material.LEATHER_LEGGINGS, Color.BLUE));
        armorPieces.put(EquipmentSlot.FEET, createColoredArmor(Material.LEATHER_BOOTS, Color.RED));

        return armorPieces;
    }

    @Override
    public Map<PotionEffectType, Integer> getPassiveEffects() {
        Map<PotionEffectType, Integer> effects = new HashMap<>();
        effects.put(PotionEffectType.SPEED, 0);
        effects.put(PotionEffectType.JUMP, 1);
        return effects;
    }
}
