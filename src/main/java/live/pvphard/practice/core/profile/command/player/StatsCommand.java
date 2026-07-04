package live.pvphard.practice.core.profile.command.player;

import live.pvphard.practice.common.PlayerUtil;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.core.profile.menu.statistic.StatisticsMenu;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * @author Emmy
 * @project Hard
 * @date 17/11/2024 - 12:07
 */
public class StatsCommand extends BaseCommand {
    @CommandData(
            name = "stats",
            aliases = {"statistics"},
            usage = "stats [player]",
            description = "Veja suas estatísticas ou as de outro jogador."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length < 1) {
            new StatisticsMenu(player).openMenu(player);
            return;
        }

        Player onlineTarget = Bukkit.getPlayerExact(args[0]);
        OfflinePlayer target = onlineTarget != null ? onlineTarget : PlayerUtil.getOfflinePlayerByName(args[0]);
        if (target == null) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_INVALID_PLAYER));
            return;
        }

        new StatisticsMenu(target).openMenu(player);
    }
}