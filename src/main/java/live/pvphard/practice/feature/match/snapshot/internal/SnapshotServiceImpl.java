package live.pvphard.practice.feature.match.snapshot.internal;

import live.pvphard.practice.bootstrap.annotation.Service;
import live.pvphard.practice.feature.match.snapshot.Snapshot;
import live.pvphard.practice.feature.match.snapshot.SnapshotService;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Remi
 * @project Hard
 * @date 5/26/2024
 */
@Getter
@Service(provides = SnapshotService.class, priority = 210)
public class SnapshotServiceImpl implements SnapshotService {
    private final Map<UUID, Snapshot> snapshots = new ConcurrentHashMap<>();

    @Override
    public Map<UUID, Snapshot> getSnapshots() {
        return snapshots;
    }

    @Override
    public void addSnapshot(Snapshot snapshot) {
        if (snapshot != null) {
            this.snapshots.put(snapshot.getUuid(), snapshot);
        }
    }

    @Override
    public Snapshot getSnapshot(UUID uuid) {
        return this.snapshots.get(uuid);
    }

    @Override
    public Snapshot getSnapshot(String username) {
        return this.snapshots.values().stream()
                .filter(snapshot -> snapshot.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void removeSnapshot(UUID uuid) {
        this.snapshots.remove(uuid);
    }
}