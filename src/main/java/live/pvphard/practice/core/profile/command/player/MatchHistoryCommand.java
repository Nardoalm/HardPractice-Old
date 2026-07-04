package live.pvphard.practice.core.profile.command.player;

import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.menu.match.MatchHistorySelectKitMenu;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

/**
 * @author Emmy
 * @project Hard
 * @date 14/09/2024 - 23:05
 */
public class MatchHistoryCommand extends BaseCommand {
    @CommandData(
            name = "matchhistory",
            aliases = {"pastmatches", "previousmatches", "mh"},
            usage = "matchhistory",
            description = "Veja seu histórico de partidas"
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        Profile profile = this.getProfile(player.getUniqueId());
        if (profile.isBusy()) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_YOU_MUST_BE_IN_LOBBY));
            return;
        }

        if (profile.getProfileData().getPreviousMatches().isEmpty()) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_YOU_NO_MATCH_HISTORY));
            return;
        }

        new MatchHistorySelectKitMenu().openMenu(player);
    }
}