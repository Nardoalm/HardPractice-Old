package live.pvphard.practice.feature.ffa.command.impl.admin;

import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.core.profile.ProfileService;
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
public class FFAKickCommand extends BaseCommand {
    @CommandData(
            name = "ffa.kick",
            isAdminOnly = true,
            usage = "ffa kick <player>",
            description = "Expulsa um jogador da partida de FFA atual."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length != 1) {
            command.sendUsage();
            return;
        }

        Player targetPlayer = player.getServer().getPlayer(args[0]);
        if (targetPlayer == null) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_INVALID_PLAYER));
            return;
        }

        FFAMatch match = this.plugin.getService(FFAService.class).getFFAMatch(targetPlayer);
        if (match == null) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_PLAYER_NOT_PLAYING_FFA)
                    .replace("{name-color}", String.valueOf(this.getProfile(targetPlayer.getUniqueId()).getNameColor()))
                    .replace("{player}", targetPlayer.getName()));
            return;
        }

        match.leave(targetPlayer);
        player.sendMessage(this.getString(GlobalMessagesLocaleImpl.FFA_KICKED_PLAYER)
                .replace("{player}", targetPlayer.getName())
                .replace("{ffa-name}", match.getName())
                .replace("{name-color}", String.valueOf(this.plugin.getService(ProfileService.class).getProfile(targetPlayer.getUniqueId()).getNameColor()))
        );
    }
}