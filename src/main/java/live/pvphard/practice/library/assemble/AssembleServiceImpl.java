package live.pvphard.practice.library.assemble;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.library.assemble.enums.AssembleStyle;
import live.pvphard.practice.library.assemble.events.AssembleBoardCreateEvent;
import live.pvphard.practice.library.assemble.events.AssembleBoardDestroyEvent;
import live.pvphard.practice.library.assemble.listener.AssembleListener;
import live.pvphard.practice.core.config.ConfigService;
import live.pvphard.practice.bootstrap.HardContext;
import live.pvphard.practice.bootstrap.annotation.Service;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.common.animation.AnimationService;
import live.pvphard.practice.common.logger.Logger;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@Service(provides = AssembleService.class, priority = 320)
public class AssembleServiceImpl implements AssembleService {
    private final HardPractice plugin;
    private final AnimationService animationService;
    private final ProfileService profileService;
    private final ConfigService configService;

    private final Map<UUID, AssembleBoard> boards = new ConcurrentHashMap<>();
    private final ChatColor[] chatColorCache = ChatColor.values();

    private AssembleAdapter adapter;
    private AssembleThread thread;
    private AssembleListener listeners;
    private AssembleStyle assembleStyle = AssembleStyle.MODERN;

    private boolean hook;
    private boolean debugMode = true;
    private boolean callEvents = true;
    private long ticks = 2;

    public AssembleServiceImpl(HardPractice plugin, AnimationService animationService, ProfileService profileService, ConfigService configService) {
        this.plugin = plugin;
        this.animationService = animationService;
        this.profileService = profileService;
        this.configService = configService;
    }

    @Override
    public void initialize(HardContext context) {
        this.adapter = new AssembleAdapterImpl(animationService, profileService, configService);

        this.listeners = new AssembleListener(this);
        this.plugin.getServer().getPluginManager().registerEvents(this.listeners, this.plugin);

        for (Player player : this.plugin.getServer().getOnlinePlayers()) {
            if (this.callEvents) {
                AssembleBoardCreateEvent createEvent = new AssembleBoardCreateEvent(player);
                Bukkit.getPluginManager().callEvent(createEvent);
                if (createEvent.isCancelled()) continue;
            }
            this.boards.putIfAbsent(player.getUniqueId(), new AssembleBoard(player, this));
        }

        this.thread = new AssembleThread(this);
    }

    @Override
    public void shutdown(HardContext context) {
        if (this.thread != null) {
            this.thread.interrupt();
            this.thread = null;
        }

        if (this.listeners != null) {
            HandlerList.unregisterAll(this.listeners);
            this.listeners = null;
        }

        for (UUID uuid : this.boards.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            }
        }
        this.boards.clear();
        Logger.info("A scoreboard do Assemble foi encerrada.");
    }

    @Override
    public void createBoard(Player player) {
        if (isCallEvents()) {
            AssembleBoardCreateEvent createEvent = new AssembleBoardCreateEvent(player);
            Bukkit.getPluginManager().callEvent(createEvent);
            if (createEvent.isCancelled()) {
                return;
            }
        }
        this.boards.put(player.getUniqueId(), new AssembleBoard(player, this));
    }

    @Override
    public void removeBoard(Player player) {
        if (isCallEvents()) {
            AssembleBoardDestroyEvent destroyEvent = new AssembleBoardDestroyEvent(player);
            Bukkit.getPluginManager().callEvent(destroyEvent);
            if (destroyEvent.isCancelled()) {
                return;
            }
        }
        this.boards.remove(player.getUniqueId());
        if (player.getScoreboard() != null) {
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
    }

    @Override
    public Map<UUID, AssembleBoard> getBoards() {
        return Collections.unmodifiableMap(this.boards);
    }
}
