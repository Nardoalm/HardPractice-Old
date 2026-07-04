package live.pvphard.practice.feature.spawn.internal;

import live.pvphard.practice.bootstrap.HardContext;
import live.pvphard.practice.bootstrap.annotation.Service;
import live.pvphard.practice.common.PlayerUtil;
import live.pvphard.practice.common.logger.Logger;
import live.pvphard.practice.common.serializer.Serializer;
import live.pvphard.practice.core.locale.LocaleService;
import live.pvphard.practice.core.locale.internal.impl.SettingsLocaleImpl;
import live.pvphard.practice.feature.spawn.SpawnService;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @author Emmy
 * @project Hard
 * @date 17/05/2024 - 17:47
 */
@Getter
@Service(provides = SpawnService.class, priority = 240)
public class SpawnServiceImpl implements SpawnService {
    private final LocaleService localeService;

    private Location location;

    /**
     * DI Constructor for the SpawnServiceImpl class.
     *
     * @param localeService The locale service.
     */
    public SpawnServiceImpl(LocaleService localeService) {
        this.localeService = localeService;
    }

    @Override
    public void initialize(HardContext context) {
        this.loadSpawnLocation();
    }

    private void loadSpawnLocation() {
        Location location = Serializer.deserializeLocation(this.localeService.getString(SettingsLocaleImpl.SERVER_SPAWN_LOCATION));
        if (location == null) {
            Logger.error("Spawn location is null.");
            return;
        }

        this.location = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    @Override
    public void updateSpawnLocation(Location location) {
        if (location == null) return;

        this.location = location;
        this.localeService.setString(SettingsLocaleImpl.SERVER_SPAWN_LOCATION, Serializer.serializeLocation(location));
    }

    @Override
    public void teleportToSpawn(Player player) {
        if (this.location == null) {
            Logger.error("Cannot teleport " + player.getName() + " to spawn: Spawn location is not set.");
            return;
        }

        PlayerUtil.reset(player, false, true);

        Location targetLocation = this.location.clone();
        if (PlayerUtil.canFly(player)) {
            targetLocation.add(0.0D, 4.0D, 0.0D);
        }

        player.teleport(targetLocation);
        player.setFallDistance(0.0F);
    }
}
