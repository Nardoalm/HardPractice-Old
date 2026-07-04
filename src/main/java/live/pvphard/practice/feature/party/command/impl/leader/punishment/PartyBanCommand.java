package live.pvphard.practice.feature.party.command.impl.leader.punishment;

import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import live.pvphard.practice.feature.party.PartyService;
import live.pvphard.practice.common.text.CC;
import org.bukkit.entity.Player;

/**
 * @author Emmy
 * @project Hard
 * @date 11/12/2024 - 13:25
 */
public class PartyBanCommand extends BaseCommand {
    @CommandData(
            name = "party.ban",
            aliases = "p.ban",
            usage = "party ban <player>",
            description = "Ban a player from your party."
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

        if (target.equals(player)) {
            player.sendMessage(CC.translate("&cYou cannot ban yourself from the party."));
            return;
        }

        this.plugin.getService(PartyService.class).banMember(player, target);
    }
}