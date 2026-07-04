package live.pvphard.practice.feature.leaderboard;

import lombok.Getter;

/**
 * @author Emmy
 * @project Hard
 * @date 17/11/2024 - 14:13
 */
@Getter
public enum LeaderboardType {
    UNRANKED("Unranked (General)"),
    UNRANKED_MONTHLY("Unranked (Monthly)"),
    WIN_STREAK("Winstreak"),
    FFA("FFA"),
    RANKED("Ranked"),
    TOURNAMENT("Tournament");

    private final String name;

    /**
     * Constructor for the EnumLeaderboardType.
     *
     * @param name The name of the leaderboard type.
     */
    LeaderboardType(String name) {
        this.name = name;
    }
}
