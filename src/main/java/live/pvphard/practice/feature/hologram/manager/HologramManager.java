package live.pvphard.practice.feature.hologram.manager;

import live.pvphard.practice.bootstrap.lifecycle.Service; // Certifique-se de importar o Service correto do seu core
import live.pvphard.practice.feature.hologram.leaderboard.KitLeaderboardHologram;
import live.pvphard.practice.feature.leaderboard.LeaderboardType;
import org.bukkit.Location;

import java.util.List;

public interface HologramManager extends Service {

    void loadHolograms();

    void saveHologram(Location loc, String target, LeaderboardType type);

    boolean removeHologram(String target, LeaderboardType type);

    List<KitLeaderboardHologram> getActiveHolograms();
}