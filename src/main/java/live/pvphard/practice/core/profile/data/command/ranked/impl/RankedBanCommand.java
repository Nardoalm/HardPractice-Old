package live.pvphard.practice.core.profile.data.command.ranked.impl;

import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.data.ProfileData;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author Emmy
 * @project Hard
 * @since 13/03/2025
 */
public class RankedBanCommand extends BaseCommand {
    @CommandData(
            name = "ranked.ban",
            isAdminOnly = true,
            usage = "ranked ban <player>",
            description = "Bane um jogador das partidas ranked."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length < 1) {
            command.sendUsage();
            return;
        }

        String targetName = args[0];
        RankedBanCommandSupport.ResolvedRankedProfile resolvedTarget = RankedBanCommandSupport.resolveTarget(this.plugin, targetName);
        if (resolvedTarget == null) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_INVALID_PLAYER));
            return;
        }

        Profile profile = resolvedTarget.getProfile();
        if (profile == null) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_INVALID_PLAYER));
            return;
        }

        ProfileData profileData = profile.getProfileData();
        if (profileData == null) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_INVALID_PLAYER));
            return;
        }

        String targetDisplayName = resolvedTarget.getDisplayName();

        if (profileData.isRankedBanned()) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.RANKED_PLAYER_ALREADY_BANNED)
                    .replace("{name-color}", String.valueOf(profile.getNameColor()))
                    .replace("{player}", targetDisplayName)
            );
            return;
        }

        profileData.setRankedBanned(true);
        profile.save();

        if (this.getBoolean(GlobalMessagesLocaleImpl.RANKED_PLAYER_BAN_BROADCAST_BOOLEAN)) {
            List<String> message = this.getStringList(GlobalMessagesLocaleImpl.RANKED_PLAYER_BAN_BROADCAST);
            for (String line : message) {
                this.plugin.getServer().broadcastMessage(CC.translate(line
                        .replace("{name-color}", String.valueOf(profile.getNameColor()))
                        .replace("{player}", targetDisplayName)
                        .replace("{reason}", "N/D")
                        .replace("{ban-id}", "N/D")
                        .replace("{duration}", "N/D") //TODO
                ));
            }
        }

        if (this.getBoolean(GlobalMessagesLocaleImpl.RANKED_BAN_MESSAGE_NOTICE_BOOLEAN)) {
            Player targetPlayer = this.plugin.getServer().getPlayer(profile.getUuid());
            if (targetPlayer != null && targetPlayer.isOnline()) {
                List<String> message = this.getStringList(GlobalMessagesLocaleImpl.RANKED_BAN_MESSAGE_NOTICE);
                for (String line : message) {
                    targetPlayer.sendMessage(line
                            .replace("{name-color}", String.valueOf(profile.getNameColor()))
                            .replace("{player}", targetDisplayName)
                            .replace("{reason}", "N/D")
                            .replace("{ban-id}", "N/D")
                            .replace("{duration}", "N/D") //TODO
                    );
                }
            }
        }
    }
}
