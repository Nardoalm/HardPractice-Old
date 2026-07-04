package live.pvphard.practice.feature.ffa.command.impl.admin.data;

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
public class FFAMaxPlayersCommand extends BaseCommand {
    @CommandData(
            name = "ffa.maxplayers",
            isAdminOnly = true,
            usage = "ffa maxplayers <kit> <maxPlayers>",
            description = "Define o máximo de jogadores para um kit de FFA."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length != 2) {
            command.sendUsage();
            return;
        }

        String kitName = args[0];
        int maxPlayers = Integer.parseInt(args[1]);

        FFAMatch match = this.plugin.getService(FFAService.class).getFFAMatch(kitName);
        if (match == null) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.FFA_NOT_FOUND).replace("{ffa-name}", kitName));
            return;
        }

        match.setMaxPlayers(maxPlayers);
        player.sendMessage(this.getString(GlobalMessagesLocaleImpl.FFA_MAX_PLAYERS_SET)
                .replace("{kit-name}", kitName)
                .replace("{max-players}", String.valueOf(maxPlayers))
        );
    }
}