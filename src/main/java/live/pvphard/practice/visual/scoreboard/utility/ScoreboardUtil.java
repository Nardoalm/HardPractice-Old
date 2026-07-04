package live.pvphard.practice.visual.scoreboard.utility;

import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.common.text.Symbol;
import lombok.experimental.UtilityClass;

/**
 * @author Emmy
 * @project Hard
 * @date 05/10/2024 - 11:05
 */
@UtilityClass
public class ScoreboardUtil {
    /**
     * Visualizes the goals in a scoreboard format.
     *
     * @param currentGoals The current number of goals achieved.
     * @param maxGoals     The maximum number of goals to visualize.
     * @return A string representation of the goals, with filled and empty indicators.
     */
    public String visualizeGoals(int currentGoals, int maxGoals) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < maxGoals; i++) {
            if (i < currentGoals) {
                stringBuilder.append(CC.translate("&a■"));
            } else {
                stringBuilder.append(CC.translate("&7■"));
            }
        }

        return stringBuilder.toString();
    }

    /**
     * Visualizes the bed status in a scoreboard format.
     *
     * @param isBroken Whether the bed is broken or not.
     * @return A string representation of the bed status, with a tick for intact and an X for broken.
     */
    public String visualizeBed(boolean isBroken) {
        return CC.translate(isBroken ? "&c" + Symbol.X : "&a" + Symbol.TICK);
    }
}