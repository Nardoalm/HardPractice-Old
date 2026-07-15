package live.pvphard.practice.feature.ffa.internal;

import live.pvphard.practice.bootstrap.HardContext;
import live.pvphard.practice.bootstrap.annotation.Service;
import live.pvphard.practice.common.logger.Logger;
import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.feature.arena.Arena;
import live.pvphard.practice.feature.arena.ArenaService;
import live.pvphard.practice.feature.ffa.FFAMatch;
import live.pvphard.practice.feature.ffa.FFAService;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.feature.kit.KitService;
import live.pvphard.practice.feature.kit.setting.types.mechanic.KitSettingBuildImpl;
import live.pvphard.practice.feature.kit.setting.types.mode.KitSettingBoxing;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Emmy
 * @project Hard
 * @since 11/04/2025
 */
@Getter
@Service(provides = FFAService.class, priority = 130)
public class FFAServiceImpl implements FFAService {
    private final KitService kitService;
    private final ArenaService arenaService;

    private final List<FFAMatch> matches = new ArrayList<>();
    private final List<Kit> ffaKits = new ArrayList<>();
    private final int defaultPlayerSize = 20;

    /**
     * Constructor for DI.
     */
    public FFAServiceImpl(KitService kitService, ArenaService arenaService) {
        this.kitService = kitService;
        this.arenaService = arenaService;
    }

    @Override
    public void initialize(HardContext context) {
        this.ffaKits.addAll(this.kitService.getKits().stream().filter(Kit::isFfaEnabled).collect(Collectors.toList()));
        this.initializeMatches();
    }

    @Override
    public void shutdown(HardContext context) {
        this.matches.forEach(match -> {
            new ArrayList<>(match.getPlayers()).forEach(ffaPlayer -> {
                Player player = ffaPlayer.getPlayer();
                if (player != null) {
                    match.leave(player);
                    player.sendMessage(CC.translate("&cA arena de FFA está sendo fechada devido ao desligamento do servidor."));
                }
            });
        });
        this.matches.clear();
        Logger.info("Cleaned up all FFA matches.");
    }

    /**
     * Load all FFA matches
     */
    public void initializeMatches() {
        for (Kit kit : this.ffaKits) {
            Arena arena = this.arenaService.getArenaByName(kit.getFfaArenaName());
            if (arena == null) {
                Logger.error("Kit " + kit.getName() + " has no FFA arena set. Please set the FFA arena in the kit settings.");
                continue;
            }

            if (kit.getMaxFfaPlayers() <= 0) {
                kit.setMaxFfaPlayers(this.defaultPlayerSize);
                Logger.error("FFA match for kit " + kit.getName() + " has a max player size of 0. Setting to default of " + this.defaultPlayerSize + " players.");
            }

            this.createFFAMatch(arena, kit, kit.getMaxFfaPlayers());
        }
    }

    @Override
    public void createFFAMatch(Arena arena, Kit kit, int maxPlayers) {
        DefaultFFAMatch match = new DefaultFFAMatch(kit.getName(), arena, kit, maxPlayers);
        this.matches.add(match);
    }

    @Override
    public Optional<FFAMatch> getMatchByPlayer(Player player) {
        return this.matches.stream().filter(match -> match.getPlayers().contains(match.getGameFFAPlayer(player))).findFirst();
    }

    @Override
    public FFAMatch getFFAMatch(String kitName) {
        return this.matches.stream()
                .filter(match -> match.getKit().getName().equalsIgnoreCase(kitName))
                .findFirst()
                .orElse(null);
    }

    @Override
    public FFAMatch getFFAMatch(Player player) {
        return this.getMatchByPlayer(player).orElse(null);
    }

    @Override
    public void reloadFFAKits() {
        this.shutdown(null);

        this.ffaKits.clear();
        this.ffaKits.addAll(this.kitService.getKits().stream().filter(Kit::isFfaEnabled).collect(Collectors.toList()));
        this.initializeMatches();
    }

    @Override
    public boolean isNotEligibleForFFA(Kit kit) {
        return kit.isSettingEnabled(KitSettingBuildImpl.class) || kit.isSettingEnabled(KitSettingBoxing.class);
    }
}