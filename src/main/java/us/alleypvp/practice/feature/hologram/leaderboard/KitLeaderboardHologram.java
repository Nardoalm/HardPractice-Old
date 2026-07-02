package us.alleypvp.practice.feature.hologram.leaderboard;

import us.alleypvp.practice.AlleyPractice;
import us.alleypvp.practice.feature.hologram.Hologram;
import us.alleypvp.practice.feature.kit.Kit;
import us.alleypvp.practice.feature.leaderboard.LeaderboardService;
import us.alleypvp.practice.feature.leaderboard.LeaderboardType;
import us.alleypvp.practice.feature.leaderboard.data.LeaderboardPlayerData;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class KitLeaderboardHologram {

    private final Hologram hologram;
    private final Kit kit;
    private final String customType;
    private final LeaderboardType type;
    private final LeaderboardService leaderboardService;

    public KitLeaderboardHologram(Location location, Kit kit, LeaderboardType type) {
        this.hologram = new Hologram(location);
        this.kit = kit;
        this.customType = null;
        this.type = type;
        this.leaderboardService = AlleyPractice.getInstance().getService(LeaderboardService.class);
    }

    public KitLeaderboardHologram(Location location, String customType, LeaderboardType type) {
        this.hologram = new Hologram(location);
        this.kit = null;
        this.customType = customType;
        this.type = type;
        this.leaderboardService = AlleyPractice.getInstance().getService(LeaderboardService.class);
    }

    public void update() {
        List<String> lines = new ArrayList<>();

        if (customType != null && customType.equalsIgnoreCase("global")) {
            lines.add("&b&lTOP 10 GLOBAL " + formatType(type).toUpperCase());
            lines.add("&7Server Leaderboard");
            lines.add("");

            List<LeaderboardPlayerData> entries = leaderboardService.getLeaderboardEntries(null, type);
            if (entries == null || entries.isEmpty()) {
                lines.add("&cNo global records found.");
            } else {
                int limit = Math.min(10, entries.size());
                for (int i = 0; i < limit; i++) {
                    LeaderboardPlayerData data = entries.get(i);
                    if (data != null && data.getName() != null) {
                        lines.add(getRankPrefix(i + 1) + " &f" + data.getName() + " &7- &b" + data.getValue());
                    }
                }
            }
        } else if (kit != null) {
            List<LeaderboardPlayerData> entries = leaderboardService.getLeaderboardEntries(kit, type);
            lines.add("&b&l" + kit.getDisplayName() + " " + formatType(type));
            lines.add("&7Global Top 10");
            lines.add("");

            if (entries == null || entries.isEmpty()) {
                lines.add("&cNo records found.");
            } else {
                int limit = Math.min(10, entries.size());
                for (int i = 0; i < limit; i++) {
                    LeaderboardPlayerData data = entries.get(i);
                    if (data != null && data.getName() != null) {
                        lines.add(getRankPrefix(i + 1) + " &f" + data.getName() + " &7- &b" + data.getValue());
                    }
                }
            }
        }

        lines.add("");
        lines.add("&bClick to update");

        hologram.update(lines);
    }

    public void destroy() {
        hologram.destroy();
    }

    public Kit getKit() {
        return kit;
    }

    public String getCustomType() {
        return customType;
    }

    public LeaderboardType getType() {
        return type;
    }

    public Location getLocation() {
        return hologram.getLocation();
    }

    private String getRankPrefix(int rank) {
        switch (rank) {
            case 1: return "&b&l✫1";
            case 2: return "&b&l✫2";
            case 3: return "&d&l✫3";
            default: return "&e" + rank + ".";
        }
    }

    private String formatType(LeaderboardType type) {
        switch (type) {
            case RANKED: return "Elo";
            case UNRANKED: return "Wins";
            case WIN_STREAK: return "Streak";
            case FFA: return "Kills";
            default: return type.name();
        }
    }
}