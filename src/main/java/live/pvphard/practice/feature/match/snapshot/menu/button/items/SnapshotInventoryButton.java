package live.pvphard.practice.feature.match.snapshot.menu.button.items;

import live.pvphard.practice.library.menu.Button;
import live.pvphard.practice.common.item.ItemBuilder;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Emmy
 * @project Hard
 * @since 27/06/2025
 */
@AllArgsConstructor
public class SnapshotInventoryButton extends Button {
    private final ItemStack item;

    @Override
    public ItemStack getButtonItem(Player player) {
        if (this.item == null) {
            return new ItemStack(Material.AIR);
        }
        return new ItemBuilder(this.item).build();
    }
}
