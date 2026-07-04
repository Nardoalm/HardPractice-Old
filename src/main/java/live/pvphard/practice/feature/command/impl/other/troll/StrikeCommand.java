package live.pvphard.practice.feature.command.impl.other.troll;

import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

/**
 * @author Emmy
 * @project Hard
 * @date 25/06/2024 - 19:42
 */
public class StrikeCommand extends BaseCommand {
    @CommandData(
            name = "strike",
            isAdminOnly = true,
            inGameOnly = true,
            usage = "strike <player> | all",
            description = "Atinge um jogador com um raio"
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length < 1) {
            command.sendUsage();
            return;
        }

        String targetName = args[0];
        Player targetPlayer = player.getServer().getPlayer(targetName);

        if (targetPlayer == null) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_INVALID_PLAYER));
            return;
        }

        targetPlayer.getWorld().strikeLightning(targetPlayer.getLocation());
        player.sendMessage(this.getString(GlobalMessagesLocaleImpl.TROLL_PLAYER_STRUCK_BY_LIGHTNING)
                .replace("{name-color}", String.valueOf(this.getProfile(targetPlayer.getUniqueId()).getNameColor()))
                .replace("{player}", targetPlayer.getName())
        );
    }
}