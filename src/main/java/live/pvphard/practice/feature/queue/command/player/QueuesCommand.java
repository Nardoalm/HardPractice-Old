package live.pvphard.practice.feature.queue.command.player;

import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import live.pvphard.practice.feature.queue.QueueService;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.enums.ProfileState;
import live.pvphard.practice.common.text.CC;
import org.bukkit.entity.Player;

/**
 * @author Emmy
 * @project Hard
 * @date 25/05/2024 - 16:45
 */
public class QueuesCommand extends BaseCommand {
    @Override
    @CommandData(
            name = "queues",
            aliases = {"selectqueue", "joinqueue"},
            usage = "queues",
            description = "Opens the queue selection menu."
    )
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        ProfileService profileService = this.plugin.getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());
        if (!profile.getState().equals(ProfileState.LOBBY)) {
            player.sendMessage(CC.translate("&cYou need to be in the lobby to use this command."));
            return;
        }

        if (profile.getParty() != null) {
            player.sendMessage(CC.translate("&cYou must leave your party before joining a queue."));
            return;
        }

        this.plugin.getService(QueueService.class).getQueueMenu().openMenu(player);
    }
}
