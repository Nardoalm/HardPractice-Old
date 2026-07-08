package live.pvphard.practice.feature.party.command.impl.external;

import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.feature.party.Party;
import live.pvphard.practice.feature.party.PartyService;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author Emmy
 * @project Hard
 * @since 23/01/2025
 */
public class PartyLookupCommand extends BaseCommand {
    @CommandData(
            name = "party.lookup",
            usage = "party lookup <player>",
            description = "Sends info about a specific party",
            cooldown = 1
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length < 1) {
            command.sendUsage();
            return;
        }

        Player target = this.plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_INVALID_PLAYER));
            return;
        }

        Party party = this.plugin.getService(PartyService.class).getParty(target);
        if (party == null) {
            player.sendMessage(CC.translate("&cThis player is not in a party."));
            return;
        }

        List<String> message = this.getStringList(GlobalMessagesLocaleImpl.PARTY_LOOKUP);
        message.replaceAll(line -> line
                .replace("{name-color}", String.valueOf(this.getProfile(party.getLeader().getUniqueId()).getNameColor()))
                .replace("{leader}", party.getLeader().getName())
                .replace("{members}", String.valueOf(party.getMembers().size()))
                .replace("{status}", party.getState().getName())
                .replace("{privacy-desc}", party.getState().getDescription())
                .replace("{privacy}", party.getState().getName())
        );

        message.forEach(line -> player.sendMessage(CC.translate(line)));
    }
}