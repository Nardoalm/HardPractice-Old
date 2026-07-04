package live.pvphard.practice.feature.server.command.impl;

import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.feature.server.ServerService;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

/**
 * @author Emmy
 * @project Hard
 * @since 09/03/2025
 */
public class ServiceAllowQueueCommand extends BaseCommand {
    @CommandData(
            name = "service.allowqueue",
            isAdminOnly = true,
            usage = "service allowqueue <true/false>",
            description = "Permite/bloqueia filas."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length < 1) {
            command.sendUsage();
            return;
        }

        boolean allowQueue;
        try {
            allowQueue = Boolean.parseBoolean(args[0]);
        } catch (Exception e) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_INVALID_BOOLEAN));
            return;
        }

        ServerService serverService = this.plugin.getService(ServerService.class);
        serverService.clearAllQueues(player);
        serverService.setQueueingAllowed(allowQueue);

        player.sendMessage(this.getString(GlobalMessagesLocaleImpl.QUEUE_TOGGLED)
                .replace("{status}", allowQueue ? CC.translate("&apermitiu") : CC.translate("&cbloqueou"))
        );
    }
}
