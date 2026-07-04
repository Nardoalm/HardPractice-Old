package live.pvphard.practice.feature.arena.internal.swm;

import live.pvphard.practice.feature.arena.internal.types.StandAloneArena;

public interface ArenaCopyManager {
    void initialize();

    boolean isEnabled();

    StandAloneArena createTemporaryArenaCopy(StandAloneArena originalArena, int copyId);

    boolean deleteTemporaryArena(StandAloneArena arena);

    void shutdown();
}
