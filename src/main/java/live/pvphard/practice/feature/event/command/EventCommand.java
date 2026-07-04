package live.pvphard.practice.feature.event.command;

import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.feature.event.menu.EventMenu;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

public class EventCommand extends BaseCommand {
    @CommandData(
            name = "event",
            aliases = {"events"},
            usage = "event",
            description = "Abre o menu de eventos."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length > 0) {
            player.sendMessage(CC.translate("&b/event join &7- Entrar no evento ativo"));
            player.sendMessage(CC.translate("&b/event leave &7- Sair do evento atual"));
            player.sendMessage(CC.translate("&b/event start <evento> &7- Hospedar um evento"));
            player.sendMessage(CC.translate("&b/event stop &7- Encerrar o evento ativo"));
            return;
        }

        new EventMenu().openMenu(player);
    }
}
