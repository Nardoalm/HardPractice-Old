package live.pvphard.practice.feature.match.listener.types;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.feature.combat.CombatService;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.feature.kit.setting.types.mechanic.KitSettingNoDamageImpl;
import live.pvphard.practice.feature.kit.setting.types.mechanic.KitSettingNoFallDamageImpl;
import live.pvphard.practice.feature.kit.setting.types.mode.KitSettingBoxing;
import live.pvphard.practice.feature.kit.setting.types.mode.KitSettingHideAndSeek;
import live.pvphard.practice.feature.kit.setting.types.mode.KitSettingSpleef;
import live.pvphard.practice.feature.kit.setting.types.mode.KitSettingSumo;
import live.pvphard.practice.feature.kit.setting.types.visual.KitSettingBowShotIndicator;
import live.pvphard.practice.feature.kit.setting.types.visual.KitSettingHealthBar;
import live.pvphard.practice.feature.match.Match;
import live.pvphard.practice.feature.match.MatchState;
import live.pvphard.practice.feature.match.internal.types.HideAndSeekMatch;
import live.pvphard.practice.feature.match.model.internal.MatchGamePlayer;
import live.pvphard.practice.feature.match.model.GameParticipant;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.enums.ProfileState;
import live.pvphard.practice.adapter.core.CoreAdapter;
import live.pvphard.practice.common.PlayerDisplayUtil;
import live.pvphard.practice.common.reflect.ReflectionService;
import live.pvphard.practice.common.reflect.internal.types.ActionBarReflectionServiceImpl;
import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.common.text.Symbol;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class MatchDamageListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    private void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());
        if (profile == null) {
            return;
        }

        if (profile.getState() == ProfileState.SPECTATING) {
            event.setCancelled(true);
            return;
        }

        if (profile.getState() == ProfileState.PLAYING) {
            Match match = profile.getMatch();
            if (match == null) {
                event.setCancelled(true);
                return;
            }

            MatchGamePlayer gamePlayer = match.getGamePlayer(player);
            if (gamePlayer == null || gamePlayer.isDead()) {
                event.setCancelled(true);
                return;
            }

            Kit matchKit = match.getKit();

            if (matchKit.isSettingEnabled(KitSettingNoFallDamageImpl.class)
                    && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                event.setCancelled(true);
                return;
            }

            if (event.getCause() == EntityDamageEvent.DamageCause.FALL
                    && (matchKit.isSettingEnabled(KitSettingBoxing.class)
                    || matchKit.isSettingEnabled(KitSettingSumo.class)
                    || matchKit.isSettingEnabled(KitSettingSpleef.class))) {
                event.setCancelled(true);
                return;
            }

            if (match.getState() != MatchState.RUNNING) {
                event.setCancelled(true);
                return;
            }

            if (matchKit.isSettingEnabled(KitSettingBoxing.class)
                    || matchKit.isSettingEnabled(KitSettingSumo.class)
                    || matchKit.isSettingEnabled(KitSettingSpleef.class)
                    || matchKit.isSettingEnabled(KitSettingNoDamageImpl.class)) {

                event.setCancelled(true);
                Bukkit.getScheduler().runTask(HardPractice.getInstance(), () -> {
                    player.setHealth(player.getMaxHealth());
                    player.updateInventory();
                });
                return;
            }

            if (event.getFinalDamage() >= player.getHealth()) {
                event.setCancelled(true);

                EntityDamageEvent.DamageCause cause = event.getCause();
                Bukkit.getScheduler().runTask(HardPractice.getInstance(), () -> {
                    player.setHealth(player.getMaxHealth());
                    match.handleDeath(player, cause);
                });
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player damaged = (Player) event.getEntity();
        Player attacker;

        if (event.getDamager() instanceof Player) {
            attacker = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();
            if (projectile.getShooter() instanceof Player) {
                attacker = (Player) projectile.getShooter();
            } else {
                return;
            }
        } else {
            return;
        }

        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);

        Profile damagedProfile = profileService.getProfile(damaged.getUniqueId());
        Profile attackerProfile = profileService.getProfile(attacker.getUniqueId());
        if (damagedProfile == null || attackerProfile == null) {
            event.setCancelled(true);
            return;
        }

        if (damagedProfile.getState() == ProfileState.SPECTATING || attackerProfile.getState() == ProfileState.SPECTATING) {
            event.setCancelled(true);
            return;
        }

        if (damagedProfile.getState() == ProfileState.PLAYING && attackerProfile.getState() == ProfileState.PLAYING) {
            Match match = damagedProfile.getMatch();
            if (match == null || attackerProfile.getMatch() != match) {
                event.setCancelled(true);
                return;
            }

            if (match.getState() != MatchState.RUNNING) {
                event.setCancelled(true);
                return;
            }

            MatchGamePlayer damagedGamePlayer = match.getGamePlayer(damaged);
            MatchGamePlayer attackerGamePlayer = match.getGamePlayer(attacker);

            if (damagedGamePlayer == null || attackerGamePlayer == null) {
                event.setCancelled(true);
                return;
            }

            if (damagedGamePlayer.isDead() || attackerGamePlayer.isDead()) {
                event.setCancelled(true);
                return;
            }

            if (!attacker.getUniqueId().equals(damaged.getUniqueId()) && match.isInSameTeam(attacker, damaged)) {
                if (match.getKit().isSettingEnabled(KitSettingHideAndSeek.class)) {
                    HideAndSeekMatch matchHideAndSeek = (HideAndSeekMatch) attackerProfile.getMatch();
                    GameParticipant<MatchGamePlayer> seekers = matchHideAndSeek.getParticipantA();

                    boolean isSeeker = seekers.containsPlayer(attacker.getUniqueId());

                    if (matchHideAndSeek.getGameEndTask() == null && isSeeker) {
                        return;
                    }
                }

                event.setCancelled(true);
                return;
            }

            if (!attacker.getUniqueId().equals(damaged.getUniqueId())) {

                Bukkit.getScheduler().runTask(HardPractice.getInstance(), () -> {
                    attackerProfile.getMatch().getGamePlayer(attacker).getData().handleAttack();
                    damagedProfile.getMatch().getGamePlayer(damaged).getData().resetCombo();
                });

                GameParticipant<MatchGamePlayer> participant = match.getParticipant(attacker);
                GameParticipant<MatchGamePlayer> opponent = match.getParticipant(damaged);

                if (participant != null && opponent != null) {
                    participant.setTeamHits(participant.getTeamHits() + 1);

                    if (match.getKit().isSettingEnabled(KitSettingBowShotIndicator.class) && event.getDamager() instanceof Arrow) {
                        double finalHealth = damaged.getHealth() - event.getFinalDamage();
                        finalHealth = Math.max(0, finalHealth);

                        if (finalHealth > 0) {
                            String damagedName = PlayerDisplayUtil.resolveCurrentNick(damaged, damaged.getName());
                            attacker.sendMessage(CC.translate(HardPractice.getInstance().getService(CoreAdapter.class).getCore().getPlayerColor(damaged) + damagedName + " &7&l" + Symbol.ARROW_R + " &b" + String.format("%.1f", finalHealth) + " &c" + Symbol.HEART));
                        }
                    }

                    if (match.getKit().isSettingEnabled(KitSettingBoxing.class)) {
                        int lowestPlayerCount = match.getParticipants().stream()
                                .mapToInt(p -> p.getPlayers().size())
                                .filter(size -> size > 0)
                                .min()
                                .orElse(1);

                        int requiredHits = lowestPlayerCount * 100;

                        if (participant.getTeamHits() >= requiredHits) {
                            Bukkit.getScheduler().runTask(HardPractice.getInstance(), () -> {
                                opponent.getPlayers().forEach(matchGamePlayer -> {
                                    Player loser = matchGamePlayer.getTeamPlayer();
                                    if (loser != null) {
                                        match.handleDeath(loser, EntityDamageEvent.DamageCause.ENTITY_ATTACK);
                                    }
                                });
                            });
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onEntityDamageByEntityMonitor(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Profile profile = HardPractice.getInstance().getService(ProfileService.class).getProfile(event.getEntity().getUniqueId());
            if (profile == null) {
                event.setCancelled(true);
                return;
            }

            if (profile.getState() == ProfileState.SPECTATING) {
                event.setCancelled(true);
                return;
            }

            if (profile.getState() == ProfileState.PLAYING) {
                if (profile.getMatch() == null || profile.getMatch().getState() != MatchState.RUNNING) {
                    event.setCancelled(true);
                    return;
                }

                Player player = (Player) event.getEntity();
                Player attacker = (Player) event.getDamager();

                Bukkit.getScheduler().runTask(HardPractice.getInstance(), () -> {
                    HardPractice.getInstance().getService(CombatService.class).setLastAttacker(player, attacker);

                    if (profile.getMatch().getKit().isSettingEnabled(KitSettingHealthBar.class)) {
                        HardPractice.getInstance().getService(ReflectionService.class).getReflectionService(ActionBarReflectionServiceImpl.class).visualizeTargetHealth(attacker, player);
                    }
                });
            }
        }
    }
}