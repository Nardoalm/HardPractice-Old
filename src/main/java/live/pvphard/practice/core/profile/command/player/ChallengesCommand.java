package live.pvphard.practice.core.profile.command.player;

import live.pvphard.practice.common.constants.MessageConstant;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

/**
 * @author Emmy
 * @project Hard
 * @date 14/09/2024 - 23:03
 */
public class ChallengesCommand extends BaseCommand {
    @CommandData(
            name = "challenges",
            usage = "challenges",
            description = "Veja seus desafios."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        Profile profile = this.getProfile(player.getUniqueId());
        if (profile.isBusy()) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_YOU_MUST_BE_IN_LOBBY));
            return;
        }

        player.sendMessage(MessageConstant.IN_DEVELOPMENT);
    }
}