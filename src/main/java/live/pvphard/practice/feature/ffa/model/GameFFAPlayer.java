package live.pvphard.practice.feature.ffa.model;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.feature.ffa.FFAState;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author Emmy
 * @project Hard
 * @since 02/06/2025
 */
@Getter
@Setter
public class GameFFAPlayer {
    private final UUID uuid;
    private final String name;

    private FFAState state;

    /**
     * Constructor for the GameFFAPlayer class.
     *
     * @param uuid The UUID of the player.
     * @param name The name of the player.
     */
    public GameFFAPlayer(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.state = FFAState.SPAWN;
    }

    /**
     * Gets the Player object associated with this GameFFAPlayer.
     *
     * @return The Player object, or null if the player is not online.
     */
    public Player getPlayer() {
        return HardPractice.getInstance().getServer().getPlayer(this.uuid);
    }
}