package live.pvphard.practice.feature.party.command.impl.leader.privacy;

import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.enums.ProfileState;
import live.pvphard.practice.feature.party.PartyState;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

/**
 * @author Emmy
 * @project Hard
 * @date 16/11/2024 - 23:14
 */
public class PartyOpenCommand extends BaseCommand {
    @CommandData(
            name = "party.open",
            aliases = {"p.open"},
            usage = "party open",
            description = "Opens your party to the public."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        ProfileService profileService = this.plugin.getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());
        if (profile.getParty() == null) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_YOU_NOT_IN_PARTY));
            return;
        }

        if (!profile.getState().equals(ProfileState.LOBBY)) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_YOU_MUST_BE_IN_LOBBY));
            return;
        }

        profile.getParty().setState(PartyState.PUBLIC);
        player.sendMessage(CC.translate("&aYou opened your party to the public. Now anyone can join."));
    }
}