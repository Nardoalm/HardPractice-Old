package live.pvphard.practice.feature.queue.command.player;

import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.enums.ProfileState;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import live.pvphard.practice.feature.queue.menu.QueuesMenuModern;
import org.bukkit.entity.Player;

/**
 * @author Remi
 * @project hard-practice
 * @date 22/06/2025
 */
public class UnrankedCommand extends BaseCommand {
    @CommandData(
            name = "unranked",
            usage = "unranked",
            description = "Abre o menu da fila unranked."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        Profile profile = this.plugin.getService(ProfileService.class).getProfile(player.getUniqueId());
        if (profile.getState() != ProfileState.LOBBY) {
            return;
        }

        new QueuesMenuModern().openMenu(player);
    }
}