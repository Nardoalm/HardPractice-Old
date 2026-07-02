package us.alleypvp.practice.feature.arena.internal;

import us.alleypvp.practice.AlleyPractice;
import us.alleypvp.practice.bootstrap.KaosContext;
import us.alleypvp.practice.bootstrap.annotation.Service;
import us.alleypvp.practice.common.FileUtil;
import us.alleypvp.practice.common.VoidChunkGenerator;
import us.alleypvp.practice.common.logger.Logger;
import us.alleypvp.practice.common.serializer.Serializer;
import us.alleypvp.practice.core.config.ConfigService;
import us.alleypvp.practice.feature.arena.Arena;
import us.alleypvp.practice.feature.arena.ArenaService;
import us.alleypvp.practice.feature.arena.ArenaType;
import us.alleypvp.practice.feature.arena.ArenaValidator;
import us.alleypvp.practice.feature.arena.internal.swm.ArenaCopyManager;
import us.alleypvp.practice.feature.arena.internal.swm.NoOpArenaCopyManager;
import us.alleypvp.practice.feature.arena.internal.types.EventArena;
import us.alleypvp.practice.feature.arena.internal.types.FreeForAllArena;
import us.alleypvp.practice.feature.arena.internal.types.SharedArena;
import us.alleypvp.practice.feature.arena.internal.types.StandAloneArena;
import us.alleypvp.practice.feature.arena.schematic.ArenaSchematicService;
import us.alleypvp.practice.feature.kit.Kit;
import us.alleypvp.practice.feature.kit.KitService;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Getter
@Service(provides = ArenaService.class, priority = 110)
public final class ArenaServiceImpl implements ArenaService {
    private final AlleyPractice plugin;
    private final ConfigService configService;
    private final KitService kitService;
    private final ArenaSchematicService arenaSchematicService;
    private ArenaCopyManager arenaCopyManager;
    private final ExecutorService executorService;

    private final List<Arena> arenas = new ArrayList<>();
    private final List<StandAloneArena> temporaryArenas = new ArrayList<>();
    private final AtomicInteger copyIdCounter = new AtomicInteger(0);

    private final Map<String, List<Arena>> arenasByKit = new ConcurrentHashMap<>();
    private final Map<String, Arena> arenasByName = new ConcurrentHashMap<>();

    private final ThreadLocalRandom random = ThreadLocalRandom.current();

    private World temporaryWorld;
    private Location nextCopyLocation;
    private final int arenaSpacing = 1500;

    private final ArenaValidator arenaValidator = new ArenaValidator();

    public ArenaServiceImpl(AlleyPractice plugin, ConfigService configService, KitService kitService, ArenaSchematicService arenaSchematicService) {
        this.plugin = plugin;
        this.configService = configService;
        this.kitService = kitService;
        this.arenaSchematicService = arenaSchematicService;
        this.arenaCopyManager = new NoOpArenaCopyManager();
        this.executorService = Executors.newFixedThreadPool(4);
    }

    @Override
    public void initialize(KaosContext context) {
        this.loadArenas();
        this.arenaSchematicService.generateMissingSchematics(this.arenas);
        this.arenaCopyManager = this.createArenaCopyManager();
        this.arenaCopyManager.initialize();

        if (!this.arenaCopyManager.isEnabled()) {
            this.initializeTemporaryWorld();
        }

        buildCaches();
    }

