package live.pvphard.practice.feature.division.command.impl.manage;

import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.feature.division.Division;
import live.pvphard.practice.feature.division.DivisionService;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

/**
 * @author Emmy
 * @project Hard
 * @since 26/01/2025
 */
public class DivisionCreateCommand extends BaseCommand {
    @CommandData(
            name = "division.create",
            isAdminOnly = true,
            usage = "division.create <name> <requiredWins>",
            description = "Cria uma nova divisão."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length < 2) {
            command.sendUsage();
            return;
        }

        String name = args[0];
        int requiredWins;
        try {
            requiredWins = Integer.parseInt(args[1]);
        } catch (NumberFormatException exception) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_INVALID_NUMBER).replace("{input}", args[1]));
            return;
        }

        DivisionService divisionService = this.plugin.getService(DivisionService.class);
        Division division = divisionService.getDivision(name);
        if (division != null) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.DIVISION_ALREADY_EXISTS).replace("{division-name}", name));
            return;
        }

        divisionService.createDivision(name, requiredWins);
        player.sendMessage(this.getString(GlobalMessagesLocaleImpl.DIVISION_CREATED)
                .replace("{division-name}", name)
                .replace("{required-wins}", String.valueOf(requiredWins))
        );
    }
}