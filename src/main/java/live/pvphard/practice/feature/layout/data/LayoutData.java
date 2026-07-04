package live.pvphard.practice.feature.layout.data;

import live.pvphard.practice.common.InventoryUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

/**
 * @author Emmy
 * @project Hard
 * @since 03/05/2025
 */
@Getter
@Setter
public class LayoutData {
    private String name;
    private String displayName;
    private ItemStack[] items;

    /**
     * Constructor for the LayoutData class.
     *
     * @param name        The name of the layout.
     * @param displayName The display name of the layout.
     * @param items       The items in the layout.
     */
    public LayoutData(String name, String displayName, ItemStack[] items) {
        this.name = name;
        this.displayName = displayName;
        this.setItems(items);
    }

    public ItemStack[] getItems() {
        return InventoryUtil.cloneItemStackArray(this.items);
    }

    public void setItems(ItemStack[] items) {
        this.items = InventoryUtil.cloneItemStackArray(items);
    }
}
