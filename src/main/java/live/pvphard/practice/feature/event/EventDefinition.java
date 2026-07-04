package live.pvphard.practice.feature.event;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.Location;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
public class EventDefinition {
    private final String key;
    private final boolean enabled;
    private final EventType type;
    private final String displayName;
    private final Material material;
    private final int durability;
    private final String arenaName;
    private final String kitName;
    private final int minimumPlayers;
    private final int maximumPlayers;
    private final int countdownSeconds;
    private final List<String> description;
    private final List<Location> spawnLocations;
    private final Map<String, Location> locations;
    private final Map<String, Object> settings;

    public EventDefinition(String key, boolean enabled, EventType type, String displayName, Material material, int durability,
                           String arenaName, String kitName, int minimumPlayers, int maximumPlayers, int countdownSeconds,
                           List<String> description, List<Location> spawnLocations, Map<String, Location> locations,
                           Map<String, Object> settings) {
        this.key = key;
        this.enabled = enabled;
        this.type = type;
        this.displayName = displayName;
        this.material = material;
        this.durability = durability;
        this.arenaName = arenaName;
        this.kitName = kitName;
        this.minimumPlayers = minimumPlayers;
        this.maximumPlayers = maximumPlayers;
        this.countdownSeconds = countdownSeconds;
        this.description = description;
        this.spawnLocations = spawnLocations == null ? Collections.emptyList() : Collections.unmodifiableList(spawnLocations);
        this.locations = locations == null ? Collections.emptyMap() : Collections.unmodifiableMap(locations);
        this.settings = settings == null ? Collections.emptyMap() : Collections.unmodifiableMap(settings);
    }

    public Location getLocation(String key) {
        return this.locations.get(key);
    }

    public int getIntSetting(String key, int defaultValue) {
        Object value = this.settings.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return defaultValue;
    }

    public double getDoubleSetting(String key, double defaultValue) {
        Object value = this.settings.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return defaultValue;
    }

    public boolean getBooleanSetting(String key, boolean defaultValue) {
        Object value = this.settings.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return defaultValue;
    }

    public String getStringSetting(String key, String defaultValue) {
        Object value = this.settings.get(key);
        return value instanceof String ? (String) value : defaultValue;
    }
}
