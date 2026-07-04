package live.pvphard.practice.core.profile.command.player;

import live.pvphard.practice.core.profile.menu.shop.ShopMenu;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;

/**
 * @author Remi
 * @project Hard
 * @date 6/2/2024
 */
public class ShopCommand extends BaseCommand {
    @CommandData(
            name = "shop",
            usage = "shop",
            description = "Abre a loja do servidor"
    )
    @Override
    public void onCommand(CommandArgs command) {
        new ShopMenu().openMenu(command.getPlayer());
    }
}