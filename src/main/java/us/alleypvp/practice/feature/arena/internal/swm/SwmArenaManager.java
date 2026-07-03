package us.alleypvp.practice.feature.arena.internal.swm;

import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import us.alleypvp.practice.AlleyPractice;
import us.alleypvp.practice.common.FileUtil;
import us.alleypvp.practice.core.config.ConfigService;
import us.alleypvp.practice.feature.arena.internal.types.StandAloneArena;
import us.alleypvp.practice.feature.arena.schematic.ArenaSchematicService;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

import java.io.File;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SwmArenaManager implements ArenaCopyManager {
    private final AlleyPractice plugin;
    private final ConfigService configService;
    private final ArenaSchematicService arenaSchematicService;
    private final Map<String, SlimeWorld> templateWorldCache = new ConcurrentHashMap<>();
    private boolean enabled;
    private SlimePlugin slimePlugin;
    private SlimeLoader slimeLoader;

    public SwmArenaManager(AlleyPractice plugin, ConfigService configService, ArenaSchematicService arenaSchematicService) {
        this.plugin = plugin;
        this.configService = configService;
        this.arenaSchematicService = arenaSchematicService;
    }

    @Override
    public void initialize() {
        try {
            boolean swmEnabled = this.configService.getSettingsConfig().getBoolean("arena-management.swm.enabled", false);
            if (!swmEnabled || !(Bukkit.getPluginManager().getPlugin("SlimeWorldManager") instanceof SlimePlugin)) {
                this.enabled = false;
                return;
            }
            this.slimePlugin = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
            this.slimeLoader = this.slimePlugin.getLoader(this.configService.getSettingsConfig().getString("arena-management.swm.loader", "file"));
            this.enabled = this.slimeLoader != null;
        } catch (Throwable t) { this.enabled = false; }
    }

    @Override
    public boolean isEnabled() { return enabled; }

    @Override
    public StandAloneArena createTemporaryArenaCopy(StandAloneArena originalArena, int copyId) {
        if (!enabled) return null;
        String templateName = "kaos_template_" + sanitizeName(originalArena.getName());
        String copyName = "kaos_copy_" + sanitizeName(originalArena.getName()) + "_" + copyId;

        try {
            SlimeWorld template = getOrCreateTemplateWorld(originalArena, templateName);
            if (template == null) return null;

            SlimeWorld copy = template.clone(copyName, this.slimeLoader);
            this.slimePlugin.generateWorld(copy);

            World bukkitWorld = Bukkit.getWorld(copyName);
            if (bukkitWorld == null) return null;

            Location origin = new Location(bukkitWorld, 0, 100, 0);
            StandAloneArena arena = originalArena.createCopy(bukkitWorld, origin, copyId);
            if (arena != null) {
                forceLoadChunks(arena);
                arena.setManagedBySlimeWorldManager(true);
            }
            return arena;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void forceLoadChunks(StandAloneArena arena) {
        Location min = arena.getMinimum();
        Location max = arena.getMaximum();
        if (min == null || max == null || min.getWorld() == null) return;
        World w = min.getWorld();

        // MARGEM DE SEGURANÇA: +2 Chunks para cada lado para as pérolas não explodirem o Netty (Disconnect Fix)
        int minX = (Math.min(min.getBlockX(), max.getBlockX()) >> 4) - 2;
        int maxX = (Math.max(min.getBlockX(), max.getBlockX()) >> 4) + 2;
        int minZ = (Math.min(min.getBlockZ(), max.getBlockZ()) >> 4) - 2;
        int maxZ = (Math.max(min.getBlockZ(), max.getBlockZ()) >> 4) + 2;

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                if (!w.isChunkLoaded(x, z)) w.loadChunk(x, z, true);
            }
        }
    }

    @Override
    public boolean deleteTemporaryArena(StandAloneArena arena) {
        if (!enabled || !arena.isManagedBySlimeWorldManager() || arena.getMinimum() == null) return false;
        World w = arena.getMinimum().getWorld();
        if (w == null) return true;

        String name = w.getName();
        w.getPlayers().forEach(p -> p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation()));
        Bukkit.unloadWorld(w, false);

        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try {
                if (this.slimeLoader.worldExists(name)) this.slimeLoader.deleteWorld(name);
            } catch (Exception ignored) {}
        });

        File folder = new File(Bukkit.getWorldContainer(), name);
        if (folder.exists()) FileUtil.deleteWorldFolder(folder);
        return true;
    }

    @Override
    public void shutdown() { this.templateWorldCache.clear(); }

    private SlimeWorld getOrCreateTemplateWorld(StandAloneArena arena, String name) throws Exception {
        if (templateWorldCache.containsKey(name)) return templateWorldCache.get(name);
        try {
            SlimeWorld sw = this.slimePlugin.loadWorld(this.slimeLoader, name, true, createTemplatePropertyMap());
            templateWorldCache.put(name, sw);
            return sw;
        } catch (Exception e) {
            importTemplateWorld(arena, name);
            // Agora sim tenta ler, sabendo que a geração terminou.
            return this.slimePlugin.loadWorld(this.slimeLoader, name, true, createTemplatePropertyMap());
        }
    }

    private void importTemplateWorld(StandAloneArena arena, String name) {
        File schem = this.arenaSchematicService.getSchematicFile(arena);
        if (!schem.exists()) return;
        String importName = "swm_import_" + sanitizeName(arena.getName());

        WorldCreator wc = new WorldCreator(importName);
        wc.environment(World.Environment.NORMAL);
        wc.generateStructures(false);
        wc.type(WorldType.FLAT);
        wc.generatorSettings("2;0;1");

        World w = wc.createWorld();
        this.arenaSchematicService.paste(new Location(w, 0, 100, 0), schem);

        w.save(); // Salva a schematic injetada NO DISCO com segurança
        Bukkit.unloadWorld(w, true);

        try {
            this.slimePlugin.importWorld(new File(Bukkit.getWorldContainer(), importName), name, this.slimeLoader);
            // Coloca no cache imediato para evitar refazer a leitura atoa
            templateWorldCache.put(name, this.slimePlugin.loadWorld(this.slimeLoader, name, true, createTemplatePropertyMap()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        FileUtil.deleteWorldFolder(new File(Bukkit.getWorldContainer(), importName));
    }

    private SlimePropertyMap createTemplatePropertyMap() {
        SlimePropertyMap m = new SlimePropertyMap();
        m.setInt(SlimeProperties.SPAWN_X, 0);
        m.setInt(SlimeProperties.SPAWN_Y, 100);
        m.setInt(SlimeProperties.SPAWN_Z, 0);
        return m;
    }

    private String sanitizeName(String i) { return i.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_]", "_"); }
}