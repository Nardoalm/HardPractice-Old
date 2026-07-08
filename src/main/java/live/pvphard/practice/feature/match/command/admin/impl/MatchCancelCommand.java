package live.pvphard.practice.feature.match.command.admin.impl;

import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.enums.ProfileState;
import live.pvphard.practice.feature.match.MatchState;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

/**
 * @author Remi
 * @project Hard
 * @date 5/26/2024
 */
public class MatchCancelCommand extends BaseCommand {
    @CommandData(
            name = "match.cancel",
            isAdminOnly = true,
            usage = "match cancel <player>",
            description = "Force-cancels a player match."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length != 1) {
            command.sendUsage();
            return;
        }

        Player target = player.getServer().getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_INVALID_PLAYER));
            return;
        }

        Profile profile = this.plugin.getService(ProfileService.class).getProfile(target.getUniqueId());
        if (profile.getState() != ProfileState.PLAYING || profile.getMatch() == null) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_PLAYER_NOT_PLAYING_MATCH)
                    .replace("{name-color}", String.valueOf(profile.getNameColor()))
                    .replace("{player}", target.getName())
            );
            return;
        }

        profile.getMatch().handleRoundEnd();
        profile.getMatch().setState(MatchState.ENDING_MATCH);
        profile.getMatch().getRunnable().setStage(4);

        player.sendMessage(this.getString(GlobalMessagesLocaleImpl.MATCH_CANCELLED_FOR_PLAYER)
                .replace("{name-color}", String.valueOf(profile.getNameColor()))
                .replace("{player}", target.getName())
        );
    }
}
