package live.pvphard.practice.feature.ffa.command.impl.admin.manage;

import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.feature.ffa.FFAMatch;
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
public class FFAListPlayersCommand extends BaseCommand {
    @CommandData(
            name = "ffa.listplayers",
            isAdminOnly = true,
            usage = "ffa listplayers <kit>",
            description = "Lista todos os jogadores de uma partida de FFA."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length != 1) {
            command.sendUsage();
            return;
        }

        String kitName = args[0];
        FFAMatch match = this.plugin.getService(FFAService.class).getFFAMatch(kitName);
        if (match == null) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.FFA_NOT_FOUND).replace("{ffa-name}", kitName));
            return;
        }

        player.sendMessage("");
        player.sendMessage(CC.translate("     &b&l" + match.getKit().getDisplayName() + " Player List &f(" + match.getPlayers().size() + "&f)"));
        if (match.getPlayers().isEmpty()) {
            player.sendMessage(CC.translate("      &f◆ &cNenhum jogador disponível."));
        }
        match.getPlayers().forEach(participant -> player.sendMessage(CC.translate("      &f◆ &b" + participant.getName())));
        player.sendMessage("");
    }
}
