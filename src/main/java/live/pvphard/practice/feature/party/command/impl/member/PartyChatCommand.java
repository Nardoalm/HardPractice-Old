package live.pvphard.practice.feature.party.command.impl.member;

import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.enums.ChatChannel;
import live.pvphard.practice.feature.party.PartyService;
import live.pvphard.practice.feature.party.Party;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author Remi
 * @project Hard
 * @date 5/25/2024
 */
public class PartyChatCommand extends BaseCommand {
    @CommandData(
            name = "party.chat",
            aliases = {"p.chat", "pc"},
            usage = "party chat [message]",
            description = "Envia mensagem no chat da party."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        ProfileService profileService = this.plugin.getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());
        if (profile == null || profile.getProfileData() == null || profile.getProfileData().getSettingData() == null) {
            player.sendMessage(CC.translate("&cNão foi possível alterar o canal da party agora."));
            return;
        }
        String message = Arrays.stream(args).collect(Collectors.joining(" ")).trim();

        if (args.length == 0) {
            if (!this.hasActiveParty(profile)) {
                profile.getProfileData().getSettingData().setChatChannel(ChatChannel.GLOBAL.toString());
                player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_YOU_NOT_IN_PARTY));
                return;
            }

            if (ChatChannel.PARTY.toString().equalsIgnoreCase(profile.getProfileData().getSettingData().getChatChannel())) {
                profile.getProfileData().getSettingData().setChatChannel(ChatChannel.GLOBAL.toString());
                player.sendMessage(CC.translate("&eSeu chat foi alterado para &fGlobal&e."));
            } else {
                profile.getProfileData().getSettingData().setChatChannel(ChatChannel.PARTY.toString());
                player.sendMessage(CC.translate("&aSeu chat foi alterado para &bParty&a."));
            }
            return;
        }

        if (!this.hasActiveParty(profile)) {
            profile.getProfileData().getSettingData().setChatChannel(ChatChannel.GLOBAL.toString());
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_YOU_NOT_IN_PARTY));
            return;
        }

        profile.getProfileData().getSettingData().setChatChannel(ChatChannel.PARTY.toString());
        String partyMessage = this.plugin.getService(PartyService.class).formatPartyChatMessage(player, message);
        profile.getParty().notifyParty(partyMessage);
    }

    private boolean hasActiveParty(Profile profile) {
        Party party = profile != null ? profile.getParty() : null;
        return profile != null
                && party != null
                && party.getMembers() != null
                && !party.getMembers().isEmpty()
                && party.getMembers().contains(profile.getUuid());
    }
}
