package live.pvphard.practice.feature.command.impl.main.impl;

import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.config.ConfigService;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

/**
 * @author Emmy
 * @project Hard
 * @date 06/06/2024 - 17:34
 */
public class KaosReloadCommand extends BaseCommand {

    //TODO: reload gui with options: Config files (messages), menus, Caches(kits, arenas, levels, etc..)

    @CommandData(
            name = "hard.reload",
            isAdminOnly = true,
            inGameOnly = false,
            usage = "hard.reload",
            description = "Recarrega as configuracoes do HardPractice."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        player.sendMessage(CC.translate("&bHardPrac &frecarregando..."));
        this.plugin.getService(ConfigService.class).reloadConfigs();
        player.sendMessage(CC.translate("&bHardPrac &arecarregado&f."));
    }
}
