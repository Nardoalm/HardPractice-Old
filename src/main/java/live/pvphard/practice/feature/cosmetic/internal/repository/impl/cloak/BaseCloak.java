package live.pvphard.practice.feature.cosmetic.internal.repository.impl.cloak;

import live.pvphard.practice.feature.cosmetic.model.BaseCosmetic;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * @author Remi
 * @project hard-practice
 * @date 4/08/2025
 */
public abstract class BaseCloak extends BaseCosmetic {
    /**
     * Renders the particle effect for this specific cloak.
     * This method is called repeatedly by the CloakService while the player stands still.
     *
     * @param player The player to render the cloak for.
     */
    public abstract void render(Player player);

    /**
     * Rotates a vector around the Y axis based on a given yaw angle.
     * The original vector is modified and returned.
     *
     * @param v   The vector to rotate.
     * @param yaw The yaw angle in degrees to rotate by.
     * @return The same vector instance, now rotated.
     */
    protected Vector rotateAroundAxisY(Vector v, float yaw) {
        double angle = Math.toRadians(yaw);
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double x = v.getX() * cos - v.getZ() * sin;
        double z = v.getX() * sin + v.getZ() * cos;
        return v.setX(x).setZ(z);
    }
}