    @Override
    public void shutdown(KaosContext context) {
        cleanupTemporaryArenas();
        this.arenaCopyManager.shutdown();

        if (temporaryWorld != null) {
            String worldName = temporaryWorld.getName();
            temporaryWorld.getPlayers().forEach(player -> player.teleport(Bukkit.getServer().getWorlds().get(0).getSpawnLocation()));

            if (Bukkit.unloadWorld(temporaryWorld, false)) {
                File worldFolder = new File(Bukkit.getWorldContainer(), worldName);
                if (worldFolder.exists()) FileUtil.deleteWorldFolder(worldFolder);
            }
            temporaryWorld = null;
        }

        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    private void buildCaches() {
        arenasByKit.clear();
        arenasByName.clear();

        for (Kit kit : kitService.getKits()) {
            List<Arena> kitArenas = arenas.stream()
                    .filter(arena -> arena.getType() != ArenaType.EVENT)
                    .filter(arena -> arena.getKits().contains(kit.getName()))
                    .filter(Arena::isEnabled)
                    .collect(Collectors.toList());
            arenasByKit.put(kit.getName(), kitArenas);
        }

        for (Arena arena : arenas) {
            arenasByName.put(arena.getName().toLowerCase(), arena);
        }
    }

    private void initializeTemporaryWorld() {
        String worldName = "temporary_arena_world";
        cleanupExistingWorld(worldName);

        WorldCreator creator = new WorldCreator(worldName);
        creator.environment(World.Environment.NORMAL);
        creator.generateStructures(false);
        creator.type(WorldType.FLAT);
        creator.generatorSettings("2;0;1");

        this.temporaryWorld = creator.createWorld();
        this.temporaryWorld.setGameRuleValue("doMobSpawning", "false");
        this.temporaryWorld.setGameRuleValue("doDaylightCycle", "false");
        this.temporaryWorld.setGameRuleValue("doTileDrops", "false");
        this.temporaryWorld.setGameRuleValue("doFireTick", "false");

        this.nextCopyLocation = new Location(temporaryWorld, 0, 100, 0);
    }

    public void loadArenas() {
        FileConfiguration config = configService.getArenasConfig();
        ConfigurationSection arenasConfig = config.getConfigurationSection("arenas");

        if (arenasConfig == null) return;

        for (String arenaName : arenasConfig.getKeys(false)) {
            Arena arena = loadSingleArena(config, arenaName);
            if (arena != null) this.arenas.add(arena);
        }
    }

    private Arena loadSingleArena(FileConfiguration config, String arenaName) {
        try {
            String name = "arenas." + arenaName;
            ArenaType arenaType = ArenaType.valueOf(config.getString(name + ".type"));
            Location minimum = Serializer.deserializeLocation(config.getString(name + ".minimum"));
            Location maximum = Serializer.deserializeLocation(config.getString(name + ".maximum"));

            Arena arena = createArenaByType(arenaType, arenaName, minimum, maximum, config, name);
            configureArena(arena, config, name);
            return arena;
        } catch (Exception e) {
            return null;
        }
    }

    private Arena createArenaByType(ArenaType arenaType, String arenaName, Location minimum, Location maximum, FileConfiguration config, String name) {
        switch (arenaType) {
            case SHARED: return new SharedArena(arenaName, minimum, maximum);
            case STANDALONE:
                return new StandAloneArena(arenaName, minimum, maximum,
                        Serializer.deserializeLocation(config.getString(name + ".team-one-portal")),
                        Serializer.deserializeLocation(config.getString(name + ".team-two-portal")),
                        config.getInt(name + ".height-limit", 7),
                        config.getInt(name + ".void-level", 0));
            case FFA:
                return new FreeForAllArena(arenaName,
                        Serializer.deserializeLocation(config.getString(name + ".safe-zone.pos1")),
                        Serializer.deserializeLocation(config.getString(name + ".safe-zone.pos2")));
            case EVENT: return new EventArena(arenaName, minimum, maximum);
            default: throw new IllegalStateException();
        }
    }

    private void configureArena(Arena arena, FileConfiguration config, String name) {
        if (config.contains(name + ".kits")) {
            arena.getKits().addAll(config.getStringList(name + ".kits").stream().filter(k -> kitService.getKit(k) != null).collect(Collectors.toSet()));
        }
        if (config.contains(name + ".events")) {
            arena.getEvents().addAll(config.getStringList(name + ".events").stream().filter(Objects::nonNull).map(String::toLowerCase).collect(Collectors.toSet()));
        }
        if (config.contains(name + ".pos1")) arena.setPos1(Serializer.deserializeLocation(config.getString(name + ".pos1")));
        if (config.contains(name + ".pos2")) arena.setPos2(Serializer.deserializeLocation(config.getString(name + ".pos2")));
        if (config.contains(name + ".center")) arena.setCenter(Serializer.deserializeLocation(config.getString(name + ".center")));
        if (config.contains(name + ".display-name")) arena.setDisplayName(config.getString(name + ".display-name"));
        if (config.contains(name + ".enabled")) arena.setEnabled(config.getBoolean(name + ".enabled"));
    }

    @Override
    public StandAloneArena createTemporaryArenaCopy(StandAloneArena originalArena) {
        if (originalArena.isTemporaryCopy()) throw new IllegalArgumentException();

        if (this.arenaCopyManager.isEnabled()) {
            StandAloneArena copied = this.arenaCopyManager.createTemporaryArenaCopy(originalArena, copyIdCounter.incrementAndGet());
            if (copied != null) {
                this.temporaryArenas.add(copied);
                return copied;
            }
        }

        if (originalArena.getMinimum() == null || originalArena.getMaximum() == null) return null;

        Location copyLocation = getNextCopyLocationForArena(originalArena);
        if (originalArena.getPos1() != null) {
            copyLocation.setY(100 - (originalArena.getPos1().getBlockY() - Math.min(originalArena.getMinimum().getBlockY(), originalArena.getMaximum().getBlockY())));
        }

        StandAloneArena copiedArena = originalArena.createCopy(temporaryWorld, copyLocation, copyIdCounter.incrementAndGet());
        if (copiedArena == null) return null;

        if (copiedArena.getPos1() != null) copiedArena.setHeightLimit(copiedArena.getPos1().getBlockY() + copiedArena.getHeightLimit());

        this.forceLoadChunks(copiedArena);
        this.arenaSchematicService.paste(copyLocation, this.arenaSchematicService.getSchematicFile(originalArena.getName()));

        this.temporaryArenas.add(copiedArena);
        return copiedArena;
    }

    private void forceLoadChunks(StandAloneArena arena) {
        Location min = arena.getMinimum();
        Location max = arena.getMaximum();
        if (min == null || max == null || min.getWorld() == null) return;
        World world = min.getWorld();
        for (int x = (Math.min(min.getBlockX(), max.getBlockX()) >> 4); x <= (Math.max(min.getBlockX(), max.getBlockX()) >> 4); x++) {
            for (int z = (Math.min(min.getBlockZ(), max.getBlockZ()) >> 4); z <= (Math.max(min.getBlockZ(), max.getBlockZ()) >> 4); z++) {
                if (!world.isChunkLoaded(x, z)) world.loadChunk(x, z, true);
            }
        }
    }

    public Location getNextCopyLocationForArena(StandAloneArena originalArena) {
        Location loc = this.nextCopyLocation.clone();
        if (originalArena.getPos1() != null && originalArena.getMinimum() != null) {
            loc.setY(100 - (originalArena.getPos1().getBlockY() - originalArena.getMinimum().getBlockY()));
        }
        nextCopyLocation.add(arenaSpacing, 0, 0);
        if (nextCopyLocation.getX() > arenaSpacing * 10) {
            nextCopyLocation.setX(0);
            nextCopyLocation.add(0, 0, arenaSpacing);
        }
        return loc;
    }

    public void cleanupTemporaryArenas() {
        for (StandAloneArena arena : new ArrayList<>(temporaryArenas)) {
            if (!this.arenaCopyManager.deleteTemporaryArena(arena)) arena.deleteCopiedArena();
        }
        this.temporaryArenas.clear();
    }

    private void cleanupExistingWorld(String worldName) {
        World world = this.plugin.getServer().getWorld(worldName);
        if (world != null) {
            world.getPlayers().forEach(p -> p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation()));
            this.plugin.getServer().unloadWorld(world, false);
        }
        File folder = new File(Bukkit.getWorldContainer(), worldName);
        if (folder.exists()) FileUtil.deleteWorldFolder(folder);
    }

