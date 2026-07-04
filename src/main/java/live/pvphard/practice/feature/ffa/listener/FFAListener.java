package live.pvphard.practice.feature.ffa.listener;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.common.InventoryUtil;
import live.pvphard.practice.common.ListenerUtil;
import live.pvphard.practice.core.locale.LocaleService;
import live.pvphard.practice.core.locale.internal.impl.message.GameMessagesLocaleImpl;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.enums.ProfileState;
import live.pvphard.practice.feature.combat.CombatService;
import live.pvphard.practice.feature.cooldown.Cooldown;
import live.pvphard.practice.feature.cooldown.CooldownService;
import live.pvphard.practice.feature.cooldown.CooldownType;
import live.pvphard.practice.feature.ffa.spawn.FFASpawnService;
import live.pvphard.practice.feature.kit.setting.types.mechanic.KitSettingNoHungerImpl;
import live.pvphard.practice.feature.kit.setting.types.mechanic.KitSettingSoup;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/**
 * @author Emmy
 * @project Hard
 * @date 25/05/2024 - 14:24
 */
public class FFAListener implements Listener {

    @EventHandler
    private void onPlayerDropItem(PlayerDropItemEvent event) {
        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        LocaleService localeService = HardPractice.getInstance().getService(LocaleService.class);

        Player player = event.getPlayer();
        Profile profile = profileService.getProfile(player.getUniqueId());
        if (profile.getState() != ProfileState.FFA) return;

        if (ListenerUtil.isSword(event.getItemDrop().getItemStack().getType())) {
            event.setCancelled(true);
            player.sendMessage(localeService.getString(GameMessagesLocaleImpl.GAME_CANNOT_DROP_SWORD));
            return;
        }

        ListenerUtil.clearDroppedItemsOnRegularItemDrop(event.getItemDrop());
    }

    @EventHandler
    private void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());
        if (profile.getState() != ProfileState.FFA) {
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
        if (profile.getState() != ProfileState.FFA || profile.getFfaMatch() == null) {
            return;
        }

        if (!profile.getFfaMatch().getKit().isSettingEnabled(KitSettingSoup.class)) {
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
    private void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());
        if (profile.getState() != ProfileState.FFA) return;
        event.setDeathMessage(null);

        ListenerUtil.clearDroppedItemsOnDeath(event, player);

        Player killer = HardPractice.getInstance().getService(CombatService.class).getLastAttacker(player);

        HardPractice.getInstance().getServer().getScheduler().runTaskLater(HardPractice.getInstance(), () -> player.spigot().respawn(), 1L);
        Bukkit.getScheduler().runTaskLater(HardPractice.getInstance(), () -> profile.getFfaMatch().handleDeath(player, killer), 1L);
    }

    @EventHandler
    private void onPearlLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof EnderPearl)) return;
        if (!(event.getEntity().getShooter() instanceof Player)) return;

        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        LocaleService localeService = HardPractice.getInstance().getService(LocaleService.class);

        Player player = (Player) event.getEntity().getShooter();
        Profile profile = profileService.getProfile(player.getUniqueId());

        if (profile.getState() != ProfileState.FFA) return;

        if (HardPractice.getInstance().getService(FFASpawnService.class).getCuboid().isIn(player)) {
            event.setCancelled(true);
            InventoryUtil.giveItem(player, Material.ENDER_PEARL, 1);
            player.updateInventory();
            player.sendMessage(localeService.getString(GlobalMessagesLocaleImpl.COOLDOWN_CANNOT_USE_AT_FFA_SPAWN));
            return;
        }

        CooldownService cooldownService = HardPractice.getInstance().getService(CooldownService.class);
        Optional<Cooldown> optionalCooldown = Optional.ofNullable(cooldownService.getCooldown(player.getUniqueId(), CooldownType.ENDER_PEARL));

        if (optionalCooldown.isPresent() && optionalCooldown.get().isActive()) {
            event.setCancelled(true);
            InventoryUtil.giveItem(player, Material.ENDER_PEARL, 1);
            player.updateInventory();
            player.sendMessage(localeService.getString(GlobalMessagesLocaleImpl.COOLDOWN_PEARL_MUST_WAIT).replace("{time}", String.valueOf(optionalCooldown.get().remainingTime())));
            return;
        }

        Cooldown cooldown = optionalCooldown.orElseGet(() -> {
            Cooldown newCooldown = new Cooldown(CooldownType.ENDER_PEARL, () -> player.sendMessage(localeService.getString(GlobalMessagesLocaleImpl.COOLDOWN_CAN_NOW_USE_PEARLS_AGAIN)));
            cooldownService.addCooldown(player.getUniqueId(), CooldownType.ENDER_PEARL, newCooldown);
            return newCooldown;
        });

        cooldown.resetCooldown();
    }

    @EventHandler
    private void onHunger(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
            Profile profile = profileService.getProfile(player.getUniqueId());
            if (profile.getState() != ProfileState.FFA) return;

            if (profile.getFfaMatch().getKit().isSettingEnabled(KitSettingNoHungerImpl.class)) {
                event.setCancelled(true);
            }
        }
    }
}
