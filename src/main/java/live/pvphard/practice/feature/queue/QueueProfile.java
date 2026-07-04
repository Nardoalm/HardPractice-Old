package live.pvphard.practice.feature.queue;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.common.reflect.ReflectionService;
import live.pvphard.practice.common.reflect.internal.types.ActionBarReflectionServiceImpl;
import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.common.time.TimeUtil;
import live.pvphard.practice.core.locale.LocaleService;
import live.pvphard.practice.core.locale.internal.impl.VisualsLocaleImpl;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.ProfileService;
import lombok.Data;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

/**
 * @author Remi
 * @project Hard
 * @date 5/21/2024
 */
@Data
public class QueueProfile {
    private final int TICK_THRESHOLD = 6;
    private final int RANGE_INCREMENT = 10;
    private final int MAX_RANGE = 220;

    private final Queue queue;
    private final UUID uuid;
    private int range;
    private int ticks;
    private int elo;

    private long startTime = System.currentTimeMillis();

    /**
     * Constructor for the QueueProfile class.
     *
     * @param queue The queue.
     * @param uuid  The UUID of the queue.
     */
    public QueueProfile(Queue queue, UUID uuid) {
        this.queue = queue;
        this.uuid = uuid;
    }

    /**
     * Method to queue the player.
     *
     * @param player The player to queue.
     */
    public void queueRange(Player player) {
        LocaleService localeService = HardPractice.getInstance().getService(LocaleService.class);

        this.ticks++;

        if (player != null) {
            if (localeService.getBoolean(VisualsLocaleImpl.ACTIONBAR_QUEUE_INDICATOR_ENABLED_BOOLEAN)) {
                String message = localeService.getString(VisualsLocaleImpl.ACTIONBAR_QUEUE_INDICATOR_MESSAGE_FORMAT)
                        .replace("{queue-type}", this.queue.getQueueType())
                        .replace("{kit-name}", this.queue.getKit().getDisplayName())
                        .replace("{elapsed-time}", TimeUtil.getFormattedElapsedTime(getElapsedTime()));
                ReflectionService reflectionService = HardPractice.getInstance().getService(ReflectionService.class);
                reflectionService.getReflectionService(ActionBarReflectionServiceImpl.class).sendMessage(player, message);
            }
        }

        if (this.ticks % this.TICK_THRESHOLD != 0) {
            return;
        }

        if (!this.queue.isRanked()) {
            if (player != null) {
                if (localeService.getBoolean(GlobalMessagesLocaleImpl.QUEUE_PROGRESSING_UNRANKED_BOOLEAN)) {
                    List<String> lines = localeService.getStringList(GlobalMessagesLocaleImpl.QUEUE_PROGRESSING_UNRANKED);
                    lines.replaceAll(line -> line
                            .replace("{kit}", this.queue.getKit().getDisplayName())
                            .replace("{ping-range}", this.getPingRangeDisplay(player))
                    );
                    lines.forEach(line -> player.sendMessage(CC.translate(line)));
                }
            }
            return;
        }

        if (this.range >= this.MAX_RANGE) {
            return;
        }

        int previousRange = this.range;
        this.range = Math.min(this.range + this.RANGE_INCREMENT, this.MAX_RANGE);

        if (this.range != previousRange) {
            if (player != null) {
                if (this.range == this.MAX_RANGE) {
                    if (localeService.getBoolean(GlobalMessagesLocaleImpl.QUEUE_PROGRESSING_RANKED_LIMIT_REACHED_BOOLEAN)) {
                        for (String line : localeService.getStringList(GlobalMessagesLocaleImpl.QUEUE_PROGRESSING_RANKED_LIMIT_REACHED)) {
                            line = line.replace("{kit}", this.queue.getKit().getDisplayName())
                                    .replace("{min-elo}", String.valueOf(this.getMinimumElo()))
                                    .replace("{max-elo}", String.valueOf(this.getMaximumElo()))
                                    .replace("{ping-range}", this.getPingRangeDisplay(player));
                            player.sendMessage(CC.translate(line));
                        }
                    }
                } else {
                    if (localeService.getBoolean(GlobalMessagesLocaleImpl.QUEUE_PROGRESSING_RANKED_BOOLEAN)) {
                        for (String line : localeService.getStringList(GlobalMessagesLocaleImpl.QUEUE_PROGRESSING_RANKED)) {
                            line = line.replace("{kit}", this.queue.getKit().getDisplayName())
                                    .replace("{min-elo}", String.valueOf(this.getMinimumElo()))
                                    .replace("{max-elo}", String.valueOf(this.getMaximumElo()))
                                    .replace("{ping-range}", this.getPingRangeDisplay(player));
                            player.sendMessage(CC.translate(line));
                        }
                    }
                }
            }
        }
    }

    private String getPingRangeDisplay(Player player) {
        if (player == null) {
            return "N/D";
        }

        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        if (profileService == null) {
            return "N/D";
        }

        Profile profile = profileService.getProfile(player.getUniqueId());
        if (profile == null || profile.getProfileData() == null || profile.getProfileData().getSettingData() == null) {
            return "100ms";
        }

        return profile.getProfileData().getSettingData().getPingRangeDisplay();
    }

    /**
     * Method to get the minimum elo.
     *
     * @return The minimum elo.
     */
    private int getMinimumElo() {
        int minimumElo = this.elo - this.range;
        return Math.max(minimumElo, 0);
    }

    /**
     * Method to get the maximum elo.
     *
     * @return The maximum elo.
     */
    private int getMaximumElo() {
        int max = this.elo + this.range;
        return Math.min(max, 3000);
    }

    /**
     * Method to get the elapsed time.
     *
     * @return The elapsed time.
     */
    public long getElapsedTime() {
        return System.currentTimeMillis() - this.startTime;
    }
}
