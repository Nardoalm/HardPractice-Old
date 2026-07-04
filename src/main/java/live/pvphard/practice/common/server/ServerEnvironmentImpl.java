package live.pvphard.practice.common.server;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.common.server.listener.ServerEnvironmentListener;
import live.pvphard.practice.bootstrap.HardContext;
import live.pvphard.practice.bootstrap.annotation.Service;
import live.pvphard.practice.common.text.CC;
import lombok.Getter;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * This class is made for preparing the server environment.
 * Mainly during startup to pre-setup the server with specific settings.
 *
 * @author Emmy
 * @project Hard
 * @since 03/04/2025
 */
@Getter
@Service(provides = ServerEnvironment.class, priority = 10)
public class ServerEnvironmentImpl implements ServerEnvironment {
    private final HardPractice plugin;

    private final boolean doDaylightCycle;
    private final boolean doWeatherCycle;
    private final boolean doMobSpawning;
    private final boolean doMobLoot;
    private final boolean removeDroppedItemsOnEnable;

    /**
     * Constructor for DI. Receives the main bootstrap instance.
     * Note for emmy: The boolean flags are hardcoded here to match the original instantiation logic.
     */
    public ServerEnvironmentImpl(HardPractice plugin) {
        this.plugin = plugin;
        this.doDaylightCycle = false;
        this.doWeatherCycle = false;
        this.doMobSpawning = false;
        this.doMobLoot = false;
        this.removeDroppedItemsOnEnable = true;
    }

    @Override
    public void initialize(HardContext context) {
        this.plugin.getServer().getPluginManager().registerEvents(new ServerEnvironmentListener(), this.plugin);
        this.setupWorldDefaults();
    }

    @Override
    public void shutdown(HardContext context) {
        this.disconnectPlayers();
        this.clearEntities(EntityType.DROPPED_ITEM);
    }

    /**
     * Applies default settings to all worlds on the server.
     */
    private void setupWorldDefaults() {
        for (World world : this.plugin.getServer().getWorlds()) {
            world.setDifficulty(Difficulty.HARD);
            world.setTime(6000);
            world.setGameRuleValue("doDaylightCycle", String.valueOf(this.doDaylightCycle));
            world.setGameRuleValue("doWeatherCycle", String.valueOf(this.doWeatherCycle));
            world.setGameRuleValue("doMobSpawning", String.valueOf(this.doMobSpawning));
            world.setGameRuleValue("doMobLoot", String.valueOf(this.doMobLoot));

            if (this.removeDroppedItemsOnEnable) {
                clearEntities(world, EntityType.DROPPED_ITEM);
            }
        }
    }

    @Override
    public void clearEntities(EntityType entityType) {
        for (World world : this.plugin.getServer().getWorlds()) {
            clearEntities(world, entityType);
        }
    }

    @Override
    public void clearAllEntities() {
        for (World world : this.plugin.getServer().getWorlds()) {
            world.getEntities().stream()
                    .filter(entity -> !(entity instanceof Player))
                    .forEach(Entity::remove);
        }
    }

    /**
     * Kicks all online players with a restart message.
     */
    private void disconnectPlayers() {
        this.plugin.getServer().getOnlinePlayers().forEach(player ->
                player.kickPlayer(CC.translate("&cThe server is restarting."))
        );
    }

    /**
     * Private helper to clear entities from a single world.
     */
    private void clearEntities(World world, EntityType entityType) {
        world.getEntitiesByClass(entityType.getEntityClass()).forEach(Entity::remove);
    }
}