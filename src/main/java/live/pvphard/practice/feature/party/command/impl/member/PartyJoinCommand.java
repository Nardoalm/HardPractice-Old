package live.pvphard.practice.feature.party.command.impl.member;

import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import live.pvphard.practice.feature.party.PartyService;
import live.pvphard.practice.feature.party.Party;
import live.pvphard.practice.feature.party.PartyState;
import live.pvphard.practice.common.text.CC;
import org.bukkit.entity.Player;

/**
 * @author Emmy
 * @project Hard
 * @date 16/11/2024 - 23:25
 */
public class PartyJoinCommand extends BaseCommand {
    @CommandData(
            name = "party.join",
            aliases = {"p.join"},
            usage = "party join <player>",
            description = "Joins a player's party."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length < 1) {
            command.sendUsage();
            return;
        }

        Player target = player.getServer().getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(CC.translate("&cThat player is not online."));
            return;
        }

        Party party = this.plugin.getService(PartyService.class).getPartyByLeader(target);
        if (party == null) {
            player.sendMessage(CC.translate("&cThat player is not in a party."));
            return;
        }

        if (party.getState() != PartyState.PUBLIC) {
            player.sendMessage(CC.translate("&cThat party is not open to the public."));
            return;
        }

        this.plugin.getService(PartyService.class).joinParty(player, target);
    }
}