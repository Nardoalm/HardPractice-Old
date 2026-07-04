package live.pvphard.practice.feature.spawn.command;

import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.feature.spawn.SpawnService;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Objects;

/**
 * @author Emmy
 * @project Hard
 * @date 29/04/2024 - 18:41
 */
public class SetSpawnCommand extends BaseCommand {
    @CommandData(
            name = "setspawn",
            isAdminOnly = true,
            usage = "setspawn",
            description = "Define o ponto de spawn do servidor."
    )
    @Override
    public void onCommand(CommandArgs cmd) {
        Player player = (Player) cmd.getSender();

        Location location = player.getLocation();
        this.plugin.getService(SpawnService.class).updateSpawnLocation(location);

        player.sendMessage(this.getString(GlobalMessagesLocaleImpl.SPAWN_SET)
                .replace("{world}", Objects.requireNonNull(location.getWorld()).getName())
                .replace("{x}", String.format("%.2f", location.getX()))
                .replace("{y}", String.format("%.2f", location.getY()))
                .replace("{z}", String.format("%.2f", location.getZ()))
                .replace("{yaw}", String.format("%.2f", location.getYaw()))
                .replace("{pitch}", String.format("%.2f", location.getPitch()))
        );
    }
}