    @Override public List<Arena> getArenas() { return Collections.unmodifiableList(arenas); }
    @Override public List<StandAloneArena> getTemporaryArenas() { return Collections.unmodifiableList(temporaryArenas); }
    @Override public void saveArena(Arena arena) { if (arena != null) { arena.saveArena(); buildCaches(); } }
    @Override public void deleteArena(Arena arena) { if (arena != null) { arena.deleteArena(); arenas.remove(arena); buildCaches(); } }
    @Override public void deleteTemporaryArena(StandAloneArena arena) { if (arena != null && temporaryArenas.contains(arena)) { if (!this.arenaCopyManager.deleteTemporaryArena(arena)) arena.deleteCopiedArena(); temporaryArenas.remove(arena); } }
    @Override public Arena getRandomArena(Kit kit) { List<Arena> a = arenasByKit.get(kit.getName()); return (a == null || a.isEmpty()) ? null : selectArenaWithPotentialTemporaryCopy(a.get(random.nextInt(a.size()))); }
    @Override public Arena getArenaByName(String name) { return arenasByName.get(name.toLowerCase()); }
    @Override public Arena selectArenaWithPotentialTemporaryCopy(Arena arena) { return (arena instanceof StandAloneArena) ? createTemporaryArenaCopy((StandAloneArena) arena) : arena; }
    @Override public void registerNewArena(Arena arena) { if (arena != null && !arenasByName.containsKey(arena.getName().toLowerCase())) { this.arenas.add(arena); buildCaches(); } }

    private ArenaCopyManager createArenaCopyManager() {
        FileConfiguration settings = this.configService.getSettingsConfig();
        if (settings == null || !settings.getBoolean("arena-management.swm.enabled", false)) return new NoOpArenaCopyManager();
        if (Bukkit.getPluginManager().getPlugin("SlimeWorldManager") == null) return new NoOpArenaCopyManager();
        try {
            return (ArenaCopyManager) Class.forName("us.alleypvp.practice.feature.arena.internal.swm.SwmArenaManager").getConstructor(AlleyPractice.class, ConfigService.class, ArenaSchematicService.class).newInstance(this.plugin, this.configService, this.arenaSchematicService);
        } catch (Throwable t) { return new NoOpArenaCopyManager(); }
    }
}