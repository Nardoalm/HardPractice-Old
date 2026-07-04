package live.pvphard.practice.feature.event.command;

import live.pvphard.practice.feature.event.EventService;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;

public class EventLeaveCommand extends BaseCommand {
    @CommandData(
            name = "event.leave",
            aliases = {"events.leave"},
            usage = "event leave",
            description = "Sai do evento atual."
    )
    @Override
    public void onCommand(CommandArgs command) {
        this.plugin.getService(EventService.class).leaveActiveEvent(command.getPlayer(), false);
    }
}
