package live.pvphard.practice.feature.arena.command.impl.manage;

import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.feature.arena.ArenaService;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import live.pvphard.practice.library.command.annotation.CompleterData;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Remi
 * @project Hard
 * @date 5/20/2024
 */
public class ArenaDeleteCommand extends BaseCommand {
    @CompleterData(name = "arena.delete")
    public List<String> arenaDeleteCompleter(CommandArgs command) {
        List<String> completion = new ArrayList<>();
        ArenaService arenaService = this.plugin.getService(ArenaService.class);


        if (command.getArgs().length == 1 && command.getPlayer().hasPermission(this.getAdminPermission())) {
            arenaService.getArenas().forEach(arena -> completion.add(arena.getName()));
        }

        return completion;
    }

    @CommandData(
            name = "arena.delete",
            isAdminOnly = true,
            usage = "arena delete <arenaName>",
            description = "Exclui uma arena"
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        ArenaService arenaService = this.plugin.getService(ArenaService.class);

        if (args.length < 1) {
            command.sendUsage();
            return;
        }

        String arenaName = args[0];
        if (arenaService.getArenaByName(arenaName) == null) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ARENA_NOT_FOUND).replace("{arena-name}", arenaName));
            return;
        }

        arenaService.deleteArena(arenaService.getArenaByName(arenaName));
        player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ARENA_DELETED).replace("{arena-name}", arenaName));
    }
}