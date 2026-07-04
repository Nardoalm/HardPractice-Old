package live.pvphard.practice.feature.abilities.types;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import live.pvphard.practice.HardPractice;
import live.pvphard.practice.feature.abilities.Ability;
import live.pvphard.practice.feature.abilities.AbilityService;
import live.pvphard.practice.common.time.DurationFormatter;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.enums.GlobalCooldown;
import live.pvphard.practice.common.PlayerUtil;
import live.pvphard.practice.common.text.CC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Map;
import java.util.UUID;

public class TimeWarp extends Ability {

    private final Map<UUID, Map<Location, Long>> lastPearl;

    public TimeWarp() {
        super("TIME_WARP");

        this.lastPearl = Maps.newConcurrentMap();

        Bukkit.getScheduler().runTaskTimerAsynchronously(HardPractice.getInstance(), () -> {

            for (Map.Entry<UUID, Map<Location, Long>> lastPearlEntry : this.lastPearl.entrySet()) {
                UUID uuid = lastPearlEntry.getKey();
                Map<Location, Long> map = lastPearlEntry.getValue();

                if (System.currentTimeMillis() > map.values().iterator().next()) {
                    this.lastPearl.remove(uuid);
                }
            }

        }, 20L, 20L);
    }

    @EventHandler
    public void checkCooldown(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        Profile profile = HardPractice.getInstance().getService(ProfileService.class).getProfile(player.getUniqueId());
        if (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)) {
            if (!isAbility(player.getItemInHand())) {
                return;
            }
            if (isAbility(player.getItemInHand())) {
                if (this.hasCooldown(player)) {
                    player.sendMessage(CC.translate("&fVocê está em cooldown por &4" + DurationFormatter.getRemaining(profile.getCooldown(TimeWarp.class).getRemainingMillis(player), true)));
                    event.setCancelled(true);
                    player.updateInventory();
                }
            }
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getEntityType() != EntityType.ENDER_PEARL) {
            return;
        }

        EnderPearl enderpearl = (EnderPearl) event.getEntity();

        if (!(enderpearl.getShooter() instanceof Player)) {
            return;
        }

        Player shooter = (Player) enderpearl.getShooter();
        long PEARL_EXPIRE = 15 * 1000;
        Map<Location, Long> map = ImmutableMap.of(shooter.getLocation().clone(),
                System.currentTimeMillis() + PEARL_EXPIRE);

        this.lastPearl.put(shooter.getUniqueId(), map);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        AbilityService abilityService = HardPractice.getInstance().getService(AbilityService.class);

        Profile profile = profileService.getProfile(player.getUniqueId());

        if (!event.getAction().name().contains("RIGHT_CLICK")) {
            return;
        }

        if (!isAbility(event.getItem())) {
            return;
        }

        if (profile.getCooldown(TimeWarp.class).onCooldown(player)) {
            event.setCancelled(true);
            player.sendMessage(CC.translate("&fVocê está no cooldown de &b&lTime Warp &7por &4" + DurationFormatter.getRemaining(profile.getCooldown(TimeWarp.class).getRemainingMillis(player), true, true)));
            player.updateInventory();
            event.setCancelled(true);
            return;
        }

        if (profile.getGlobalCooldown(GlobalCooldown.PARTNER_ITEM).onCooldown(player)) {
            player.sendMessage(CC.translate("&fVocê está no cooldown de &b&lPartner Item &fpor &b" + DurationFormatter.getRemaining(profile.getGlobalCooldown(GlobalCooldown.PARTNER_ITEM).getRemainingMillis(player), true, true)));
            player.updateInventory();
            event.setCancelled(true);
            return;
        }

        if (!this.lastPearl.containsKey(player.getUniqueId())) {
            player.sendMessage(CC.translate("&cA localização da sua última ender pearl expirou!"));
            return;
        }

        Location location = this.lastPearl.get(player.getUniqueId()).keySet().iterator().next().clone();

        PlayerUtil.decrement(player);

        profile.getCooldown(TimeWarp.class).applyCooldown(player, 60 * 1000);
        profile.getGlobalCooldown(GlobalCooldown.PARTNER_ITEM).applyCooldown(player, 10 * 1000);

        player.sendMessage(CC.translate(
                "&7Você ativou o Time Warp e será teleportado para a localização da sua última ender pearl em &43 &7segundos!"));

        Bukkit.getScheduler().runTaskLater(HardPractice.getInstance(), () -> {
            player.teleport(location);
            player.sendMessage(
                    CC.translate("&7Você foi &4teleportado &7para a localização da sua última ender pearl lançada!"));

            this.lastPearl.remove(player.getUniqueId());
        }, 60L);

        abilityService.sendCooldownExpiredMessage(player, this.getName(), this.getAbility());
        abilityService.sendPlayerMessage(player, this.getAbility());
    }
}