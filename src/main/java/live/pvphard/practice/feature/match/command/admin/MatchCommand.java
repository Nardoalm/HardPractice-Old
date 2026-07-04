package live.pvphard.practice.feature.match.command.admin;

import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.command.CommandSender;

/**
 * @author Remi
 * @project Hard
 * @date 5/26/2024
 */
public class MatchCommand extends BaseCommand {
    @CommandData(
            name = "match",
            isAdminOnly = true,
            inGameOnly = false,
            usage = "match",
            description = "Gerencia partidas"
    )
    @Override
    public void onCommand(CommandArgs args) {
        CommandSender sender = args.getSender();

        sender.sendMessage(" ");
        sender.sendMessage(CC.translate("&b&lAjuda dos Comandos de Partida:"));
        sender.sendMessage(CC.translate(" &f◆ &b/match start &8(&7p1&8) &8(&7p2&8) &8(&7arena&8) &8(&7kit&8) &7| Iniciar uma partida"));
        sender.sendMessage(CC.translate(" &f◆ &b/match cancel &8(&7player&8) &7| Cancelar uma partida"));
        sender.sendMessage(CC.translate(" &f◆ &b/match info &8(&7player&8) &7| Ver info da partida de um jogador"));
        sender.sendMessage("");
    }
}
