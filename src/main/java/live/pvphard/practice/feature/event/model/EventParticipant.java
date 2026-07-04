package live.pvphard.practice.feature.event.model;

import live.pvphard.practice.feature.event.EventPlayerState;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.UUID;

@Getter
@Setter
public class EventParticipant {
    private final UUID uuid;
    private EventPlayerState state = EventPlayerState.WAITING;
    private int score;
    private int progress;
    private boolean marked;
    private Location checkpoint;
    private Location lastKnownLocation;

    public EventParticipant(UUID uuid) {
        this.uuid = uuid;
    }
}
