package live.pvphard.practice.feature.level.command.impl.manage;

import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.feature.level.LevelService;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.command.CommandSender;

/**
 * @author Emmy
 * @project Hard
 * @since 26/05/2025
 */
public class LevelAdminListCommand extends BaseCommand {
    @CommandData(
            name = "leveladmin.list",
            isAdminOnly = true,
            usage = "level admin list",
            description = "Lista todos os níveis.",
            inGameOnly = false
    )
    @Override
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();

        LevelService levelService = this.plugin.getService(LevelService.class);

        sender.sendMessage("");
        sender.sendMessage(CC.translate("     &b&lLevel List &f(" + levelService.getLevels().size() + "&f)"));
        if (levelService.getLevels().isEmpty()) {
            sender.sendMessage(CC.translate("      &f◆ &cNenhum nível disponível."));
        } else {
            levelService.getLevels()
                    .forEach(level -> sender.sendMessage(CC.translate("      &f◆ &b" + level.getDisplayName() + " &f(" + level.getMinElo() + " - " + level.getMaxElo() + " elo)")));
        }
        sender.sendMessage("");
    }
}