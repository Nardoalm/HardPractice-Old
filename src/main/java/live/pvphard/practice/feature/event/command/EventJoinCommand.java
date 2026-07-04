package live.pvphard.practice.feature.event.command;

import live.pvphard.practice.feature.event.EventService;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;

public class EventJoinCommand extends BaseCommand {
    @CommandData(
            name = "event.join",
            aliases = {"events.join"},
            usage = "event join",
            description = "Entra no evento ativo."
    )
    @Override
    public void onCommand(CommandArgs command) {
        this.plugin.getService(EventService.class).joinActiveEvent(command.getPlayer());
    }
}
