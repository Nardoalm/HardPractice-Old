package live.pvphard.practice.feature.event.command;

import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.feature.event.EventService;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

public class EventStartCommand extends BaseCommand {
    @CommandData(
            name = "event.start",
            aliases = {"events.start"},
            permission = "kaos.command.donator.host",
            usage = "event start <evento>",
            description = "Hospeda um evento."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length < 1) {
            player.sendMessage(CC.translate("&cUse /event start <evento>."));
            return;
        }

        this.plugin.getService(EventService.class).hostEvent(player, args[0]);
    }
}
