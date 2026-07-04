package live.pvphard.practice.feature.level.command.impl.data;

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
public class LevelAdminSetMaxEloCommand extends BaseCommand {
    @CommandData(
            name = "leveladmin.setmaxelo",
            isAdminOnly = true,
            inGameOnly = false,
            usage = "leveladmin setmaxelo <levelName> <maxElo>",
            description = "Define o Elo máximo para um nível"
    )
    @Override
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (args.length < 2) {
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

        int maxElo;
        try {
            maxElo = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_INVALID_NUMBER).replace("{input}", args[1]));
            return;
        }

        if (maxElo <= level.getMinElo()) {
            sender.sendMessage(this.getString(GlobalMessagesLocaleImpl.LEVEL_MAX_ELO_MUST_BE_GREATER_THAN_MIN).replace("{min-elo}", String.valueOf(level.getMinElo())));
            return;
        }

        level.setMaxElo(maxElo);
        levelService.saveLevel(level);

        sender.sendMessage(this.getString(GlobalMessagesLocaleImpl.LEVEL_MAX_ELO_SET)
                .replace("{level-name}", levelName)
                .replace("{max-elo}", String.valueOf(maxElo))
        );
    }
}