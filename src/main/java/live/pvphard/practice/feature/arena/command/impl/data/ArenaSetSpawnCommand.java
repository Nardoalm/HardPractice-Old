package live.pvphard.practice.feature.arena.command.impl.data;

import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.feature.arena.Arena;
import live.pvphard.practice.feature.arena.ArenaService;
import live.pvphard.practice.feature.arena.ArenaType;
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
public class ArenaSetSpawnCommand extends BaseCommand {
    @CompleterData(name = "arena.setspawn")
    public List<String> arenaSetSpawnCompleter(CommandArgs command) {
        List<String> completion = new ArrayList<>();

        if (command.getArgs().length == 1 && command.getPlayer().hasPermission(this.getAdminPermission())) {
            this.plugin.getService(ArenaService.class).getArenas().forEach(arena -> completion.add(arena.getName()));
        }

        return completion;
    }

    @CommandData(
            name = "arena.setspawn",
            isAdminOnly = true,
            usage = "arena setspawn <arenaName> <blue/red/ffa>",
            description = "Define a localização de spawn de uma arena."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length < 2) {
            command.sendUsage();
            return;
        }

        String arenaName = args[0];
        String spawnType = args[1];

        ArenaService arenaService = this.plugin.getService(ArenaService.class);
        Arena arena = arenaService.getArenaByName(arenaName);
        if (arena == null) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ARENA_NOT_FOUND).replace("{arena-name}", arenaName));
            return;
        }

        if (!spawnType.equalsIgnoreCase("blue") && !spawnType.equalsIgnoreCase("red") && !spawnType.equalsIgnoreCase("ffa")) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ARENA_INVALID_SPAWN_TYPE));
            return;
        }

        switch (spawnType.toLowerCase()) {
            case "blue":
                if (arena.getType() == ArenaType.FFA) {
                    player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ARENA_FFA_ARENAS_NO_SPAWNS));
                    return;
                }
                arena.setPos1(player.getLocation());
                player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ARENA_SPAWN_SET)
                        .replace("{arena-name}", arenaName)
                        .replace("{position}", "blue")
                );
                break;
            case "ffa":
                if (arena.getType() != ArenaType.FFA) {
                    player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ARENA_IS_NOT_FFA));
                    return;
                }
                arena.setPos1(player.getLocation());
                player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ARENA_FFA_SPAWN_SET).replace("{arena-name}", arenaName));
                break;
            default:
                if (arena.getType() == ArenaType.FFA) {
                    player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ARENA_FFA_ARENAS_NO_SPAWNS));
                    return;
                }
                arena.setPos2(player.getLocation());
                player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ARENA_SPAWN_SET)
                        .replace("{arena-name}", arenaName)
                        .replace("{position}", "red")
                );
                break;
        }

        arenaService.saveArena(arena);
    }
}
