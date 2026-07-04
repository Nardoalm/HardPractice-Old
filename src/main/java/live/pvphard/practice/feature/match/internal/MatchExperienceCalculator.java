package live.pvphard.practice.feature.match.internal;

public final class MatchExperienceCalculator {
    private static final int BASE_WIN_XP = 20;
    private static final double RANKED_MULTIPLIER = 1.5D;
    private static final double STREAK_STEP = 0.05D;

    private MatchExperienceCalculator() {
    }

    public static int calculateWinXp(boolean ranked, int winStreak) {
        int safeStreak = Math.max(0, winStreak);
        double rankedMultiplier = ranked ? RANKED_MULTIPLIER : 1.0D;
        double streakBonus = Math.min(1.0D, safeStreak * STREAK_STEP);
        double streakMultiplier = 1.0D + streakBonus;
        long computed = Math.round(BASE_WIN_XP * rankedMultiplier * streakMultiplier);
        return computed > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) computed;
    }
}
