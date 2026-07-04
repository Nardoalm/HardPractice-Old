package live.pvphard.practice.library.assemble;

import live.pvphard.practice.bootstrap.lifecycle.Service;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

/**
 * @author Remi
 * @project hard-practice
 * @date 2/07/2025
 */
public interface AssembleService extends Service {
    /**
     * Gets the map of all active scoreboard instances.
     *
     * @return A map where the key is the player's UUID and the value is their AssembleBoard.
     */
    Map<UUID, AssembleBoard> getBoards();

    /**
     * Gets the adapter that is currently providing content (title and lines) to the scoreboard.
     *
     * @return The active IAssembleAdapter instance.
     */
    AssembleAdapter getAdapter();

    boolean isCallEvents();

    /**
     * Creates and registers a new scoreboard for a player.
     * This should be called when a player joins.
     *
     * @param player The player to create the board for.
     */
    void createBoard(Player player);

    /**
     * Removes the scoreboard for a player.
     * This should be called when a player quits.
     *
     * @param player The player whose board to remove.
     */
    void removeBoard(Player player);
}