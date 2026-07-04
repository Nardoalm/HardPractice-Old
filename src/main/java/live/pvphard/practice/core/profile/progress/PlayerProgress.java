package live.pvphard.practice.core.profile.progress;

import live.pvphard.practice.common.text.ProgressBarUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Remi
 * @project hard-practice
 * @date 2/07/2025
 */
@Getter
@RequiredArgsConstructor
public class PlayerProgress {
    private final int currentWins;
    private final int winsForNextTier;
    private final String nextRankName;
    private final boolean isMaxRank;

    /**
     * Generates a visual progress bar string.
     * @param length The desired length of the bar.
     * @param symbol The character to use for the bar.
     * @return A formatted progress bar.
     */
    public String getProgressBar(int length, String symbol) {
        return ProgressBarUtil.generate(currentWins, winsForNextTier, length, symbol);
    }

    /**
     * @return The progress as a formatted percentage string (e.g., "75%").
     */
    public String getProgressPercentage() {
        if (isMaxRank) return "100%";
        return winsForNextTier > 0 ? Math.round((float) currentWins / winsForNextTier * 100) + "%" : "100%";
    }

    /**
     * @return The number of additional wins required to reach the next tier.
     */
    public int getWinsRequired() {
        if (isMaxRank) return 0;
        return Math.max(0, winsForNextTier - currentWins);
    }

    /**
     * @return The word "win" or "wins" based on the number of required wins.
     */
    public String getWinOrWins() {
        return getWinsRequired() == 1 ? "win" : "wins";
    }
}