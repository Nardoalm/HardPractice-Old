package live.pvphard.practice.feature.ffa.command.impl.admin.manage;

import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.feature.ffa.FFAService;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

/**
 * @author Remi
 * @project Hard
 * @date 5/27/2024
 */
public class FFAListCommand extends BaseCommand {
    @CommandData(
            name = "ffa.list",
            isAdminOnly = true,
            usage = "ffa list",
            description = "Sends a list of all FFA matches."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        FFAService ffaService = this.plugin.getService(FFAService.class);

        player.sendMessage("");
        player.sendMessage(CC.translate("     &b&lFFA Match List &f(" + ffaService.getMatches().size() + "&f)"));
        if (ffaService.getMatches().isEmpty()) {
            player.sendMessage(CC.translate("      &f◆ &cNo matches available."));
        }
        ffaService.getMatches().forEach(match -> player.sendMessage(CC.translate("      &f◆ &b" + match.getKit().getDisplayName() + " &f(" + (match.getPlayers().size() + "/" + match.getMaxPlayers()) + "&f)")));
        player.sendMessage("");
    }
}