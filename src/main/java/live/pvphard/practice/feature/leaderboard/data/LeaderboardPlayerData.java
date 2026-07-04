package live.pvphard.practice.feature.leaderboard.data;

import live.pvphard.practice.feature.kit.Kit;
import lombok.Data;

import java.util.UUID;

/**
 * @author Emmy
 * @project Hard
 * @date 3/3/2025
 */
@Data
public class LeaderboardPlayerData {
    private final String name;
    private final UUID uuid;
    private final Kit kit;
    private int value;

    /**
     * Constructor for the LeaderboardEntry class.
     *
     * @param name  The name of the player
     * @param uuid  The UUID of the player
     * @param kit   The kit of the player
     * @param value The value of the player
     */
    public LeaderboardPlayerData(String name, UUID uuid, Kit kit, int value) {
        this.name = name;
        this.uuid = uuid;
        this.kit = kit;
        this.value = value;
    }
}