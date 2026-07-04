package live.pvphard.practice.feature.match.snapshot;

import live.pvphard.practice.common.InventoryUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Remi
 * @project Hard
 * @date 5/26/2024
 */
@Getter
@Setter
public class Snapshot {
    private final String username;

    private final UUID uuid;
    private UUID opponent;

    private double health;
    private int foodLevel;

    private final ItemStack[] armor;
    private final ItemStack[] inventory;

    private final List<String> potionEffects;

    private int thrownPotions;
    private int missedPotions;

    private int longestCombo;

    private int totalHits;
    private int criticalHits;
    private int blockedHits;

    private int wTaps;

    private long createdAt;

    /**
     * Constructor for the Snapshot class.
     *
     * @param player the player to create the snapshot for
     * @param alive  whether the player is alive or not
     */
    public Snapshot(Player player, boolean alive) {
        this.uuid = player.getUniqueId();
        this.username = player.getName();

        this.health = alive ? Math.round(player.getHealth() / 2) : 0;
        this.foodLevel = player.getFoodLevel();

        this.armor = InventoryUtil.cloneItemStackArray(player.getInventory().getArmorContents());
        this.inventory = InventoryUtil.cloneItemStackArray(player.getInventory().getContents());
        this.potionEffects = player.getActivePotionEffects().stream()
                .map(effect -> effect.getType().getName() + " " + effect.getAmplifier())
                .collect(Collectors.toList());

        this.thrownPotions = 0;
        this.missedPotions = 0;

        this.longestCombo = 0;

        this.totalHits = 0;
        this.criticalHits = 0;
        this.blockedHits = 0;

        this.wTaps = 0;

        this.createdAt = System.currentTimeMillis();
    }

    /**
     * Get the accuracy of potion throws as a percentage.
     *
     * @return the potion accuracy percentage
     */
    public double getPotionAccuracy() {
        if (this.missedPotions == 0) {
            return 100.0;
        } else if (this.thrownPotions == this.missedPotions) {
            return 50.0;
        }

        return Math.round(100.0D - (((double) this.missedPotions / (double) this.thrownPotions) * 100.0D));
    }

    /**
     * Get the amount of potions in the player's inventory.
     *
     * @return the amount of potions in the inventory
     */
    public int getAmountOfPotionsInInventory() {
        int amount = 0;
        for (ItemStack item : this.inventory) {
            if (item != null && item.getType() == Material.POTION && item.getDurability() == 16421) {
                amount += item.getAmount();
            }
        }
        return amount;
    }
}