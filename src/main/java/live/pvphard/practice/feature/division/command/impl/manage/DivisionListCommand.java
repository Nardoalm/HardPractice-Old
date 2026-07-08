package live.pvphard.practice.feature.division.command.impl.manage;

import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.feature.division.DivisionService;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

/**
 * @author Remi
 * @project Hard
 * @date 6/2/2024
 */
public class DivisionListCommand extends BaseCommand {
    @CommandData(
            name = "division.list",
            isAdminOnly = true,
            usage = "division list",
            description = "Sends a list of all divisions."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        DivisionService divisionService = this.plugin.getService(DivisionService.class);

        player.sendMessage("");
        player.sendMessage(CC.translate("     &b&lDivision List &f(" + divisionService.getDivisions().size() + "&f)"));
        if (divisionService.getDivisions().isEmpty()) {
            player.sendMessage(CC.translate("      &f◆ &cNo divisions available."));
        }
        divisionService.getDivisions()
                .forEach(division -> player.sendMessage(CC.translate("      &f◆ &b" + division.getDisplayName() + " &f(" + division.getTiers().get(0).getRequiredWins() + " wins)")));
        player.sendMessage("");

    }
}