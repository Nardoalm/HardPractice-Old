package live.pvphard.practice.feature.match.snapshot.command;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.feature.match.snapshot.Snapshot;
import live.pvphard.practice.feature.match.snapshot.SnapshotService;
import live.pvphard.practice.feature.match.snapshot.menu.SnapshotMenu;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author Emmy
 * @project Hard
 * @date 15/06/2024 - 22:19
 */
public class InventoryCommand extends BaseCommand {
    @CommandData(
            name = "inventory",
            aliases = {"inv", "snapshot"},
            usage = "inventory <playerId>",
            description = "Vê o inventário de um jogador."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (command.length() < 1) {
            command.sendUsage();
            return;
        }

        String uuid = args[0];
        Snapshot snapshot;
        SnapshotService snapshotRepository = HardPractice.getInstance().getService(SnapshotService.class);

        try {
            snapshot = snapshotRepository.getSnapshot(UUID.fromString(uuid));
        } catch (Exception exception) {
            snapshot = snapshotRepository.getSnapshot(uuid);
        }

        if (snapshot == null) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.SNAPSHOT_INVENTORY_EXPIRED));
            return;
        }

        new SnapshotMenu(snapshot).openMenu(player);
    }
}