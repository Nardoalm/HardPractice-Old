package live.pvphard.practice.core.profile.command.player.setting;

import live.pvphard.practice.common.constants.MessageConstant;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

/**
 * @author Emmy
 * @project Hard
 * @date 19/05/2024 - 11:27
 */
public class MatchSettingsCommand extends BaseCommand {
    @CommandData(
            name = "matchsettings",
            usage = "matchsettings",
            description = "Abre o menu de configurações de partida."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        //new MatchSettingsMenu().openMenu(player);
        player.sendMessage(MessageConstant.IN_DEVELOPMENT);
    }
}