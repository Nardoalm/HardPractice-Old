package live.pvphard.practice;

import live.pvphard.practice.api.HardPracticeAPI;
import live.pvphard.practice.api.internal.HardPracticeAPIImpl;
import live.pvphard.practice.bootstrap.HardContext;
import live.pvphard.practice.bootstrap.lifecycle.Service;
import live.pvphard.practice.common.logger.Logger;
import live.pvphard.practice.common.logger.PluginLogger;
import live.pvphard.practice.core.database.task.RepositoryCleanupTask;
import live.pvphard.practice.feature.cosmetic.CosmeticListener;
import live.pvphard.practice.feature.cosmetic.task.CosmeticTask;
import live.pvphard.practice.feature.hologram.manager.HologramManager;
import live.pvphard.practice.feature.match.task.other.ArrowRemovalTask;
import live.pvphard.practice.feature.match.task.other.MatchPearlCooldownTask;
import live.pvphard.practice.feature.leaderboard.internal.LeaderboardUpdateTask;
import lombok.Getter;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import live.pvphard.practice.adapter.placeholder.internal.KaosPlaceholderExpansion;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Hard – A modern, modular Practice PvP core built from the ground up for Minecraft 1.8.
 * <p>
 * Developed by Revere Inc., Hard focuses on clean, professional, and readable code,
 * making it easy for developers to jump into practice PvP development with minimal friction.
 * </p>
 *
 *
 * @author ysubz
 * @version 2.0 — Complete recode (entirely rewritten from scratch)
 * @see <a href="https://revere.dev">revere.dev</a>
 * @see <a href="https://discord.gg/revere">Discord Support</a>
 * @since 19/04/2024
 */
@Getter
public class HardPractice extends JavaPlugin {
    @Getter
    private static HardPractice instance;

    private final Hard api;
    private HardPracticeAPI practiceAPI;
    private HardContext context;

    public HologramManager getHologramManager() {
        return this.getService(HologramManager.class);
    }

    public static HardPractice getInstance() {
        return instance;
    }

    public HardPractice() {
        this.api = new Hard();
    }

    @Override
    public void onEnable() {
        final long startTime = System.nanoTime();
        instance = this;

        this.validatePluginMetadata();

        try {
            this.context = new HardContext(this);
            this.context.initialize();
            this.registerPublicApi();

            if (this.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                new KaosPlaceholderExpansion(this).register();
                Logger.info("PlaceholderAPI detectado: Placeholders do Hard registradas!");
            }

        } catch (Exception exception) {
            Logger.logException("Ocorreu um erro fatal durante a inicialização dos serviços. O Hard será desativado.", exception);
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.scheduleTasks();

        final long durationMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
        PluginLogger.onEnable(durationMillis);

        this.api.runOnEnableCallbacks();
    }

    @Override
    public void onDisable() {
        this.unregisterPublicApi();

        CosmeticListener.purgeAllBlocks();

        if (this.context != null) {
            this.context.shutdown();
        }

        PluginLogger.onDisable();

        this.api.runOnDisableCallbacks();
    }

    /**
     * Provides global, type-safe access to any managed service via its interface.
     *
     * @param serviceInterface The class of the service interface you want (e.g., ProfileService.class).
     * @return The service instance.
     * @throws IllegalStateException if the service is not found.
     */
    public <T extends Service> T getService(Class<T> serviceInterface) {
        Objects.requireNonNull(serviceInterface, "A interface do serviço não pode ser nula");
        if (this.context == null) {
            throw new IllegalStateException("HardContext não está disponível. O bootstrap pode estar desativando ou falhou ao carregar.");
        }
        return this.context.getService(serviceInterface)
                .orElseThrow(() -> new IllegalStateException("Não foi possível encontrar um serviço registrado para: " + serviceInterface.getSimpleName()));
    }

    public HardPracticeAPI getAPI() {
        return this.practiceAPI;
    }

    private void validatePluginMetadata() {
        List<String> authors = this.getDescription().getAuthors();
        if (authors == null || authors.isEmpty()) {
            Logger.error("plugin.yml sem autores definidos. Verifique os metadados.");
        }
    }

    private void scheduleTasks() {
        final Map<String, Runnable> tasks = new LinkedHashMap<>();

        tasks.put(RepositoryCleanupTask.class.getSimpleName(), () -> new RepositoryCleanupTask(this).runTaskTimer(this, 0L, 40L));
        tasks.put(MatchPearlCooldownTask.class.getSimpleName(), () -> new MatchPearlCooldownTask().runTaskTimer(this, 2L, 2L));
        tasks.put(ArrowRemovalTask.class.getSimpleName(), () -> new ArrowRemovalTask().runTaskTimer(this, 20L, 20L));
        tasks.put(CosmeticTask.class.getSimpleName(), () -> new CosmeticTask(this).runTaskTimerAsynchronously(this, 0L, 4L));
        tasks.put(LeaderboardUpdateTask.class.getSimpleName(), () -> new LeaderboardUpdateTask().runTaskTimer(this, 1200L, 1200L));

        tasks.forEach(Logger::logTimeTask);
    }

    private void registerPublicApi() {
        this.practiceAPI = new HardPracticeAPIImpl(this);
        this.getServer().getServicesManager().register(HardPracticeAPI.class, this.practiceAPI, this, ServicePriority.Normal);
    }

    private void unregisterPublicApi() {
        if (this.practiceAPI == null) {
            return;
        }

        this.getServer().getServicesManager().unregister(HardPracticeAPI.class, this.practiceAPI);
        this.practiceAPI = null;
    }
}
