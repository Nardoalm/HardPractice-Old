package live.pvphard.practice.feature.match.snapshot.menu.button.items;

import live.pvphard.practice.library.menu.Button;
import live.pvphard.practice.feature.match.snapshot.Snapshot;
import live.pvphard.practice.common.item.ItemBuilder;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Emmy
 * @project Hard
 * @date 07/10/2024
 */
@AllArgsConstructor
public class SnapshotArmorButton extends Button {
    private final Snapshot snapshot;
    private int armorPart;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(this.snapshot.getArmor()[this.armorPart]).build();
    }
}