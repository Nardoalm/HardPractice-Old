package live.pvphard.practice.feature.match.command.admin.impl;

import live.pvphard.practice.core.locale.internal.impl.message.GameMessagesLocaleImpl;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.feature.match.Match;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author Emmy
 * @project Hard
 * @since 16/06/2025
 */
public class MatchResetBlocksCommand extends BaseCommand {
    @CommandData(
            name = "match.resetblocks",
            isAdminOnly = true,
            usage = "match resetblocks",
            description = "Resets all block changes in your current match."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        ProfileService profileService = this.plugin.getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());
        Match match = profile.getMatch();
        if (match == null) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_YOU_NOT_PLAYING_MATCH));
            return;
        }

        match.resetBlockChanges();
        if (this.getBoolean(GameMessagesLocaleImpl.MATCH_BLOCKS_RESET_MESSAGE_ENABLED_BOOLEAN)) {
            List<String> messages = this.getStringList(GameMessagesLocaleImpl.MATCH_BLOCKS_RESET_MESSAGE_FORMAT);
            for (String message : messages) {
                match.sendMessage(message
                        .replace("{name-color}", String.valueOf(profile.getNameColor()))
                        .replace("{player}", player.getName())
                );
            }
        }
    }
}