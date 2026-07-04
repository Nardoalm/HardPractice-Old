package live.pvphard.practice.feature.queue.command.admin;

import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.command.CommandSender;

/**
 * @author Emmy
 * @project Hard
 * @date 24/09/2024 - 20:51
 */
public class QueueCommand extends BaseCommand {
    @CommandData(
            name = "queue",
            isAdminOnly = true,
            inGameOnly = false,
            usage = "queue",
            description = "Comando principal de filas"
    )
    @Override
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();

        sender.sendMessage(" ");
        sender.sendMessage(CC.translate("&b&lAjuda dos Comandos de Fila:"));
        sender.sendMessage(CC.translate(" &f◆ &b/queue force &8(&7player&8) &8(&7kit&8) &8<&7ranked&8> &7| Forçar um jogador para uma fila"));
        //sender.sendMessage(CC.translate(" &f◆ &b/queue remove &8(&7player&8) &7| Remove a player from queue"));
        sender.sendMessage(CC.translate(" &f◆ &b/queue reload &7| Recarregar as filas"));
        sender.sendMessage("");
    }
}