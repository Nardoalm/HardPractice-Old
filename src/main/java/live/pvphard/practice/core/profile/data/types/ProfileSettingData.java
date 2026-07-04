package live.pvphard.practice.core.profile.data.types;

import live.pvphard.practice.core.profile.enums.ChatChannel;
import live.pvphard.practice.core.profile.enums.WorldTime;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

/**
 * @author Emmy
 * @project Hard
 * @date 25/05/2024 - 15:22
 */
@Getter
@Setter
public class ProfileSettingData {
    private static final int[] PING_RANGE_OPTIONS = {25, 50, 75, 100, 125, 150, 200, -1};

    //TODO: Clean this class up a bit, make generic methods for toggling settings, use an enum map to store settings, etc.

    /**
     * public class ProfileSettingData {
     * private final EnumMap<ProfileSetting, ToggleState> settings = new EnumMap<>(ProfileSetting.class);
     * private WorldTime worldTime = WorldTime.DEFAULT;
     * <p>
     * public ProfileSettingData() {
     * settings.put(ProfileSetting.PARTY_MESSAGES, ToggleState.ENABLED);
     * settings.put(ProfileSetting.PARTY_INVITES, ToggleState.ENABLED);
     * settings.put(ProfileSetting.SCOREBOARD, ToggleState.ENABLED);
     * settings.put(ProfileSetting.TABLIST, ToggleState.ENABLED);
     * settings.put(ProfileSetting.SCOREBOARD_LINES, ToggleState.ENABLED);
     * settings.put(ProfileSetting.DUEL_REQUESTS, ToggleState.ENABLED);
     * settings.put(ProfileSetting.SERVER_TITLES, ToggleState.ENABLED);
     * }
     * <p>
     * public boolean isEnabled(ProfileSetting setting) {
     * return settings.get(setting).asBoolean();
     * }
     * <p>
     * public void set(ProfileSetting setting, ToggleState state) {
     * settings.put(setting, state);
     * }
     * <p>
     * public void applyWorldTime(Player player) {
     * worldTime.apply(player);
     * }
     * }
     * <p>
     * public enum WorldTime {
     * DEFAULT("Default", (player) -> player.resetPlayerTime()),
     * DAY("Day", (player) -> player.setPlayerTime(6000L, false)),
     * SUNSET("Sunset", (player) -> player.setPlayerTime(12000L, false)),
     * NIGHT("Night", (player) -> player.setPlayerTime(18000L, false));
     * <p>
     * private final String name;
     * private final Consumer<Player> applier;
     * <p>
     * WorldTime(String name, Consumer<Player> applier) {
     * this.name = name;
     * this.applier = applier;
     * }
     * <p>
     * public String getName() {
     * return name;
     * }
     * <p>
     * public void apply(Player player) {
     * applier.accept(player);
     * }
     * }
     * <p>
     * public enum ToggleState {
     * ENABLED, DISABLED;
     * <p>
     * public boolean asBoolean() {
     * return this == ENABLED;
     * }
     * <p>
     * public static ToggleState fromBoolean(boolean value) {
     * return value ? ENABLED : DISABLED;
     * }
     * }
     */

    private boolean partyMessagesEnabled;
    private boolean partyInvitesEnabled;
    private boolean scoreboardEnabled;
    private boolean tablistEnabled;
    private boolean showScoreboardLines;
    private boolean receiveDuelRequestsEnabled;
    private boolean serverTitles;
    private int pingRange;
    private String chatChannel;
    private String time;

    /**
     * Constructor for the ProfileSettingData class.
     */
    public ProfileSettingData() {
        this.partyMessagesEnabled = true;
        this.partyInvitesEnabled = true;
        this.scoreboardEnabled = true;
        this.tablistEnabled = true;
        this.showScoreboardLines = true;
        this.receiveDuelRequestsEnabled = true;
        this.serverTitles = true;
        this.pingRange = 100;
        this.chatChannel = ChatChannel.GLOBAL.toString();
        this.time = WorldTime.DEFAULT.getName();
    }

