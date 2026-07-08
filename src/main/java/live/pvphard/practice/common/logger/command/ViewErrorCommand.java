package live.pvphard.practice.common.logger.command;

import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import live.pvphard.practice.common.logger.Logger;
import live.pvphard.practice.common.text.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author Emmy
 * @project Hard
 * @since 03/04/2025
 */
public class ViewErrorCommand extends BaseCommand {
    @CommandData(
            name = "viewerror",
            isAdminOnly = true,
            inGameOnly = false,
            usage = "viewerror <error>",
            description = "Views a registered error by its ID."
    )
    @Override
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (sender instanceof Player) {
            sender.sendMessage(CC.translate("&cThis command can only be used from the console."));
            return;
        }

        if (args.length == 0) {
            command.sendUsage();
            return;
        }

        try {
            UUID errorId = UUID.fromString(args[0]);
            Logger.viewException(errorId);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(CC.translate("&cInvalid error ID!"));
        }
    }
}
