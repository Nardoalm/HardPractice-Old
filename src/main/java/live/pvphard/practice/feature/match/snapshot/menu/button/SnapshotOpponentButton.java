package live.pvphard.practice.feature.match.snapshot.menu.button;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.library.menu.Button;
import live.pvphard.practice.feature.match.snapshot.SnapshotService;
import live.pvphard.practice.feature.match.snapshot.Snapshot;
import live.pvphard.practice.common.item.ItemBuilder;
import live.pvphard.practice.common.text.CC;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class SnapshotOpponentButton extends Button {
    private final Snapshot snapshot;

    @Override
    public ItemStack getButtonItem(Player player) {
        Snapshot opponentSnapshot = HardPractice.getInstance().getService(SnapshotService.class).getSnapshot(this.snapshot.getOpponent());
        if (opponentSnapshot == null) {
            return new ItemBuilder(Material.BARRIER)
                    .name(CC.translate("&cOpponent Not Found"))
                    .lore("&7The opponent's snapshot was not found.")
                    .hideMeta()
                    .build();
        }

        return new ItemBuilder(Material.PAPER)
                .name(CC.translate("&bView Opponent"))
                .lore("&7Click to view &b" + opponentSnapshot.getUsername() + "&7's inventory.")
                .hideMeta()
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        if (clickType != ClickType.LEFT) return;

        player.performCommand("inventory " + this.snapshot.getOpponent());
    }
}