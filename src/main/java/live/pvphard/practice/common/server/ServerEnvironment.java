package live.pvphard.practice.common.server;

import live.pvphard.practice.bootstrap.lifecycle.Service;
import org.bukkit.entity.EntityType;

/**
 * @author Remi
 * @project hard-practice
 * @date 2/07/2025
 */
public interface ServerEnvironment extends Service {
    /**
     * Clears all entities of a specific type from all worlds.
     *
     * @param entityType The type of entity to clear.
     */
    void clearEntities(EntityType entityType);

    /**
     * Clears all entities of any type from all worlds.
     * <p>
     * Caution for people who may use this in their own project:
     * This is destructive and will remove players' mounts, item frames, etc.
     */
    void clearAllEntities();
}