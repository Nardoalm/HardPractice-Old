package live.pvphard.practice.common.text;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;

/**
 * @author Emmy
 * @project Hard
 * @since 27/03/2025
 */
@UtilityClass
public class ProgressBarUtil {
    /**
     * Generates a progress bar based on the given values.
     *
     * @param current the current value
     * @param maximum the max value
     * @param length  the total number of bars (usually 20 or 40)
     * @param symbol  the symbol to use for the progress bar (Default: ▎)
     * @return the progress bar string with the given values.
     */
    public String generate(int current, int maximum, int length, String symbol) {
        if (maximum <= 0) throw new IllegalArgumentException("Max value must be greater than zero.");

        ChatColor progressColor = ChatColor.GREEN;
        ChatColor pendingColor = ChatColor.GRAY;

        int progressBars = Math.round(length * ((float) current / maximum));
        StringBuilder bar = new StringBuilder();

        for (int i = 0; i < length; i++) {
            bar.append(i < progressBars ? progressColor : pendingColor).append(symbol);
        }

        return bar.toString();
    }
}