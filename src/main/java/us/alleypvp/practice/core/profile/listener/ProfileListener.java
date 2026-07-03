package us.alleypvp.practice.core.profile.listener;

import us.alleypvp.practice.AlleyPractice;
import us.alleypvp.practice.adapter.core.CoreAdapter;
import us.alleypvp.practice.common.PlayerUtil;
import us.alleypvp.practice.common.reflect.ReflectionService;
import us.alleypvp.practice.common.reflect.internal.types.TitleReflectionServiceImpl;
import us.alleypvp.practice.common.text.CC;
import us.alleypvp.practice.core.locale.LocaleService;
import us.alleypvp.practice.core.locale.internal.impl.VisualsLocaleImpl;
import us.alleypvp.practice.core.profile.Profile;
import us.alleypvp.practice.core.profile.ProfileService;
import us.alleypvp.practice.core.profile.enums.ProfileState;
import us.alleypvp.practice.feature.hotbar.HotbarService;
import us.alleypvp.practice.feature.spawn.SpawnService;
import us.alleypvp.practice.feature.visibility.VisibilityService;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.InventoryHolder;

public class ProfileListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onLogin(PlayerLoginEvent event) {
        if (!AlleyPractice.getInstance().isEnabled()) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, CC.translate("&cO servidor ainda está carregando, tente novamente em alguns segundos."));
            return;
        }

        Player player = event.getPlayer();

        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            return;
        }

        Profile profile = new Profile(player.getUniqueId(), player.getName());
        profile.load();

        ProfileService profileService = AlleyPractice.getInstance().getService(ProfileService.class);
        profileService.getProfile(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onJoin(PlayerJoinEvent event) {
        if (!AlleyPractice.getInstance().isEnabled()) {
            event.getPlayer().kickPlayer(CC.translate("&cO servidor ainda está carregando, tente novamente em alguns segundos."));
            return;
        }

        event.setJoinMessage(null);

        Player player = event.getPlayer();
        ProfileService profileService = AlleyPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());

        this.handlePlayerJoin(profile, player);
        this.sendJoinMessageTitle(player);
    }

    @EventHandler
    private void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        ProfileService profileService = AlleyPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());

        if (profile.getState() == ProfileState.LOBBY
                || profile.getState() == ProfileState.SPECTATING
                || profile.getState() == ProfileState.EDITING
                || profile.getState() == ProfileState.WAITING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onPlayerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ProfileService profileService = AlleyPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());

        org.bukkit.scoreboard.Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard != null) {
            org.bukkit.scoreboard.Objective objective = scoreboard.getObjective("showhealth");
            if (objective != null) {
                objective.unregister();
            }
        }

        event.setQuitMessage(null);

        profile.updatePlayTime();
        profile.setOnline(false);
        profile.save();

        profileService.removeProfile(player.getUniqueId());
    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        ProfileService profileService = AlleyPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());

        if (profile.getState() == ProfileState.LOBBY
                || profile.getState() == ProfileState.EDITING
                || profile.getState() == ProfileState.SPECTATING) {
            if (player.getGameMode() == GameMode.CREATIVE) return;
            event.setCancelled(true);

            Block block = event.getClickedBlock();
            if (block != null && block.getState() instanceof InventoryHolder) {
                if (block.getType() == Material.CHEST || block.getType() == Material.DISPENSER || block.getType() == Material.FURNACE || block.getType() == Material.BREWING_STAND) {
                    event.setCancelled(true);
                }
            }
        }
    }

    private void handlePlayerJoin(Profile profile, Player player) {
        CoreAdapter coreAdapter = AlleyPractice.getInstance().getService(CoreAdapter.class);
        SpawnService spawnService = AlleyPractice.getInstance().getService(SpawnService.class);
        HotbarService hotbarService = AlleyPractice.getInstance().getService(HotbarService.class);
        VisibilityService visibilityService = AlleyPractice.getInstance().getService(VisibilityService.class);

        profile.setState(ProfileState.LOBBY);
        profile.setName(player.getName());
        profile.setOnline(true);
        profile.setMatch(null);
        profile.setParty(null);
        profile.setFfaMatch(null);

        profile.getProfileData().getSettingData().setTimeBasedOnProfileSetting(player);
        profile.getProfileData().getPlayTimeData().setLastLogin(System.currentTimeMillis());
        profile.getProfileData().determineLevel();

        player.setFlySpeed(1 * 0.1F);
        player.setWalkSpeed(2 * 0.1F);
        player.getInventory().setHeldItemSlot(0);

        PlayerUtil.reset(player, false, true);

        spawnService.teleportToSpawn(player);
        hotbarService.applyHotbarItems(player);
        visibilityService.updateVisibility(player);

        player.updateInventory();
    }

    private void sendJoinMessageTitle(Player player) {
        TitleReflectionServiceImpl titleService = AlleyPractice.getInstance().getService(ReflectionService.class).getReflectionService(TitleReflectionServiceImpl.class);

        boolean enabled = AlleyPractice.getInstance().getService(LocaleService.class).getBoolean(VisualsLocaleImpl.TITLE_JOIN_MESSAGE_ENABLED);
        if (!enabled) return;

        String header = AlleyPractice.getInstance().getService(LocaleService.class).getString(VisualsLocaleImpl.TITLE_JOIN_MESSAGE_HEADER);
        String subHeader = AlleyPractice.getInstance().getService(LocaleService.class).getString(VisualsLocaleImpl.TITLE_JOIN_MESSAGE_SUBHEADER);
        int fadeIn = AlleyPractice.getInstance().getService(LocaleService.class).getInt(VisualsLocaleImpl.TITLE_JOIN_MESSAGE_FADE_IN);
        int stay = AlleyPractice.getInstance().getService(LocaleService.class).getInt(VisualsLocaleImpl.TITLE_JOIN_MESSAGE_STAY);
        int fadeOut = AlleyPractice.getInstance().getService(LocaleService.class).getInt(VisualsLocaleImpl.TITLE_JOIN_MESSAGE_FADE_OUT);

        titleService.sendTitle(player, header, subHeader, fadeIn, stay, fadeOut);
    }
}