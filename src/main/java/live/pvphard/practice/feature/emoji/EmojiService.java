package live.pvphard.practice.feature.emoji;

import live.pvphard.practice.bootstrap.lifecycle.Service;

import java.util.Map;
import java.util.Optional;

/**
 * @author Remi
 * @project hard-practice
 * @date 3/07/2025
 */
public interface EmojiService extends Service {
    /**
     * Gets the map containing all emoji identifiers and their corresponding formats.
     *
     * @return An unmodifiable map of emojis.
     */
    Map<String, String> getEmojis();

    boolean isEnabled();

    /**
     * Gets the format for a specific emoji by its identifier (e.g., ":)").
     *
     * @param identifier The emoji identifier string.
     * @return An Optional containing the format if found.
     */
    Optional<String> getEmojiFormat(String identifier);
}