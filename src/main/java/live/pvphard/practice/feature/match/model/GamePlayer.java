package live.pvphard.practice.feature.match.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Remi
 * @project Hard
 * @date 5/21/2024
 */
@Data
@Getter
@Setter
public class GamePlayer {
    private UUID uuid;
    private String username;

    private boolean disconnected;
    private boolean eliminated;
    private boolean dead;

    private List<UUID> players;

    // These fields are all for game logic
    private Location checkpoint;
    private int checkpointCount;
    private final List<Location> checkpoints;

    /**
     * Constructor for the GamePlayer class.
     *
     * @param uuid     The UUID of the player.
     * @param username The username of the player.
     */
    public GamePlayer(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
        this.players = new ArrayList<>();

        this.checkpoints = new ArrayList<>();
        this.checkpointCount = 0;
        this.checkpoint = null;
    }

    /**
     * Gets the player associated with the GamePlayer.
     *
     * @return The player associated with the GamePlayer.
     */
    public Player getTeamPlayer() {
        return Bukkit.getPlayer(uuid);
    }
}