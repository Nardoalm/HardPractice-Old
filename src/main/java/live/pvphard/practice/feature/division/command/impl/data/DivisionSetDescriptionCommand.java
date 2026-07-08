package live.pvphard.practice.feature.division.command.impl.data;

import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.feature.division.Division;
import live.pvphard.practice.feature.division.DivisionService;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * @author Emmy
 * @project Hard
 * @since 28/01/2025
 */
public class DivisionSetDescriptionCommand extends BaseCommand {
    @CommandData(
            name = "division.setdescription",
            isAdminOnly = true,
            usage = "division setdescription <name> <description>",
            description = "Sets the description of a division."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length < 2) {
            command.sendUsage();
            return;
        }

        DivisionService divisionService = this.plugin.getService(DivisionService.class);
        Division division = divisionService.getDivision(args[0]);
        if (division == null) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.DIVISION_NOT_FOUND).replace("{division-name}", args[0]));
            return;
        }

        String description = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        division.setDescription(description);
        divisionService.saveDivision(division);
        player.sendMessage(this.getString(GlobalMessagesLocaleImpl.DIVISION_DESCRIPTION_SET)
                .replace("{division-name}", division.getDisplayName())
                .replace("{description}", description)
        );
    }
}