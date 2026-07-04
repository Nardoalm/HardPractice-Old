package live.pvphard.practice.common.animation.internal.config;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.core.config.ConfigService;

/**
 * @author Emmy
 * @project Hard
 * @since 27/03/2025
 */
public class ScoreboardTitleAnimation extends TextAnimation {
    public ScoreboardTitleAnimation() {
        super(HardPractice.getInstance().getService(ConfigService.class).getScoreboardConfig(), "scoreboard.title");
    }
}