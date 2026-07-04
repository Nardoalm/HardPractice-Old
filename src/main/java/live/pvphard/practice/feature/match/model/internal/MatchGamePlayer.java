package live.pvphard.practice.feature.match.model.internal;

import live.pvphard.practice.feature.match.model.GamePlayer;
import live.pvphard.practice.feature.match.model.MatchGamePlayerData;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * @author Remi
 * @project Hard
 * @date 5/21/2024
 */
@Setter
@Getter
public class MatchGamePlayer extends GamePlayer {
    private final MatchGamePlayerData data;
    private int elo;

    /**
     * Constructor for the MatchGamePlayerImpl class.
     *
     * @param uuid     The UUID of the player.
     * @param username The username of the player.
     * @param elo      The elo of the player.
     */
    public MatchGamePlayer(UUID uuid, String username, int elo) {
        super(uuid, username);
        this.data = new MatchGamePlayerData();
        this.elo = elo;
    }

    /**
     * Constructor for the MatchGamePlayerImpl class.
     *
     * @param uuid     The UUID of the player.
     * @param username The username of the player.
     */
    public MatchGamePlayer(UUID uuid, String username) {
        this(uuid, username, 0);
    }
}
