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
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.Player;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.ChatColor;

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

        Bukkit.getScheduler().runTaskLater(plugin, this::loadHolograms, 100L);
    }

    @Override
    public void shutdown(HardContext context) {
        activeHolograms.forEach(KitLeaderboardHologram::destroy);
        activeHolograms.clear();
    }

    @Override
    public void loadHolograms() {
        try {
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

                // Check if already active at this exact block location
                boolean alreadyActive = false;
                for (KitLeaderboardHologram holo : activeHolograms) {
                    Location l = holo.getLocation();
                    if (l != null && l.getWorld() != null && l.getWorld().getName().equalsIgnoreCase(worldName)) {
                        if (l.getBlockX() == location.getBlockX() && l.getBlockY() == location.getBlockY() && l.getBlockZ() == location.getBlockZ()) {
                            alreadyActive = true;
                            // Ensure it has valid entities, if not update/respawn them
                            boolean needsRespawn = holo.getHologram().getLines().isEmpty();
                            for (ArmorStand as : holo.getHologram().getLines()) {
                                if (as == null || !as.isValid()) {
                                    needsRespawn = true;
                                    break;
                                }
                            }
                            if (needsRespawn) {
                                holo.update();
                            }
                            break;
                        }
                    }
                }

                if (alreadyActive) {
                    continue;
                }

                KitLeaderboardHologram hologram;
                if (target.equalsIgnoreCase("global")) {
                    hologram = new KitLeaderboardHologram(location, "global", type);
                } else {
                    KitService kitService = null;
                    try {
                        kitService = plugin.getService(KitService.class);
                    } catch (Exception ignored) {}

                    if (kitService == null) continue; // Safe skip, will load on next check/trigger

                    Kit kit = kitService.getKit(target);
                    if (kit == null) continue;
                    hologram = new KitLeaderboardHologram(location, kit, type);
                }

                hologram.update();
                activeHolograms.add(hologram);
            }

            cleanupAllLoadedChunks();
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load holograms: " + e.getMessage());
            e.printStackTrace();
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

    @Override
    public void cleanupAllLoadedChunks() {
        List<ArmorStand> trackedStands = new ArrayList<>();
        for (KitLeaderboardHologram holo : activeHolograms) {
            trackedStands.addAll(holo.getHologram().getLines());
        }

        for (World world : Bukkit.getWorlds()) {
            for (org.bukkit.Chunk chunk : world.getLoadedChunks()) {
                for (Entity entity : chunk.getEntities()) {
                    if (entity instanceof ArmorStand) {
                        ArmorStand as = (ArmorStand) entity;
                        if (!as.isVisible() && !as.hasGravity() && !trackedStands.contains(as)) {
                            as.remove();
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        org.bukkit.World world = event.getWorld();
        int chunkX = event.getChunk().getX();
        int chunkZ = event.getChunk().getZ();

        Bukkit.getScheduler().runTask(plugin, () -> {
            if (!world.isChunkLoaded(chunkX, chunkZ)) return;

            org.bukkit.Chunk chunk = world.getChunkAt(chunkX, chunkZ);

            List<ArmorStand> trackedStands = new ArrayList<>();
            for (KitLeaderboardHologram holo : activeHolograms) {
                trackedStands.addAll(holo.getHologram().getLines());
            }

            for (Entity entity : chunk.getEntities()) {
                if (entity instanceof ArmorStand) {
                    ArmorStand as = (ArmorStand) entity;
                    if (!as.isVisible() && !as.hasGravity() && !trackedStands.contains(as)) {
                        as.remove();
                    }
                }
            }

            for (KitLeaderboardHologram holo : activeHolograms) {
                Location loc = holo.getLocation();
                if (loc != null && loc.getWorld() != null && loc.getWorld().equals(world)) {
                    if (loc.getBlockX() >> 4 == chunkX && loc.getBlockZ() >> 4 == chunkZ) {
                        boolean needsSpawn = holo.getHologram().getLines().isEmpty();
                        for (ArmorStand as : holo.getHologram().getLines()) {
                            if (as == null || !as.isValid()) {
                                needsSpawn = true;
                                break;
                            }
                        }
                        if (needsSpawn) {
                            holo.getHologram().respawn();
                        }
                    }
                }
            }
        });
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        for (KitLeaderboardHologram holo : activeHolograms) {
            Location loc = holo.getLocation();
            if (loc != null && loc.getWorld() != null && loc.getWorld().equals(event.getWorld())) {
                if (loc.getBlockX() >> 4 == event.getChunk().getX() && loc.getBlockZ() >> 4 == event.getChunk().getZ()) {
                    holo.getHologram().despawn();
                }
            }
        }
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        this.loadHolograms();
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof ArmorStand)) return;
        ArmorStand clicked = (ArmorStand) event.getRightClicked();
        
        for (KitLeaderboardHologram holo : activeHolograms) {
            if (holo.getHologram().getLines().contains(clicked)) {
                holo.update();
                event.getPlayer().sendMessage(ChatColor.GREEN + "Leaderboard updated!");
                event.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof ArmorStand)) return;
        if (!(event.getDamager() instanceof Player)) return;
        
        ArmorStand clicked = (ArmorStand) event.getEntity();
        Player player = (Player) event.getDamager();
        
        for (KitLeaderboardHologram holo : activeHolograms) {
            if (holo.getHologram().getLines().contains(clicked)) {
                holo.update();
                player.sendMessage(ChatColor.GREEN + "Leaderboard updated!");
                event.setCancelled(true);
                break;
            }
        }
    }
}