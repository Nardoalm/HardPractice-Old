package live.pvphard.practice.common.text;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Emmy
 * @project Hard
 * @since 21/04/2025
 */
@UtilityClass
public class TextFormatter {
    /**
     * Formats a Location object into a string representation.
     *
     * @param location The Location object to format.
     * @return A string in the format "X, Y, Z" or "Not set" if the location is null.
     */
    public String formatLocation(Location location) {
        if (location == null) return CC.translate("&cNot set");
        return location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ();
    }

    /**
     * Automatically centers a list of strings within the specified width,
     * taking into account visible characters only (color codes are ignored).
     *
     * @param lines The list of strings to center.
     * @param width The width to center within.
     * @return A list of centered strings.
     */
    public List<String> centerText(List<String> lines, int width) {
        List<String> centeredLines = new ArrayList<>();

        for (String line : lines) {
            line = line.trim();

            int visibleLength = stripColorCodes(line).length();
            int spaces = Math.max(0, (width - visibleLength) / 2);

            String centeredLine = repeat(spaces) + line + repeat(spaces);

            if (stripColorCodes(centeredLine).length() < width) {
                centeredLine += " ";
            }

            centeredLines.add(centeredLine);
        }

        return centeredLines;
    }

    /**
     * Repeats a space character a specified number of times.
     *
     * @param times The number of times to repeat.
     * @return The resulting string.
     */
    private String repeat(int times) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < times; i++) {
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }

    /**
     * Removes color codes from a string for accurate length measurement.
     *
     * @param text The input string.
     * @return The string without color codes.
     */
    private String stripColorCodes(String text) {
        return text.replaceAll("(?i)§[0-9A-FK-OR]", "")
                .replaceAll("(?i)&[0-9A-FK-OR]", "");
    }
}