package live.pvphard.practice.feature.queue.command.admin.impl;

import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.feature.queue.QueueService;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

/**
 * @author Remi
 * @project Hard
 * @date 5/26/2024
 */
public class QueueReloadCommand extends BaseCommand {
    @CommandData(
            name = "queue.reload",
            aliases = {"reloadqueue", "reloadqueues"},
            isAdminOnly = true,
            usage = "/queue reload",
            description = "Reloads all queues from configuration"
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        this.plugin.getService(QueueService.class).reloadQueues();
        player.sendMessage(this.getString(GlobalMessagesLocaleImpl.QUEUE_RELOADED));
    }
}