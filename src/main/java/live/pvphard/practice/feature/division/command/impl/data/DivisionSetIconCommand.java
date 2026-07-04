package live.pvphard.practice.feature.division.command.impl.data;

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
 * @since 28/01/2025
 */
public class DivisionSetIconCommand extends BaseCommand {
    @CommandData(
            name = "division.seticon",
            isAdminOnly = true,
            usage = "division seticon <name>",
            description = "Define o ícone de uma divisão para o item em sua mão."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length < 1) {
            command.sendUsage();
            return;
        }

        DivisionService divisionService = this.plugin.getService(DivisionService.class);
        Division division = divisionService.getDivision(args[0]);
        if (division == null) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.DIVISION_NOT_FOUND).replace("{division-name}", args[0]));
            return;
        }

        if (player.getItemInHand() == null) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_YOU_MUST_HOLD_ITEM));
            return;
        }

        division.setIcon(player.getItemInHand().getType());
        division.setDurability(player.getItemInHand().getDurability());
        divisionService.saveDivision(division);
        player.sendMessage(this.getString(GlobalMessagesLocaleImpl.DIVISION_ICON_SET)
                .replace("{division-name}", division.getDisplayName())
                .replace("{item-type}", player.getItemInHand().getType().name())
                .replace("{item-durability}", String.valueOf(player.getItemInHand().getDurability()))
        );
    }
}