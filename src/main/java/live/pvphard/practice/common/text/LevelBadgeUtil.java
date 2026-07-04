package live.pvphard.practice.common.text;

import org.bukkit.entity.Player;

public final class LevelBadgeUtil {
    private static final int BASE_XP_PER_LEVEL = 20;
    private static final int XP_INCREMENT_PER_STAGE = 10;
    private static final int EARLY_STAGE_SIZE = 10;
    private static final int LATE_STAGE_SIZE = 100;

    private LevelBadgeUtil() {
    }

    public static String getBadge(int experience) {
        return getBadgeForLevel(getLevel(experience));
    }

    public static String getDisplayBadge(Player player, int experience) {
        return getBadgeForLevel(getDisplayLevel(player, experience));
    }

    public static String getBadgeForLevel(int level) {
        int safeLevel = Math.max(0, level);
        int bracket = getDisplayBracket(safeLevel);
        LevelStyle style = resolveStyle(bracket);
        return format(style.color, safeLevel, style.icon);
    }

    public static int getLevel(int experience) {
        int remainingXp = Math.max(0, experience);
        int stage = 1;
        int level = 0;

        while (remainingXp > 0) {
            int levelsInStage = getLevelsInStage(stage);
            int xpPerLevel = getXpPerLevelForStage(stage);

            if (xpPerLevel <= 0) {
                break;
            }

            int availableLevels = remainingXp / xpPerLevel;
            if (availableLevels <= 0) {
                break;
            }

            int consumedLevels = Math.min(levelsInStage, availableLevels);
            level += consumedLevels;
            remainingXp -= consumedLevels * xpPerLevel;

            if (consumedLevels < levelsInStage) {
                break;
            }

            stage++;
        }

        return Math.max(0, level);
    }

    public static int getDisplayLevel(Player player, int experience) {
        return getLevel(experience);
    }

    public static int getXpRequiredForNextLevel(int currentLevel) {
        int safeLevel = Math.max(0, currentLevel);
        int nextLevel = safeLevel + 1;
        int stage = getStageByLevel(nextLevel);
        return getXpPerLevelForStage(stage);
    }

    public static String getProgressBar(int experience) {
        return getProgressBar(experience, 10);
    }

    public static String getProgressBar(int experience, int length) {
        int safeLength = Math.max(5, length);
        double ratio = getProgressRatio(experience);

        int filled = (int) Math.round(ratio * safeLength);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < safeLength; i++) {
            builder.append(i < filled ? CC.translate("&a■") : CC.translate("&7■"));
        }

        return builder.toString();
    }

    public static String getProgressDetails(int experience) {
        LevelProgress progress = calculateProgress(experience);
        return CC.translate("&7" + progress.currentXp + "/" + progress.requiredXp + " XP");
    }

    /**
     * Gets the progress ratio for the current level, between 0.0 and 1.0.
     *
     * @param experience total accumulated experience.
     * @return ratio of current level progression.
     */
    public static float getProgressRatio(int experience) {
        LevelProgress progress = calculateProgress(experience);
        if (progress.requiredXp <= 0) {
            return 1.0F;
        }

        double ratio = Math.min(1.0D, Math.max(0.0D, (double) progress.currentXp / (double) progress.requiredXp));
        return (float) ratio;
    }

    public static int getDisplayBracket(int level) {
        int safeLevel = Math.max(0, level);
        if (safeLevel <= 100) {
            return Math.max(10, ((safeLevel + 9) / 10) * 10);
        }

        return ((safeLevel + 99) / 100) * 100;
    }

    private static int getLevelsInStage(int stage) {
        return stage <= 10 ? EARLY_STAGE_SIZE : LATE_STAGE_SIZE;
    }

    private static int getXpPerLevelForStage(int stage) {
        int safeStage = Math.max(1, stage);
        return BASE_XP_PER_LEVEL + ((safeStage - 1) * XP_INCREMENT_PER_STAGE);
    }

    private static int getStageByLevel(int level) {
        int safeLevel = Math.max(1, level);

        if (safeLevel <= 100) {
            return ((safeLevel - 1) / EARLY_STAGE_SIZE) + 1;
        }

        return 11 + ((safeLevel - 101) / LATE_STAGE_SIZE);
    }

    private static LevelProgress calculateProgress(int experience) {
        int remainingXp = Math.max(0, experience);
        int stage = 1;
        int level = 0;

        while (remainingXp > 0) {
            int levelsInStage = getLevelsInStage(stage);
            int xpPerLevel = getXpPerLevelForStage(stage);
            int availableLevels = remainingXp / xpPerLevel;

            if (availableLevels <= 0) {
                break;
            }

            int consumedLevels = Math.min(levelsInStage, availableLevels);
            level += consumedLevels;
            remainingXp -= consumedLevels * xpPerLevel;

            if (consumedLevels < levelsInStage) {
                break;
            }

            stage++;
        }

        int requiredXp = getXpRequiredForNextLevel(level);
        return new LevelProgress(Math.max(0, remainingXp), Math.max(1, requiredXp));
    }

    private static LevelStyle resolveStyle(int bracket) {
        if (bracket <= 10) return new LevelStyle("&7", "✺");
        if (bracket <= 20) return new LevelStyle("&a", "❈");
        if (bracket <= 30) return new LevelStyle("&b", "❄");
        if (bracket <= 40) return new LevelStyle("&e", "✵");
        if (bracket <= 50) return new LevelStyle("&b", "✶");
        if (bracket <= 60) return new LevelStyle("&5", "✫");
        if (bracket <= 70) return new LevelStyle("&d", "✿");
        if (bracket <= 80) return new LevelStyle("&c", "❁");
        if (bracket <= 90) return new LevelStyle("&4", "❂");
        if (bracket <= 100) return new LevelStyle("&f", "✦");
        if (bracket <= 200) return new LevelStyle("&2", "☘");
        if (bracket <= 300) return new LevelStyle("&3", "❉");
        if (bracket <= 400) return new LevelStyle("&1", "❊");
        if (bracket <= 500) return new LevelStyle("&e", "✹");
        if (bracket <= 600) return new LevelStyle("&c", "✸");
        if (bracket <= 700) return new LevelStyle("&d", "✾");
        if (bracket <= 800) return new LevelStyle("&b", "❅");
        if (bracket <= 900) return new LevelStyle("&b", "✤");
        if (bracket <= 1000) return new LevelStyle("&4", "❂");

        return new LevelStyle("&4", "❂");
    }

    private static String format(String color, int number, String icon) {
        return CC.translate(color + "[" + number + icon + "]");
    }

    private static final class LevelStyle {
        private final String color;
        private final String icon;

        private LevelStyle(String color, String icon) {
            this.color = color;
            this.icon = icon;
        }
    }

    private static final class LevelProgress {
        private final int currentXp;
        private final int requiredXp;

        private LevelProgress(int currentXp, int requiredXp) {
            this.currentXp = currentXp;
            this.requiredXp = requiredXp;
        }
    }
}
