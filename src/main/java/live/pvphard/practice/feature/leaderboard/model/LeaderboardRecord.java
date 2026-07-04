package live.pvphard.practice.feature.leaderboard.model;

import live.pvphard.practice.feature.leaderboard.data.LeaderboardPlayerData;
import live.pvphard.practice.feature.leaderboard.LeaderboardType;
import lombok.Getter;

import java.util.List;

/**
 * @author Emmy
 * @project Hard
 * @since 03/03/2025
 */
@Getter
public class LeaderboardRecord {
    private final LeaderboardType type;
    private final List<LeaderboardPlayerData> participants;

    /**
     * Constructor for the LeaderboardRecord class.
     *
     * @param type         The type of the leaderboard.
     * @param participants The participants of the leaderboard.
     */
    public LeaderboardRecord(LeaderboardType type, List<LeaderboardPlayerData> participants) {
        this.type = type;
        this.participants = participants;
    }
}
