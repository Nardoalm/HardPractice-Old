package live.pvphard.practice.feature.arena.internal.swm;

import live.pvphard.practice.feature.arena.internal.types.StandAloneArena;

public class NoOpArenaCopyManager implements ArenaCopyManager {

    @Override
    public void initialize() {
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public StandAloneArena createTemporaryArenaCopy(StandAloneArena originalArena, int copyId) {
        return null;
    }

    @Override
    public boolean deleteTemporaryArena(StandAloneArena arena) {
        return false;
    }

    @Override
    public void shutdown() {
    }
}
