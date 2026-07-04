package live.pvphard.practice.library.assemble;

import live.pvphard.practice.common.logger.Logger;
import live.pvphard.practice.library.assemble.events.AssembleBoardCreatedEvent;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Getter
public class AssembleBoard {
    private final AssembleServiceImpl assembleServiceImpl;
    private final List<AssembleBoardEntry> entries;
    private final List<String> identifiers;
    private final UUID uuid;

    // Cache para evitar criar scoreboard múltiplas vezes
    private static final ConcurrentMap<UUID, Scoreboard> scoreboardCache = new ConcurrentHashMap<>();
    private volatile Scoreboard cachedScoreboard;

    public AssembleBoard(Player player, AssembleServiceImpl assembleServiceImpl) {
        this.assembleServiceImpl = assembleServiceImpl;
        this.entries = new ArrayList<>();
        this.identifiers = new ArrayList<>();
        this.uuid = player.getUniqueId();
        this.setup(player);
    }

    /**
     * Get's a player's bukkit scoreboard (THREAD-SAFE).
     */
    public Scoreboard getScoreboard() {
        // Retorna do cache se já existe
        if (cachedScoreboard != null) {
            return cachedScoreboard;
        }

        // Verifica cache estático
        Scoreboard cached = scoreboardCache.get(uuid);
        if (cached != null) {
            cachedScoreboard = cached;
            return cached;
        }

        Player player = Bukkit.getPlayer(getUuid());

        // Se player existe e já tem scoreboard (e não é o main), usa ele
        if (player != null && (this.assembleServiceImpl.isHook() ||
                (player.getScoreboard() != null && player.getScoreboard() != Bukkit.getScoreboardManager().getMainScoreboard()))) {
            cachedScoreboard = player.getScoreboard();
            scoreboardCache.put(uuid, cachedScoreboard);
            return cachedScoreboard;
        }

        // CRIAÇÃO SEGURA - Força na main thread
        return createScoreboardSync();
    }

    /**
     * Cria scoreboard de forma thread-safe.
     */
    private Scoreboard createScoreboardSync() {
        // Se já está na main thread, cria diretamente
        if (Bukkit.isPrimaryThread()) {
            Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            scoreboardCache.put(uuid, scoreboard);
            cachedScoreboard = scoreboard;
            return scoreboard;
        }

        // Está em thread assíncrona - força na main thread
        final Scoreboard[] result = new Scoreboard[1];
        final Object lock = new Object();

        synchronized (lock) {
            Bukkit.getScheduler().runTask(assembleServiceImpl.getPlugin(), () -> {
                result[0] = Bukkit.getScoreboardManager().getNewScoreboard();
                synchronized (lock) {
                    lock.notifyAll();
                }
            });

            try {
                lock.wait(5000); // Timeout de 5 segundos
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Logger.error("Falha ao criar scoreboard para " + uuid);
                if (Bukkit.isPrimaryThread()) {
                    result[0] = Bukkit.getScoreboardManager().getNewScoreboard();
                }
            }
        }

        if (result[0] == null) {
            throw new RuntimeException("Não foi possível criar scoreboard para " + uuid);
        }

        scoreboardCache.put(uuid, result[0]);
        cachedScoreboard = result[0];
        return result[0];
    }

    /**
     * Get's the player's scoreboard objective.
     */
    public Objective getObjective() {
        Scoreboard scoreboard = this.getScoreboard();
        if (scoreboard.getObjective("Assemble") == null) {
            Objective objective = scoreboard.registerNewObjective("Assemble", "dummy");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            Player player = Bukkit.getPlayer(getUuid());
            if (player != null) {
                objective.setDisplayName(getAssembleServiceImpl().getAdapter().getTitle(player));
            }
            return objective;
        }
        return scoreboard.getObjective("Assemble");
    }

    /**
     * Setup the board for a player (THREAD-SAFE).
     */
    private void setup(Player player) {
        // Força setup na main thread
        if (Bukkit.isPrimaryThread()) {
            setupSync(player);
        } else {
            Bukkit.getScheduler().runTask(assembleServiceImpl.getPlugin(), () -> setupSync(player));
        }
    }

    /**
     * Setup sincronizado executado na main thread.
     */
    private void setupSync(Player player) {
        Scoreboard scoreboard = getScoreboard();
        player.setScoreboard(scoreboard);
        this.getObjective();

        if (this.assembleServiceImpl.isCallEvents()) {
            AssembleBoardCreatedEvent createdEvent = new AssembleBoardCreatedEvent(this);
            Bukkit.getPluginManager().callEvent(createdEvent);
        }
    }

    /**
     * Get the board entry at a specific position.
     */
    public AssembleBoardEntry getEntryAtPosition(int pos) {
        return pos >= this.entries.size() ? null : this.entries.get(pos);
    }

    /**
     * Get the unique identifier for position in scoreboard.
     */
    public String getUniqueIdentifier(int position) {
        String identifier = this.getRandomChatColor(position) + ChatColor.WHITE;

        while (this.identifiers.contains(identifier)) {
            identifier = identifier + this.getRandomChatColor(position) + ChatColor.WHITE;
        }

        if (identifier.length() > 16) {
            return this.getUniqueIdentifier(position);
        }

        this.identifiers.add(identifier);
        return identifier;
    }

    /**
     * Gets a ChatColor based off the position in the collection.
     */
    private String getRandomChatColor(int position) {
        return this.assembleServiceImpl.getChatColorCache()[position].toString();
    }

    /**
     * Limpa o cache do player
     */
    public static void cleanup(UUID uuid) {
        scoreboardCache.remove(uuid);
    }
}