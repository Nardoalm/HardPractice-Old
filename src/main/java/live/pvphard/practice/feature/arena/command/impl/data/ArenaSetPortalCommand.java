package live.pvphard.practice.feature.arena.command.impl.data;

import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.feature.arena.Arena;
import live.pvphard.practice.feature.arena.ArenaService;
import live.pvphard.practice.feature.arena.ArenaType;
import live.pvphard.practice.feature.arena.internal.types.StandAloneArena;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

/**
 * @author Emmy
 * @project Hard
 * @since 02/03/2025
 */
public class ArenaSetPortalCommand extends BaseCommand {
    @CommandData(
            name = "arena.setportal",
            isAdminOnly = true,
            usage = "arena setportal <name> <red/blue>",
            description = "Define a localização do portal para uma arena standalone."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length < 2) {
            command.sendUsage();
            return;
        }

        ArenaService arenaService = this.plugin.getService(ArenaService.class);
        String name = args[0];

        Arena arena = arenaService.getArenaByName(name);
        if (arena == null) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ARENA_NOT_FOUND).replace("{arena-name}", name));
            return;
        }

        if (arena.getType() != ArenaType.STANDALONE) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ARENA_MUST_BE_STANDALONE).replace("{arena-name}", arena.getName()));
            return;
        }

        String portal = args[1];
        if (!portal.equalsIgnoreCase("red") && !portal.equalsIgnoreCase("blue")) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ARENA_INVALID_PORTAL));
            return;
        }

        StandAloneArena standAloneArena = (StandAloneArena) arena;
        if (portal.equalsIgnoreCase("red")) {
            standAloneArena.setTeam1Portal(player.getLocation());
        } else if (portal.equalsIgnoreCase("blue")) {
            standAloneArena.setTeam2Portal(player.getLocation());
        }

        arenaService.saveArena(arena);
        player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ARENA_PORTAL_SET)
                .replace("{arena-name}", arena.getName())
                .replace("{portal}", portal)
        );
    }
}