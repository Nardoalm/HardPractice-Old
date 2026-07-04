package live.pvphard.practice.feature.party.command;

import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.feature.party.Party;
import live.pvphard.practice.feature.party.PartyService;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PartyCommand extends BaseCommand {

    @CommandData(
            name = "party",
            aliases = "p",
            usage = "party",
            description = "Sends a list of party commands."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 1) {
            this.handleDirectInvite(player, args[0]);
            return;
        }

        player.sendMessage("");
        player.sendMessage(CC.translate("§eUsage of §6/party§e:"));
        player.sendMessage(CC.translate(" &e* &6/party create &7| Create a party"));
        player.sendMessage(CC.translate(" &e* &6/party disband &7| Disband your party"));
        player.sendMessage(CC.translate(" &e* &6/party leave &7| Leave your current party"));
        player.sendMessage(CC.translate(" &e* &6/party <player> &7| Invite a player to your party"));
        player.sendMessage(CC.translate(" &e* &6/party join &8(&7player&8) &7| Join a public party"));
        player.sendMessage(CC.translate(" &e* &6/party info &7| View information about your party"));
        player.sendMessage(CC.translate(" &e* &6/party chat &8(&7message&8) &7| Chat with your party"));
        player.sendMessage(CC.translate(" &e* &6/party accept &8(&7player&8) &7| Accept a party invitation"));
        player.sendMessage(CC.translate(" &e* &6/party invite &8(&7player&8) &7| Invite a player to your party"));
        player.sendMessage(CC.translate(" &e* &6/party kick &8(&7player&8) &7| Kick a player from your party"));
        player.sendMessage(CC.translate(" &e* &6/party open &7| Open your party to the public"));
        player.sendMessage(CC.translate(" &e* &6/party close &7| Close your party to the public"));
        player.sendMessage(CC.translate(" &e* &6/party ban &8(&7player&8) &7| Ban a player from your party"));
        player.sendMessage(CC.translate(" &e* &6/party unban &8(&7player&8) &7| Unban a player from your party"));
        player.sendMessage(CC.translate(" &e* &6/party banlist &7| List banned players from your party"));
        player.sendMessage(CC.translate(" &e* &6/party announce &8(&7message&8) &7| Publicly announce your party invitation"));
        player.sendMessage(CC.translate(" &e* &6/party lookup &8(&7player&8) &7| View a player's current party"));
        player.sendMessage("");
    }

    private void handleDirectInvite(Player player, String targetName) {
        PartyService partyService = this.plugin.getService(PartyService.class);
        ProfileService profileService = this.plugin.getService(ProfileService.class);

        Player targetPlayer = Bukkit.getPlayer(targetName);
        if (targetPlayer == null) {
            player.sendMessage(CC.translate("&cThe player you are trying to invite is not online."));
            return;
        }

        if (targetPlayer.equals(player)) {
            player.sendMessage(CC.translate("&cYou cannot invite yourself to a party."));
            return;
        }

        Party party = partyService.getPartyByMember(player.getUniqueId());
        if (party == null) {
            partyService.createParty(player);
            party = partyService.getPartyByMember(player.getUniqueId());
            if (party == null) {
                return;
            }
        }

        if (!party.getLeader().equals(player)) {
            player.sendMessage(CC.translate("&cYou must be the party leader to invite players."));
            return;
        }

        Profile targetProfile = profileService.getProfile(targetPlayer.getUniqueId());
        if (targetProfile == null || targetProfile.getProfileData() == null || targetProfile.getProfileData().getSettingData() == null) {
            player.sendMessage(CC.translate(this.getString(GlobalMessagesLocaleImpl.ERROR_INVALID_PLAYER)));
            return;
        }
        if (!targetProfile.getProfileData().getSettingData().isPartyInvitesEnabled()) {
            player.sendMessage(CC.translate(this.getString(GlobalMessagesLocaleImpl.ERROR_PLAYER_PARTY_INVITES_DISABLED)
                    .replace("{name-color}", String.valueOf(targetProfile.getNameColor()))
                    .replace("{player}", targetName)));
            return;
        }

        if (party.getMembers().contains(targetPlayer.getUniqueId())) {
            player.sendMessage(CC.translate("&6" + targetPlayer.getName() + " &cis already in your party."));
            return;
        }

        if (partyService.sendInvite(party, player, targetPlayer)) {
            party.notifyParty("&6" + targetPlayer.getName() + " &ahas been invited to the party by &6" + player.getName() + "&a.");
        }
    }
}