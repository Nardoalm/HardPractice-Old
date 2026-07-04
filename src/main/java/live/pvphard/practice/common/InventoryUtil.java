package live.pvphard.practice.common;

import live.pvphard.practice.feature.kit.Kit;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.EnumSet;
import java.util.Set;

/**
 * @date 02/01/2025 - 19:13
 */
@UtilityClass
public class InventoryUtil {
    private final Set<Material> DYEABLE_BLOCKS = EnumSet.of(Material.WOOL, Material.STAINED_CLAY);
    private final Set<Material> LEATHER_ARMOR = EnumSet.of(
            Material.LEATHER_HELMET,
            Material.LEATHER_CHESTPLATE,
            Material.LEATHER_LEGGINGS,
            Material.LEATHER_BOOTS
    );

    /**
     * Applies a specified TeamColor to a player's inventory,
     * coloring any dyeable blocks and leather armor.
     *
     * @param player    The player whose inventory will be colored.
     * @param teamColor The TeamColor data to apply.
     */
    public void applyTeamColorToInventory(Player player, TeamColor teamColor) {
        if (player == null || !player.isOnline() || teamColor == null) {
            return;
        }

        PlayerInventory inventory = player.getInventory();

        colorItems(inventory.getContents(), teamColor);
        colorItems(inventory.getArmorContents(), teamColor);

        player.updateInventory();
    }

    /**
     * A private helper to iterate over an array of items and apply coloring.
     */
    private void colorItems(ItemStack[] items, TeamColor teamColor) {
        for (ItemStack item : items) {
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }

            if (DYEABLE_BLOCKS.contains(item.getType())) {
                item.setDurability(teamColor.getBlockDataValue());
            }

            if (LEATHER_ARMOR.contains(item.getType())) {
                ItemMeta meta = item.getItemMeta();
                if (meta instanceof LeatherArmorMeta) {
                    LeatherArmorMeta leatherMeta = (LeatherArmorMeta) meta;
                    leatherMeta.setColor(teamColor.getArmorColor());
                    item.setItemMeta(leatherMeta);
                }
            }
        }
    }

    /**
     * Clone an array of ItemStacks to ensure deep copy.
     *
     * @param items the original array
     * @return a cloned array
     */
    public ItemStack[] cloneItemStackArray(ItemStack[] items) {
        if (items == null) return null;

        ItemStack[] cloned = new ItemStack[items.length];
        for (int i = 0; i < items.length; i++) {
            cloned[i] = items[i] != null ? items[i].clone() : null;
        }
        return cloned;
    }

    public boolean hasAnyItem(ItemStack[] items) {
        if (items == null || items.length == 0) {
            return false;
        }

        for (ItemStack item : items) {
            if (item != null && item.getType() != Material.AIR) {
                return true;
            }
        }

        return false;
    }

    public ItemStack[] getEditableKitItems(Kit kit) {
        if (kit == null) {
            return new ItemStack[0];
        }

        if (hasAnyItem(kit.getEditorItems())) {
            return cloneItemStackArray(kit.getEditorItems());
        }

        if (hasAnyItem(kit.getItems())) {
            return cloneItemStackArray(kit.getItems());
        }

        return new ItemStack[0];
    }

    /**
     * Give a specific item to a player.
     *
     * @param player   the player to give the item to
     * @param material the material of the item to give
     */
    public void giveItem(Player player, Material material, int amount) {
        player.getInventory().addItem(new ItemStack(material, amount));
    }

    /**
     * Represents a set of color data used for team-based item coloring.
     * This is a public enum so it can be passed as a parameter from any class.
     */
    @Getter
    public enum TeamColor {
        WHITE(Color.fromRGB(240, 240, 240), (short) 0),
        ORANGE(Color.fromRGB(235, 136, 68), (short) 1),
        MAGENTA(Color.fromRGB(200, 80, 189), (short) 2),
        LIGHT_BLUE(Color.fromRGB(102, 153, 216), (short) 3),
        YELLOW(Color.fromRGB(225, 219, 62), (short) 4),
        LIME(Color.fromRGB(127, 204, 25), (short) 5),
        PINK(Color.fromRGB(242, 127, 165), (short) 6),
        GRAY(Color.fromRGB(76, 76, 76), (short) 7),
        LIGHT_GRAY(Color.fromRGB(153, 153, 153), (short) 8),
        CYAN(Color.fromRGB(76, 127, 153), (short) 9),
        PURPLE(Color.fromRGB(127, 63, 178), (short) 10),
        BLUE(Color.fromRGB(51, 76, 178), (short) 11),
        BROWN(Color.fromRGB(102, 76, 51), (short) 12),
        GREEN(Color.fromRGB(102, 127, 51), (short) 13),
        RED(Color.fromRGB(153, 51, 51), (short) 14),
        BLACK(Color.fromRGB(25, 25, 25), (short) 15);

        private final Color armorColor;
        private final short blockDataValue;

        TeamColor(Color armorColor, short blockDataValue) {
            this.armorColor = armorColor;
            this.blockDataValue = blockDataValue;
        }

        public static TeamColor fromChatColor(ChatColor color) {
            if (color == null) {
                return BLUE;
            }

            switch (color) {
                case BLACK:
                    return BLACK;
                case DARK_BLUE:
                    return BLUE;
                case DARK_GREEN:
                    return GREEN;
                case DARK_AQUA:
                    return CYAN;
                case DARK_RED:
                    return RED;
                case DARK_PURPLE:
                    return PURPLE;
                case GOLD:
                    return ORANGE;
                case GRAY:
                    return GRAY;
                case DARK_GRAY:
                    return LIGHT_GRAY;
                case BLUE:
                    return LIGHT_BLUE;
                case GREEN:
                    return LIME;
                case AQUA:
                    return CYAN;
                case RED:
                    return RED;
                case LIGHT_PURPLE:
                    return MAGENTA;
                case YELLOW:
                    return YELLOW;
                case WHITE:
                default:
                    return WHITE;
            }
        }
    }
}
