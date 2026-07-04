package live.pvphard.practice.feature.match.snapshot.listener;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.feature.match.Match;
import live.pvphard.practice.feature.match.MatchState;
import live.pvphard.practice.feature.match.model.internal.MatchGamePlayer;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.enums.ProfileState;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;

/**
 * @author Emmy
 * @project hard-practice
 * @since 01/07/2025
 */
public class SnapshotListener implements Listener {
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    private void onPotionSplashEvent(PotionSplashEvent event) {
        if (!(event.getPotion().getShooter() instanceof Player)) {
            return;
        }

        Player shooter = (Player) event.getPotion().getShooter();
        Profile profile = HardPractice.getInstance().getService(ProfileService.class).getProfile(shooter.getUniqueId());

        ProfileState profileState = profile.getState();
        if (profileState != ProfileState.PLAYING) {
            return;
        }

        Match match = profile.getMatch();
        if (match == null || match.getState() != MatchState.RUNNING) {
            return;
        }

        MatchGamePlayer gamePlayer = match.getGamePlayer(shooter);

        if (event.getIntensity(shooter) <= 0.5D) {
            gamePlayer.getData().incrementMissedPotions();
        }
    }

    @EventHandler
    private void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player) || !(event.getEntity() instanceof ThrownPotion)) {
            return;
        }

        Player shooter = (Player) event.getEntity().getShooter();
        Profile profile = HardPractice.getInstance().getService(ProfileService.class).getProfile(shooter.getUniqueId());

        if (profile.getState() != ProfileState.PLAYING) {
            return;
        }

        Match match = profile.getMatch();
        if (match == null || match.getState() != MatchState.RUNNING) {
            return;
        }

        MatchGamePlayer gamePlayer = match.getGamePlayer(shooter);
        gamePlayer.getData().incrementThrownPotions();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }

        Player attacker = (Player) event.getDamager();
        Player defender = (Player) event.getEntity();

        Profile attackerProfile = HardPractice.getInstance().getService(ProfileService.class).getProfile(attacker.getUniqueId());
        Profile defenderProfile = HardPractice.getInstance().getService(ProfileService.class).getProfile(defender.getUniqueId());

        if (attackerProfile.getState() != ProfileState.PLAYING ||
                defenderProfile.getState() != ProfileState.PLAYING) {
            return;
        }

        Match match = attackerProfile.getMatch();
        if (match == null || match != defenderProfile.getMatch() || match.getState() != MatchState.RUNNING) {
            return;
        }

        MatchGamePlayer attackerGamePlayer = match.getGamePlayer(attacker);
        MatchGamePlayer defenderGamePlayer = match.getGamePlayer(defender);

        boolean isCritical = !attacker.isOnGround() && attacker.getFallDistance() > 0;
        if (isCritical) {
            attackerGamePlayer.getData().incrementCriticalHits();
        }

        boolean isBlocked = defender.isBlocking();
        if (isBlocked) {
            defenderGamePlayer.getData().incrementBlockedHits();
        }
    }
}