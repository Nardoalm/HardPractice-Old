package live.pvphard.practice.feature.hologram.manager.impl;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.bootstrap.HardContext;
import live.pvphard.practice.bootstrap.annotation.Service;
import live.pvphard.practice.feature.hologram.manager.HologramManager;
import live.pvphard.practice.feature.hologram.leaderboard.KitLeaderboardHologram;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.feature.kit.KitService;
import live.pvphard.practice.feature.leaderboard.LeaderboardType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service(provides = HologramManager.class, priority = 290)
public final class HologramManagerImpl implements HologramManager, Listener {

    private final HardPractice plugin;
    private File configFile;
    private FileConfiguration config;
    private final List<KitLeaderboardHologram> activeHolograms;

    public HologramManagerImpl() {
        this.plugin = HardPractice.getInstance();
        this.activeHolograms = new ArrayList<>();
    }

    @Override
    public void initialize(HardContext context) {
        File storageFolder = new File(plugin.getDataFolder(), "storage");
        if (!storageFolder.exists()) {
            storageFolder.mkdirs();
        }

        this.configFile = new File(storageFolder, "holograms.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.config = YamlConfiguration.loadConfiguration(configFile);
        Bukkit.getPluginManager().registerEvents(this, plugin);

        Bukkit.getScheduler().runTaskLater(plugin, this::loadHolograms, 20L);
    }

    @Override
    public void shutdown(HardContext context) {
        activeHolograms.forEach(KitLeaderboardHologram::destroy);
        activeHolograms.clear();
    }

    @Override
    public void loadHolograms() {
        activeHolograms.forEach(KitLeaderboardHologram::destroy);
        activeHolograms.clear();

        ConfigurationSection section = config.getConfigurationSection("holograms");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            String path = "holograms." + key;

            String worldName = config.getString(path + ".world");
            World world = Bukkit.getWorld(worldName);
            if (world == null) continue;

            double x = config.getDouble(path + ".x");
            double y = config.getDouble(path + ".y");
            double z = config.getDouble(path + ".z");
            Location location = new Location(world, x, y, z);

            String target = config.getString(path + ".target");
            LeaderboardType type = LeaderboardType.valueOf(config.getString(path + ".type"));

            KitLeaderboardHologram hologram;
            if (target.equalsIgnoreCase("global")) {
                hologram = new KitLeaderboardHologram(location, "global", type);
            } else {
                Kit kit = plugin.getService(KitService.class).getKit(target);
                if (kit == null) continue;
                hologram = new KitLeaderboardHologram(location, kit, type);
            }

            hologram.update();
            activeHolograms.add(hologram);
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        if (activeHolograms.isEmpty()) return;
        for (KitLeaderboardHologram holo : activeHolograms) {
            if (holo.getLocation() != null && holo.getLocation().getWorld().equals(event.getWorld())) {
                int cx = holo.getLocation().getBlockX() >> 4;
                int cz = holo.getLocation().getBlockZ() >> 4;
                if (event.getChunk().getX() == cx && event.getChunk().getZ() == cz) {
                    holo.update();
                }
            }
        }
    }

    @Override
    public void saveHologram(Location loc, String target, LeaderboardType type) {
        String id = target.toLowerCase() + "_" + type.name().toLowerCase() + "_" + System.currentTimeMillis();
        String path = "holograms." + id;

        config.set(path + ".world", loc.getWorld().getName());
        config.set(path + ".x", loc.getX());
        config.set(path + ".y", loc.getY());
        config.set(path + ".z", loc.getZ());
        config.set(path + ".target", target);
        config.set(path + ".type", type.name());

        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        KitLeaderboardHologram hologram;
        if (target.equalsIgnoreCase("global")) {
            hologram = new KitLeaderboardHologram(loc, "global", type);
        } else {
            Kit kit = plugin.getService(KitService.class).getKit(target);
            hologram = new KitLeaderboardHologram(loc, kit, type);
        }

        hologram.update();
        activeHolograms.add(hologram);
    }

    @Override
    public boolean removeHologram(String target, LeaderboardType type) {
        ConfigurationSection section = config.getConfigurationSection("holograms");
        if (section == null) return false;

        String foundKey = null;
        for (String key : section.getKeys(false)) {
            String t = config.getString("holograms." + key + ".target");
            String typeStr = config.getString("holograms." + key + ".type");

            if (t.equalsIgnoreCase(target) && typeStr.equalsIgnoreCase(type.name())) {
                foundKey = key;
                break;
            }
        }

        if (foundKey != null) {
            config.set("holograms." + foundKey, null);
            try {
                config.save(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            activeHolograms.removeIf(holo -> {
                boolean match = false;
                if (target.equalsIgnoreCase("global") && holo.getCustomType() != null && holo.getCustomType().equalsIgnoreCase("global") && holo.getType() == type) {
                    match = true;
                } else if (holo.getKit() != null && holo.getKit().getName().equalsIgnoreCase(target) && holo.getType() == type) {
                    match = true;
                }
                if (match) {
                    holo.destroy();
                }
                return match;
            });
            return true;
        }
        return false;
    }

    @Override
    public List<KitLeaderboardHologram> getActiveHolograms() {
        return activeHolograms;
    }
}