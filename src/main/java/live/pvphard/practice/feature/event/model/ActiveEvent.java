package live.pvphard.practice.feature.event.model;

import live.pvphard.practice.feature.arena.Arena;
import live.pvphard.practice.feature.event.EventDefinition;
import live.pvphard.practice.feature.event.EventPhase;
import live.pvphard.practice.feature.event.EventPlayerState;
import live.pvphard.practice.feature.kit.Kit;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
public class ActiveEvent {
    private final EventDefinition definition;
    private final UUID hostUniqueId;
    private final String hostName;
    private final Arena arena;
    private final Kit kit;
    private final Map<UUID, EventParticipant> participants = new LinkedHashMap<>();

    private EventPhase phase = EventPhase.WAITING;
    private int countdownRemaining;
    private int currentRound;

    private UUID currentFighterA;
    private UUID currentFighterB;

    private BukkitTask countdownTask;
    private BukkitTask preparationTask;
    private BukkitTask waterCheckTask;
    private BukkitTask primaryTask;
    private BukkitTask secondaryTask;
    private BukkitTask tertiaryTask;
    private final Map<String, Object> metadata = new HashMap<>();
    private final Set<Location> modifiedBlocks = new HashSet<>();

    public ActiveEvent(EventDefinition definition, UUID hostUniqueId, String hostName, Arena arena, Kit kit) {
        this.definition = definition;
        this.hostUniqueId = hostUniqueId;
        this.hostName = hostName;
        this.arena = arena;
        this.kit = kit;
        this.countdownRemaining = definition.getCountdownSeconds();
    }

    public boolean isParticipant(UUID uuid) {
        return this.participants.containsKey(uuid);
    }

    public boolean isCurrentFighter(UUID uuid) {
        return uuid != null && (uuid.equals(this.currentFighterA) || uuid.equals(this.currentFighterB));
    }

    public Collection<EventParticipant> getAliveParticipants() {
        return this.participants.values();
    }

    public List<EventParticipant> getParticipantsByState(EventPlayerState state) {
        return this.participants.values().stream()
                .filter(participant -> participant.getState() == state)
                .collect(Collectors.toList());
    }

    public EventParticipant getParticipant(UUID uuid) {
        return this.participants.get(uuid);
    }

    public void clearCurrentFight() {
        this.currentFighterA = null;
        this.currentFighterB = null;
    }

    public List<UUID> getWaitingParticipants() {
        List<UUID> waiting = new ArrayList<>();
        for (Map.Entry<UUID, EventParticipant> entry : this.participants.entrySet()) {
            if (entry.getValue().getState() == EventPlayerState.WAITING) {
                waiting.add(entry.getKey());
            }
        }
        return waiting;
    }

    public Player getCurrentFighterAPlayer() {
        return this.currentFighterA == null ? null : Bukkit.getPlayer(this.currentFighterA);
    }

    public Player getCurrentFighterBPlayer() {
        return this.currentFighterB == null ? null : Bukkit.getPlayer(this.currentFighterB);
    }
}
