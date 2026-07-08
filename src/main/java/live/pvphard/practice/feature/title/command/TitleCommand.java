package live.pvphard.practice.feature.title.command;

import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import live.pvphard.practice.feature.title.menu.TitleMenu;
import live.pvphard.practice.core.profile.ProfileService;
import org.bukkit.entity.Player;

/**
 * @author Emmy
 * @project Hard
 * @since 22/04/2025
 */
public class TitleCommand extends BaseCommand {
    @CommandData(
            name = "title",
            aliases = {"titles"},
            usage = "title",
            description = "Opens the titles menu."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        new TitleMenu(this.plugin.getService(ProfileService.class).getProfile(player.getUniqueId())).openMenu(player);
    }
}