    /**
     * Cycles to the next configured ping range option.
     */
    public void cyclePingRange() {
        int normalizedCurrent = normalizePingRange(this.pingRange);
        int currentIndex = -1;

        for (int i = 0; i < PING_RANGE_OPTIONS.length; i++) {
            if (PING_RANGE_OPTIONS[i] == normalizedCurrent) {
                currentIndex = i;
                break;
            }
        }

        if (currentIndex == -1) {
            this.pingRange = PING_RANGE_OPTIONS[0];
            return;
        }

        this.pingRange = PING_RANGE_OPTIONS[(currentIndex + 1) % PING_RANGE_OPTIONS.length];
    }

    /**
     * Returns a human-readable ping range string.
     *
     * @return The selected ping range label.
     */
    public String getPingRangeDisplay() {
        int normalized = normalizePingRange(this.pingRange);
        if (normalized <= 0) {
            return "§cOFF";
        }
        return normalized + "ms";
    }

    /**
     * Custom setter with normalization to keep the value in a valid option.
     *
     * @param pingRange The ping range to set.
     */
    public void setPingRange(int pingRange) {
        this.pingRange = normalizePingRange(pingRange);
    }

    private int normalizePingRange(int value) {
        for (int option : PING_RANGE_OPTIONS) {
            if (option == value) {
                return option;
            }
        }

        if (value <= 0) {
            return -1;
        }

        int nearest = PING_RANGE_OPTIONS[0];
        int smallestDiff = Integer.MAX_VALUE;
        for (int option : PING_RANGE_OPTIONS) {
            if (option <= 0) {
                continue;
            }
            int diff = Math.abs(option - value);
            if (diff < smallestDiff) {
                smallestDiff = diff;
                nearest = option;
            }
        }

        return nearest;
    }

    /**
     * Set the world time for a player to the default time.
     *
     * @param player The player to set the world time for.
     */
    public void setTimeDefault(Player player) {
        this.time = WorldTime.DEFAULT.getName();
        player.resetPlayerTime();
    }

    /**
     * Set the world time for a player to day.
     *
     * @param player The player to set the world time for.
     */
    public void setTimeDay(Player player) {
        this.time = WorldTime.DAY.getName();
        player.setPlayerTime(6000L, false);
    }

    /**
     * Set the world time for a player to sunset.
     *
     * @param player The player to set the world time for.
     */
    public void setTimeSunset(Player player) {
        this.time = WorldTime.SUNSET.getName();
        player.setPlayerTime(12000, false);
    }

    /**
     * Set the world time for a player to night.
     *
     * @param player The player to set the player time for.
     */
    public void setTimeNight(Player player) {
        this.time = WorldTime.NIGHT.getName();
        player.setPlayerTime(18000L, false);
    }

    /**
     * Get the world time based on the profile setting.
     *
     * @return The world time based on the profile setting.
     */
    public WorldTime getWorldTime() {
        return WorldTime.getByName(this.time);
    }

    /**
     * Set the world time based on the profile setting.
     *
     * @param player The player to set the world time for.
     */
    public void setTimeBasedOnProfileSetting(Player player) {
        switch (this.getWorldTime()) {
            case DEFAULT:
                this.setTimeDefault(player);
                break;
            case DAY:
                this.setTimeDay(player);
                break;
            case SUNSET:
                this.setTimeSunset(player);
                break;
            case NIGHT:
                this.setTimeNight(player);
                break;
        }
    }

    /**
     * Check if the player is in day time.
     *
     * @return True if the player is in day time.
     */
    public boolean isDayTime() {
        return this.time.equals(WorldTime.DAY.getName());
    }

    /**
     * Check if the player is in sunset time.
     *
     * @return True if the player is in sunset time.
     */
    public boolean isSunsetTime() {
        return this.time.equals(WorldTime.SUNSET.getName());
    }

    /**
     * Check if the player is in night time.
     *
     * @return True if the player is in night time.
     */
    public boolean isNightTime() {
        return this.time.equals(WorldTime.NIGHT.getName());
    }

    /**
     * Check if the player is in default time.
     *
     * @return True if the player is in default time.
     */
    public boolean isDefaultTime() {
        return this.time.equals(WorldTime.DEFAULT.getName());
    }
}
