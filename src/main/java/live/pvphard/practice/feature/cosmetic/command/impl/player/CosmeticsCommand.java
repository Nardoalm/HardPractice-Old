package live.pvphard.practice.feature.cosmetic.command.impl.player;

import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import live.pvphard.practice.feature.cosmetic.menu.CosmeticsMenu;
import org.bukkit.entity.Player;

/**
 * @author Remi
 * @project Hard
 * @date 6/1/2024
 */
public class CosmeticsCommand extends BaseCommand {
    @CommandData(
            name = "cosmetics",
            usage = "cosmetics",
            description = "Opens the cosmetics menu."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        new CosmeticsMenu().openMenu(player);
    }
}