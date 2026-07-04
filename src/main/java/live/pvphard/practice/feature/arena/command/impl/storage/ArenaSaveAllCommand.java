package live.pvphard.practice.feature.arena.command.impl.storage;

import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.feature.arena.Arena;
import live.pvphard.practice.feature.arena.ArenaService;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

/**
 * @author Remi
 * @project Hard
 * @date 5/20/2024
 */
public class ArenaSaveAllCommand extends BaseCommand {
    @CommandData(
            name = "arena.saveall",
            isAdminOnly = true,
            usage = "arena saveall",
            description = "Salva todas as arenas no armazenamento"
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        for (Arena arena : this.plugin.getService(ArenaService.class).getArenas()) {
            this.plugin.getService(ArenaService.class).saveArena(arena);
        }

        player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ARENA_SAVED_ALL));
    }
}
