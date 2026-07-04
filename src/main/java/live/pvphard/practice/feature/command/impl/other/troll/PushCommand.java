package live.pvphard.practice.feature.command.impl.other.troll;

import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

/**
 * @author Emmy
 * @project Hard
 * @date 25/06/2024 - 20:26
 */
public class PushCommand extends BaseCommand {
    @CommandData(
            name = "push",
            isAdminOnly = true,
            inGameOnly = false,
            usage = "push <player> <value>",
            description = "Empurra um jogador"
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length < 2) {
            command.sendUsage();
            return;
        }

        String targetName = args[0];
        Player targetPlayer = player.getServer().getPlayer(targetName);

        if (targetPlayer == null) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_INVALID_PLAYER));
            return;
        }

        double value;

        try {
            value = Double.parseDouble(args[1]);
        } catch (NumberFormatException exception) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_INVALID_NUMBER).replace("{input}", args[1]));
            return;
        }

        if (value > 10) {
            player.sendMessage(CC.translate("&cO valor não pode ser maior que 10."));
            return;
        }

        targetPlayer.setVelocity(player.getLocation().getDirection().multiply(value));

        player.sendMessage(this.getString(GlobalMessagesLocaleImpl.TROLL_PLAYER_PUSHED)
                .replace("{name-color}", String.valueOf(this.getProfile(targetPlayer.getUniqueId()).getNameColor()))
                .replace("{player}", targetPlayer.getName())
        );
    }
}
