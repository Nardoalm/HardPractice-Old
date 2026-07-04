package live.pvphard.practice.feature.division.command.impl.player;

import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import live.pvphard.practice.feature.division.menu.DivisionsMenu;
import org.bukkit.entity.Player;

/**
 * @author Remi
 * @project Hard
 * @date 6/2/2024
 */
public class DivisionsCommand extends BaseCommand {
    @CommandData(
            name = "divisions",
            aliases = {"division.menu"},
            usage = "divisions",
            description = "Abre o menu de divisões"
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        new DivisionsMenu().openMenu(player);
    }
}