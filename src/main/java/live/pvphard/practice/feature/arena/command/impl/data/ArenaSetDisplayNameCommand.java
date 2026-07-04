package live.pvphard.practice.feature.arena.command.impl.data;

import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.feature.arena.Arena;
import live.pvphard.practice.feature.arena.ArenaService;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

/**
 * @author Emmy
 * @project Hard
 * @date 15/09/2024 - 11:45
 */
public class ArenaSetDisplayNameCommand extends BaseCommand {
    @CommandData(
            name = "arena.setdisplayname",
            isAdminOnly = true,
            inGameOnly = false,
            usage = "arena setdisplayname <arenaName> <displayName>",
            description = "Define o nome de exibição de uma arena"
    )
    @Override
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (args.length < 2) {
            command.sendUsage();
            return;
        }

        String arenaName = args[0];
        ArenaService arenaService = this.plugin.getService(ArenaService.class);
        Arena arena = arenaService.getArenaByName(arenaName);
        if (arena == null) {
            sender.sendMessage(this.getString(GlobalMessagesLocaleImpl.ARENA_NOT_FOUND).replace("{arena-name}", arenaName));
            return;
        }

        String displayName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        arena.setDisplayName(displayName);
        arenaService.saveArena(arena);

        sender.sendMessage(this.getString(GlobalMessagesLocaleImpl.ARENA_DISPLAY_NAME_SET)
                .replace("{arena-name}", arenaName)
                .replace("{display-name}", displayName)
        );
    }
}