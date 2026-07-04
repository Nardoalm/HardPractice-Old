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
public class DivisionDeleteCommand extends BaseCommand {
    @CommandData(
            name = "division.delete",
            isAdminOnly = true,
            usage = "division delete <name>",
            description = "Exclui uma divisão."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length < 1) {
            command.sendUsage();
            return;
        }

        String name = args[0];
        DivisionService divisionService = this.plugin.getService(DivisionService.class);
        Division division = divisionService.getDivision(name);
        if (division == null) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.DIVISION_NOT_FOUND));
            return;
        }

        divisionService.deleteDivision(division.getName());
        player.sendMessage(this.getString(GlobalMessagesLocaleImpl.DIVISION_DELETED).replace("{division-name}", division.getName()));
    }
}