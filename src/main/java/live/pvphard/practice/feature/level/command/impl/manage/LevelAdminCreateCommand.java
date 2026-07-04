package live.pvphard.practice.feature.level.command.impl.manage;

import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.feature.level.LevelService;
import live.pvphard.practice.feature.level.data.LevelData;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.command.CommandSender;

/**
 * @author Emmy
 * @project Hard
 * @since 26/05/2025
 */
public class LevelAdminCreateCommand extends BaseCommand {
    @CommandData(
            name = "leveladmin.create",
            isAdminOnly = true,
            inGameOnly = false,
            usage = "leveladmin create <levelName> <minElo> <maxElo>",
            description = "Cria um novo nível"
    )
    @Override
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (args.length < 3) {
            command.sendUsage();
            return;
        }

        String levelName = args[0];
        LevelService levelService = this.plugin.getService(LevelService.class);
        LevelData level = levelService.getLevel(levelName);
        if (level != null) {
            sender.sendMessage(this.getString(GlobalMessagesLocaleImpl.LEVEL_ALREADY_EXISTS).replace("{level-name}", levelName));
            return;
        }

        int minElo;
        try {
            minElo = Integer.parseInt(args[1]);
        } catch (NumberFormatException exception) {
            sender.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_INVALID_NUMBER).replace("{input}", args[1]));
            return;
        }

        int maxElo;
        try {
            maxElo = Integer.parseInt(args[2]);
        } catch (NumberFormatException exception) {
            sender.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_INVALID_NUMBER).replace("{input}", args[2]));
            return;
        }

        if (minElo >= maxElo) {
            sender.sendMessage(this.getString(GlobalMessagesLocaleImpl.LEVEL_MINIMUM_ELO_MUST_BE_LESS_THAN_MAXIMUM));
            return;
        }

        levelService.createLevel(levelName, minElo, maxElo);

        sender.sendMessage(this.getString(GlobalMessagesLocaleImpl.LEVEL_CREATED)
                .replace("{level-name}", levelName)
                .replace("{min-elo}", String.valueOf(minElo))
                .replace("{max-elo}", String.valueOf(maxElo))
        );
    }
}