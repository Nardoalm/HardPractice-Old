package live.pvphard.practice.feature.party.command.impl.leader.punishment;

import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import live.pvphard.practice.feature.party.PartyService;
import live.pvphard.practice.common.text.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @author Emmy
 * @project Hard
 * @date 11/12/2024 - 13:46
 */
public class PartyBanListCommand extends BaseCommand {
    @CommandData(
            name = "party.banlist",
            aliases = "p.banlist",
            usage = "party banlist",
            description = "Envia uma lista de todos os banned members in your party."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        player.sendMessage("");
        player.sendMessage(CC.translate("&c&lMembros Banidos da sua party:"));
        this.plugin.getService(PartyService.class).getPartyByLeader(player).getBannedPlayers().forEach(bannedMember -> player.sendMessage(CC.translate("&7- &c" + Bukkit.getPlayer(bannedMember).getName())));
        player.sendMessage("");
    }
}