package live.pvphard.practice.feature.cosmetic.model;

import org.bukkit.Material;

/**
 * @author Remi
 * @project Hard
 * @date 6/1/2024
 */
public interface Cosmetic {
    String getName();

    String getDescription();

    String getPermission();

    Material getIcon();

    int getPrice();

    int getSlot();
}