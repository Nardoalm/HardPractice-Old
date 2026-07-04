package live.pvphard.practice.feature.cosmetic.internal.repository.impl.suit;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.feature.cosmetic.CosmeticService;
import live.pvphard.practice.feature.cosmetic.internal.repository.SuitRepository;
import live.pvphard.practice.feature.cosmetic.model.BaseCosmetic;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;

/**
 * @author Remi
 * @project hard-practice
 * @date 4/08/2025
 */
public abstract class BaseSuit extends BaseCosmetic {
    /**
     * Concrete suit classes must implement this method to provide their armor pieces.
     *
     * @return A map of equipment slots to ItemStacks representing the armor pieces.
     */
    public abstract Map<EquipmentSlot, ItemStack> getArmorPieces();

    /**
     * Concrete suit classes must implement this method to provide their passive effects.
     *
     * @return A map of PotionEffectTypes to their amplifier levels.
     */
    public abstract Map<PotionEffectType, Integer> getPassiveEffects();

    /**
     * Equips the suit by setting the armor pieces in the player's inventory.
     *
     * @param player The player whose armor will be equipped.
     */
    public void equip(Player player) {
        PlayerInventory inventory = player.getInventory();
        getArmorPieces().forEach((slot, item) -> {
            switch (slot) {
                case FEET:
                    inventory.setBoots(item);
                    break;
                case LEGS:
                    inventory.setLeggings(item);
                    break;
                case CHEST:
                    inventory.setChestplate(item);
                    break;
                case HEAD:
                    inventory.setHelmet(item);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported equipment slot: " + slot);
            }
        });

        getPassiveEffects().forEach((type, amplifier) -> {
            PotionEffect effect = new PotionEffect(type, Integer.MAX_VALUE, amplifier, false, false);
            player.addPotionEffect(effect);
        });
    }

    /**
     * Removes the suit by clearing the armor pieces and potion effects from the player.
     *
     * @param player The player whose armor will be removed.
     */
    public void remove(Player player) {
        getPassiveEffects().keySet().forEach(player::removePotionEffect);

        PlayerInventory inventory = player.getInventory();
        inventory.setBoots(null);
        inventory.setLeggings(null);
        inventory.setChestplate(null);
        inventory.setHelmet(null);
    }

    /**
     * Creates a colored leather armor piece.
     *
     * @param leatherArmor The material of the leather armor piece.
     * @param color        The color to apply to the armor.
     * @return An ItemStack representing the colored leather armor.
     */
    public ItemStack createColoredArmor(Material leatherArmor, Color color) {
        ItemStack item = new ItemStack(leatherArmor);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        if (meta != null) {
            meta.setColor(color);
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Called when the player selects this suit.
     * Removes the old suit and equips the new one.
     *
     * @param player The player who selected the suit.
     */
    public void onSelect(Player player) {
        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());
        CosmeticService cosmeticService = HardPractice.getInstance().getService(CosmeticService.class);
        if (profile == null || cosmeticService == null) return;

        String oldSuitName = profile.getProfileData().getCosmeticData().getSelected(getType());
        SuitRepository repo = cosmeticService.getRepository(getType(), SuitRepository.class);

        if (repo != null && oldSuitName != null && !oldSuitName.equalsIgnoreCase("None")) {
            BaseSuit oldSuit = repo.getCosmetic(oldSuitName);
            if (oldSuit != null) {
                oldSuit.remove(player);
            }
        }
        this.equip(player);
    }
}