package us.alleypvp.practice.feature.match.command.player;

import us.alleypvp.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import us.alleypvp.practice.core.profile.Profile;
import us.alleypvp.practice.core.profile.ProfileService;
import us.alleypvp.practice.core.profile.enums.ProfileState;
import us.alleypvp.practice.library.command.BaseCommand;
import us.alleypvp.practice.library.command.CommandArgs;
import us.alleypvp.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

/**
 * @author Remi
 * @project Alley
 * @date 5/26/2024
 */
public class LeaveMatchCommand extends BaseCommand {
    @CommandData(
            name = "leave",
            aliases = {"leavematch", "suicide", "l", "lobby"},
            usage = "leave",
            description = "Leave on the match."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        ProfileService profileService = this.plugin.getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());

        if (profile.getState() != ProfileState.PLAYING) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_YOU_NOT_PLAYING_MATCH));
            return;
        }

        profile.getMatch().handleDisconnect(player);
    }
}