package live.pvphard.practice.feature.server.command.impl;

import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import live.pvphard.practice.feature.server.menu.ServiceMenu;
import org.bukkit.entity.Player;

/**
 * @author Emmy
 * @project Hard
 * @since 09/03/2025
 */
public class ServiceMenuCommand extends BaseCommand {
    @CommandData(name = "service.menu", isAdminOnly = true, usage = "service menu", description = "Service menu command.")
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        new ServiceMenu().openMenu(player);
    }
}
