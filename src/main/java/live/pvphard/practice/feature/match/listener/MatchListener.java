package live.pvphard.practice.feature.match.listener;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.common.InventoryUtil;
import live.pvphard.practice.feature.cooldown.CooldownService;
import live.pvphard.practice.feature.cooldown.CooldownType;
import live.pvphard.practice.feature.kit.setting.types.mode.*;
import org.bukkit.event.entity.PlayerDeathEvent;
import live.pvphard.practice.common.ListenerUtil;
import live.pvphard.practice.common.PlayerDisplayUtil;
import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.locale.LocaleService;
import live.pvphard.practice.core.locale.internal.impl.message.GameMessagesLocaleImpl;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.enums.ProfileState;
import live.pvphard.practice.feature.arena.ArenaType;
import live.pvphard.practice.feature.arena.internal.types.StandAloneArena;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.feature.kit.setting.types.mechanic.KitSettingDenyMovementImpl;
import live.pvphard.practice.feature.kit.setting.types.mechanic.KitSettingNoHungerImpl;
import live.pvphard.practice.feature.kit.setting.types.mechanic.KitSettingSoup;
import live.pvphard.practice.feature.kit.setting.types.mechanic.KitSettingVoidDeathImpl;
import live.pvphard.practice.feature.match.Match;
import live.pvphard.practice.feature.match.MatchState;
import live.pvphard.practice.feature.match.internal.types.RoundsMatch;
import live.pvphard.practice.feature.match.model.GameParticipant;
import live.pvphard.practice.feature.match.model.internal.MatchGamePlayer;
import live.pvphard.practice.feature.match.utility.MatchUtility;
import live.pvphard.practice.library.menu.Menu;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MatchListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());

        if (profile.getState() == ProfileState.SPECTATING || profile.getState() == ProfileState.PLAYING) {
            if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
                if (MatchUtility.isBeyondBounds(event.getTo(), profile)) {
                    event.setCancelled(true);
                    player.sendMessage(CC.translate("&cYou cannot leave the arena."));

                    // INJETADO O REEMBOLSO: Evita o jogador de perder a pérola no inventário e ficar de cooldown quando a ação é cancelada.
                    InventoryUtil.giveItem(player, Material.ENDER_PEARL, 1);
                    player.updateInventory();

                    CooldownService cooldownService = HardPractice.getInstance().getService(CooldownService.class);
                    if (cooldownService != null) {
                        cooldownService.removeCooldown(player.getUniqueId(), CooldownType.ENDER_PEARL);
                    }
                }
            }
        }
    }

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());
        Match match = profile.getMatch();
        if (match == null) return;

        MatchGamePlayer gamePlayer = match.getGamePlayer(player);
        if (gamePlayer == null || gamePlayer.isDead()) return;

        Kit matchKit = match.getKit();
        if (profile.getState() == ProfileState.PLAYING && profile.getMatch().getState() == MatchState.RUNNING) {
            if (matchKit.isSettingEnabled(KitSettingSumo.class) || matchKit.isSettingEnabled(KitSettingSpleef.class)) {
                if (player.getLocation().getBlock().getType() == Material.WATER || player.getLocation().getBlock().getType() == Material.STATIONARY_WATER) {
                    player.setHealth(0);
                }
            }

            if (match.getArena() instanceof StandAloneArena) {
                StandAloneArena arena = (StandAloneArena) match.getArena();
                int effectiveVoidLevel = resolveEffectiveVoidLevel(arena, match);
                if (player.getLocation().getY() <= effectiveVoidLevel && matchKit.isSettingEnabled(KitSettingVoidDeathImpl.class)) {
                    if (player.getGameMode() == GameMode.SPECTATOR) return;
                    if (player.getGameMode() == GameMode.CREATIVE) return;
                    if (match.getArena().getType() != ArenaType.STANDALONE) return;
                    if (profile.getState() != ProfileState.PLAYING) return;

                    gamePlayer.setDead(true);

                    if (match instanceof RoundsMatch && match.getKit().isSettingEnabled(KitSettingStickFight.class)) {
                        RoundsMatch roundsMatch = (RoundsMatch) match;
                        roundsMatch.handleDeath(player, EntityDamageEvent.DamageCause.VOID);
                        return;
                    }

                    profile.getMatch().handleDeath(player, EntityDamageEvent.DamageCause.VOID);
                }
            }
        }

        if (profile.getState() == ProfileState.PLAYING) {
            if (match.getState() == MatchState.STARTING || match.getState() == MatchState.ENDING_ROUND || match.getState() == MatchState.RESTARTING_ROUND) {
                if (matchKit.isSettingEnabled(KitSettingDenyMovementImpl.class)) {
                    Location from = event.getFrom();
                    Location to = event.getTo();
                    if (to != null && (from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ())) {
                        event.setTo(new Location(
                                from.getWorld(),
                                from.getX(),
                                from.getY(),
                                from.getZ(),
                                to.getYaw(),
                                to.getPitch()
                        ));
                    }
                }
            }
        }
    }

    private int resolveEffectiveVoidLevel(StandAloneArena arena, Match match) {
        int configuredVoidLevel = arena.getVoidLevel();
        Location pos1 = match.getArena().getPos1();
        Location pos2 = match.getArena().getPos2();

        double lowestSpawnY = Double.MAX_VALUE;
        if (pos1 != null) {
            lowestSpawnY = Math.min(lowestSpawnY, pos1.getY());
        }
        if (pos2 != null) {
            lowestSpawnY = Math.min(lowestSpawnY, pos2.getY());
        }

        if (lowestSpawnY == Double.MAX_VALUE) {
            return configuredVoidLevel;
        }

        int maxReasonableVoidLevel = (int) Math.floor(lowestSpawnY - 2.0D);
        return Math.min(configuredVoidLevel, maxReasonableVoidLevel);
    }

    @EventHandler
    private void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;

        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());
        if (profile.getState() == ProfileState.PLAYING) {
            event.setRespawnLocation(player.getLocation());
        }
    }

    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());
        if (profile.getState() != ProfileState.PLAYING || profile.getMatch() == null) return;

        event.setDeathMessage(null);

        HardPractice.getInstance().getServer().getScheduler().runTask(HardPractice.getInstance(), () -> {
            if (!player.isOnline()) {
                return;
            }

            profile.getMatch().handleDeathItemDrop(player, event);
            player.spigot().respawn();

            EntityDamageEvent.DamageCause cause = player.getLastDamageCause() != null ? player.getLastDamageCause().getCause() : EntityDamageEvent.DamageCause.CUSTOM;
            profile.getMatch().handleDeath(player, cause);
        });
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getClickedInventory() == null) return;

        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());
        if (profile.getState() == ProfileState.SPECTATING) {
            if (!Menu.currentlyOpenedMenus.containsKey(player.getName())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());

        if (profile.getState() == ProfileState.SPECTATING) {
            event.setCancelled(true);
            return;
        }

        if (profile.getState() == ProfileState.PLAYING) {
            if (ListenerUtil.isSword(event.getItemDrop().getItemStack().getType())) {
                event.setCancelled(true);
                player.sendMessage(HardPractice.getInstance().getService(LocaleService.class).getString(GameMessagesLocaleImpl.GAME_CANNOT_DROP_SWORD));
                return;
            }
        }
        ListenerUtil.clearDroppedItemsOnRegularItemDrop(event.getItemDrop());
    }

    @EventHandler
    private void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());
        if (profile.getState() == ProfileState.SPECTATING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());
        if (profile.getState() != ProfileState.PLAYING) {
            return;
        }

        ItemStack item = event.getItem();
        if (item.getType() == Material.POTION) {
            HardPractice.getInstance().getServer().getScheduler().runTaskLater(HardPractice.getInstance(), () -> {
                player.getInventory().removeItem(new ItemStack(Material.GLASS_BOTTLE, 1));
                player.updateInventory();
            }, 1L);
        }
    }

    @EventHandler
    private void onSoupUse(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.MUSHROOM_SOUP) {
            return;
        }

        Player player = event.getPlayer();
        Profile profile = HardPractice.getInstance().getService(ProfileService.class).getProfile(player.getUniqueId());
        if (profile.getState() != ProfileState.PLAYING || profile.getMatch() == null) {
            return;
        }

        if (!profile.getMatch().getKit().isSettingEnabled(KitSettingSoup.class)) {
            return;
        }

        double maxHealth = player.getMaxHealth();
        if (player.getHealth() >= maxHealth) {
            return;
        }

        event.setCancelled(true);
        player.setHealth(Math.min(maxHealth, player.getHealth() + 7.0D));
        player.setItemInHand(new ItemStack(Material.BOWL));
        player.updateInventory();
    }

    @EventHandler
    private void onHunger(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
            Profile profile = profileService.getProfile(player.getUniqueId());
            if (profile.getState() != ProfileState.PLAYING) return;

            if (profile.getMatch().getKit().isSettingEnabled(KitSettingNoHungerImpl.class)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPortal(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());
        if (profile.getState() == ProfileState.PLAYING) {
            if (!(profile.getMatch() instanceof RoundsMatch)) return;
            RoundsMatch match = (RoundsMatch) profile.getMatch();
            if (match.getKit().isSettingEnabled(KitSettingRounds.class)) {
                if (player.getGameMode() == GameMode.CREATIVE) return;
                if (player.getGameMode() == GameMode.SPECTATOR) return;
                if (player.getLocation().getBlock().getType() == Material.ENDER_PORTAL || player.getLocation().getBlock().getType() == Material.ENDER_PORTAL_FRAME) {
                    StandAloneArena arena = (StandAloneArena) match.getArena();
                    GameParticipant<MatchGamePlayer> playerTeam = match.getParticipantA().containsPlayer(player.getUniqueId())
                            ? match.getParticipantA()
                            : match.getParticipantB();

                    if (!arena.isEnemyPortal(match, player.getLocation(), playerTeam)) {
                        player.sendMessage(CC.translate("&cYou cannot enter your own portal!"));

                        if (match.getKit().isSettingEnabled(KitSettingRespawnTimer.class)) {
                            player.setHealth(0);
                            player.setAllowFlight(true);
                            player.setFlying(true);
                            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
                        } else {
                            Location spawnLocation = match.getParticipantA().containsPlayer(player.getUniqueId()) ? match.getArena().getPos1() : match.getArena().getPos2();
                            player.teleport(spawnLocation);
                        }
                        return;
                    }

                    if (match.getState() == MatchState.ENDING_ROUND || match.getState() == MatchState.ENDING_MATCH || match.getState() == MatchState.RESTARTING_ROUND) {
                        return;
                    }

                    GameParticipant<MatchGamePlayer> opponent = match.getParticipantA().containsPlayer(player.getUniqueId()) ? match.getParticipantB() : match.getParticipantA();
                    opponent.getPlayers().forEach(matchGamePlayer -> matchGamePlayer.setDead(true));

                    if (match.canEndRound()) {
                        match.setScorer(PlayerDisplayUtil.resolveCurrentNick(player, player.getName()));
                        match.handleRoundEnd();

                        if (match.canEndMatch()) {
                            Location spawnLocation = match.getParticipantA().containsPlayer(player.getUniqueId()) ? match.getArena().getPos1() : match.getArena().getPos2();
                            player.teleport(spawnLocation);

                            match.setEndTime(System.currentTimeMillis());
                            match.setState(MatchState.ENDING_MATCH);
                            match.getRunnable().setStage(4);
                        }
                    }
                }
            }
        }
    }
}