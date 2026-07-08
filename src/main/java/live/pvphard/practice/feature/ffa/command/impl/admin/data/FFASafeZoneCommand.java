package live.pvphard.practice.feature.ffa.command.impl.admin.data;

import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.feature.arena.ArenaService;
import live.pvphard.practice.feature.arena.ArenaType;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

/**
 * @author Emmy
 * @project Hard
 * @date 12/06/2024 - 21:56
 */
public class FFASafeZoneCommand extends BaseCommand {
    @Override
    @CommandData(
            name = "ffa.safezone",
            isAdminOnly = true,
            usage = "ffa safezone <arenaName> <pos1/pos2>",
            description = "Sets the safezone positions for an FFA arena."
    )
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

        if (arenaService.getArenaByName(arenaName) == null) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ARENA_NOT_FOUND).replace("{arena-name}", arenaName));
            return;
        }

        if (arenaService.getArenaByName(arenaName).getType() != ArenaType.FFA) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.FFA_CAN_ONLY_SETUP_IN_FFA_ARENA));
            return;
        }

        if (!spawnType.equalsIgnoreCase("pos1") && !spawnType.equalsIgnoreCase("pos2")) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.FFA_INVALID_SPAWN_TYPE));
            return;
        }

        if (spawnType.equalsIgnoreCase("pos1")) {
            arenaService.getArenaByName(arenaName).setMaximum(player.getLocation());
        } else {
            arenaService.getArenaByName(arenaName).setMinimum(player.getLocation());
        }

        player.sendMessage(this.getString(GlobalMessagesLocaleImpl.FFA_SAFE_ZONE_SET)
                .replace("{arena-name}", arenaName)
                .replace("{pos}", spawnType.equalsIgnoreCase("pos1") ? "position 1" : "position 2")
        );
        arenaService.saveArena(arenaService.getArenaByName(arenaName));
    }
}