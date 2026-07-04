package live.pvphard.practice.core.profile.command.player;

import live.pvphard.practice.feature.level.menu.LevelMenu;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

/**
 * @author Emmy
 * @project Hard
 * @since 22/04/2025
 */
public class LevelCommand extends BaseCommand {
    @CommandData(
            name = "level",
            aliases = {"levels"},
            usage = "level",
            description = "Veja informações sobre seu nível atual."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        new LevelMenu(this.getProfile(player.getUniqueId())).openMenu(player);
    }
}