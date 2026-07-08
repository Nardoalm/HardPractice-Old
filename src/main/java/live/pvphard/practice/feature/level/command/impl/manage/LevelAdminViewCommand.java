package live.pvphard.practice.feature.level.command.impl.manage;

import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.feature.level.LevelService;
import live.pvphard.practice.feature.level.data.LevelData;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

/**
 * @author Emmy
 * @project Hard
 * @since 26/05/2025
 */
public class LevelAdminViewCommand extends BaseCommand {
    @CommandData(
            name = "leveladmin.view",
            isAdminOnly = true,
            inGameOnly = false,
            usage = "leveladmin view <levelName>",
            description = "Shows information about a level"
    )
    @Override
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (args.length < 1) {
            command.sendUsage();
            return;
        }

        String levelName = args[0];
        LevelService levelService = this.plugin.getService(LevelService.class);
        LevelData level = levelService.getLevel(levelName);
        if (level == null) {
            sender.sendMessage(this.getString(GlobalMessagesLocaleImpl.LEVEL_NOT_FOUND).replace("{level-name}", levelName));
            return;
        }

        Arrays.asList(
                "",
                "&b&lLevel Information:",
                " &f◆ &bName: &e" + level.getName(),
                " &f◆ &bDisplay Name: &e" + level.getDisplayName(),
                " &f◆ &bMinimum Elo: &e" + level.getMinElo(),
                " &f◆ &bMaximum Elo: &e" + level.getMaxElo(),
                " &f◆ &bMaterial: &e" + level.getMaterial().name(),
                " &f◆ &bDurability: &e" + level.getDurability(),
                ""
        ).forEach(line -> sender.sendMessage(CC.translate(line)));
    }
}
