package live.pvphard.practice.feature.event.command;

import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.feature.event.EventService;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

public class EventStopCommand extends BaseCommand {
    @CommandData(
            name = "event.stop",
            aliases = {"events.stop"},
            usage = "event stop",
            description = "Stops the active event."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        EventService eventService = this.plugin.getService(EventService.class);

        if (!eventService.canManageActiveEvent(player)) {
            player.sendMessage(CC.translate("&cYou cannot stop the current event."));
            return;
        }

        eventService.stopActiveEvent("&cThe event was stopped by " + player.getName() + "&c.");
    }
}
