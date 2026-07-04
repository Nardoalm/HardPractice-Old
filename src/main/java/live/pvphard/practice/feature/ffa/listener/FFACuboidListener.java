package live.pvphard.practice.feature.ffa.listener;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.common.geom.Cuboid;
import live.pvphard.practice.core.locale.LocaleService;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.enums.ProfileState;
import live.pvphard.practice.feature.combat.CombatService;
import live.pvphard.practice.feature.ffa.FFAService;
import live.pvphard.practice.feature.ffa.FFAState;
import live.pvphard.practice.feature.ffa.spawn.FFASpawnService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Emmy
 * @project Hard
 * @since 25/01/2025
 */
public class FFACuboidListener implements Listener {
    private final Map<UUID, Boolean> playerStates = new HashMap<>();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Cuboid cuboid = HardPractice.getInstance().getService(FFASpawnService.class).getCuboid();
        if (cuboid == null) {
            return;
        }

        Player player = event.getPlayer();
        if (event.getFrom().getBlock().equals(event.getTo().getBlock())) {
            return;
        }

        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());
        if (profile == null || profile.getState() != ProfileState.FFA) {
            return;
        }

        FFAService ffaService = HardPractice.getInstance().getService(FFAService.class);
        CombatService combatService = HardPractice.getInstance().getService(CombatService.class);
        LocaleService localeService = HardPractice.getInstance().getService(LocaleService.class);

        if (ffaService.getFFAMatch(player) == null || !ffaService.getMatchByPlayer(player).isPresent()) {
            return;
        }

        UUID playerId = player.getUniqueId();

        boolean isInCuboid = cuboid.isIn(player);
        boolean wasInCuboid = this.playerStates.getOrDefault(playerId, true);

        if (isInCuboid != wasInCuboid) {
            if (isInCuboid) {
                if (combatService.isPlayerInCombat(playerId)) return;
                player.sendMessage(localeService.getString(GlobalMessagesLocaleImpl.FFA_SPAWN_ENTERED));
                ffaService.getMatchByPlayer(player).ifPresent(match -> match.getGameFFAPlayer(player).setState(FFAState.SPAWN));
            } else {
                player.sendMessage(localeService.getString(GlobalMessagesLocaleImpl.FFA_SPAWN_LEFT));
                ffaService.getMatchByPlayer(player).ifPresent(match -> match.getGameFFAPlayer(player).setState(FFAState.FIGHTING));
            }

            this.playerStates.put(playerId, isInCuboid);
        }
    }
}