package live.pvphard.practice.feature.leaderboard.command;

import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import live.pvphard.practice.feature.leaderboard.menu.LeaderboardMenu;
import org.bukkit.entity.Player;

public class LeaderboardCommand extends BaseCommand {
    @CommandData(
            name = "leaderboard",
            aliases = {"leaderboards", "lb"},
            usage = "leaderboard",
            description = "View global leaderboards."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        new LeaderboardMenu().openMenu(player);
    }
}