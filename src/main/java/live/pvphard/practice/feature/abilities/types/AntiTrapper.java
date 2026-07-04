package live.pvphard.practice.feature.abilities.types;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.feature.abilities.Ability;
import live.pvphard.practice.feature.abilities.AbilityService;
import live.pvphard.practice.common.time.DurationFormatter;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.enums.GlobalCooldown;
import live.pvphard.practice.common.PlayerUtil;
import live.pvphard.practice.common.time.TimeUtil;
import live.pvphard.practice.common.text.CC;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;

public class AntiTrapper extends Ability {

    private final HardPractice plugin = HardPractice.getInstance();
    public static Map<String, Long> cooldownvic;
    public int count;

    public AntiTrapper() {
        super("ANTI_TRAPPER");
        AntiTrapper.cooldownvic = new HashMap<>();
        this.count = 0;
    }

    static {
        AntiTrapper.cooldownvic = new HashMap<>();
    }

    public static boolean isOnCooldownVic(Player player) {
        return AntiTrapper.cooldownvic.containsKey(player.getName())
                && AntiTrapper.cooldownvic.get(player.getName()) > System.currentTimeMillis();
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();
            Player victim = (Player) event.getEntity();

            ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
            AbilityService abilityService = HardPractice.getInstance().getService(AbilityService.class);

            Profile profile = profileService.getProfile(damager.getUniqueId());

            if (!isAbility(damager.getItemInHand())) {
                return;
            }
            if (isAbility(damager.getItemInHand())) {
                if (profile.getCooldown(AntiTrapper.class).onCooldown(damager)) {
                    damager.sendMessage(CC.translate("&fYou are on &b&lAntiTrapper &fcooldown for &c" + DurationFormatter.getRemaining(profile.getCooldown(AntiTrapper.class).getRemainingMillis(damager), true, true)));
                    damager.updateInventory();
                    return;
                }

                if (profile.getGlobalCooldown(GlobalCooldown.PARTNER_ITEM).onCooldown(damager)) {
                    damager.sendMessage(CC.translate("&fYou are on &b&lPartner Item &fcooldown for &b" + DurationFormatter.getRemaining(profile.getGlobalCooldown(GlobalCooldown.PARTNER_ITEM).getRemainingMillis(damager), true, true)));
                    damager.updateInventory();
                    return;
                }

                count = count + 1;
                if (count >= 3) {
                    count = 0;

                    // Apply cooldown on third hit
                    profile.getCooldown(AntiTrapper.class).applyCooldown(damager, 60 * 1000);
                    profile.getGlobalCooldown(GlobalCooldown.PARTNER_ITEM).applyCooldown(damager,  10 * 1000);

                    // Apply cooldown on victim to prevent interaction
                    AntiTrapper.cooldownvic.put(victim.getName(), System.currentTimeMillis() + (15 * 1000));

                    abilityService.sendPlayerMessage(damager, this.getAbility());
                    abilityService.sendTargetMessage(victim, damager, this.getAbility());

                    PlayerUtil.decrement(damager);
                }
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        Profile profile = HardPractice.getInstance().getService(ProfileService.class).getProfile(player.getUniqueId());
        if (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)) {
            if (!isAbility(player.getItemInHand())) {
                return;
            }
            if (isAbility(player.getItemInHand())) {
                if (this.hasCooldown(player)) {
                    player.sendMessage(CC.translate("&fYou are on cooldown for &c" + DurationFormatter.getRemaining(profile.getCooldown(AntiTrapper.class).getRemainingMillis(player), true)));
                    event.setCancelled(true);
                    player.updateInventory();
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (AntiTrapper.isOnCooldownVic(player)) {
            long millisLeft = AntiTrapper.cooldownvic.get(event.getPlayer().getName()) - System.currentTimeMillis();
            player.sendMessage(CC.translate("&7You cannot place blocks for another &c" + TimeUtil.formatLongMin(millisLeft) + " &7seconds"));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (AntiTrapper.isOnCooldownVic(player)) {
            long millisLeft = AntiTrapper.cooldownvic.get(event.getPlayer().getName()) - System.currentTimeMillis();
            event.setCancelled(true);
            player.sendMessage(CC.translate("&7You cannot break blocks for another &c" + TimeUtil.formatLongMin(millisLeft) + " &7seconds"));
        }
    }

    @EventHandler
    public void onFenceInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (block.getType().equals(Material.FENCE_GATE) || block.getType().equals(Material.CHEST)) {
                if (AntiTrapper.isOnCooldownVic(player)) {
                    long millisLeft = AntiTrapper.cooldownvic.get(event.getPlayer().getName()) - System.currentTimeMillis();
                    event.setCancelled(true);
                    player.sendMessage(CC.translate("&7You cannot interact with blocks for another &c" + TimeUtil.formatLongMin(millisLeft) + " &7seconds"));
                }
            }
        }
    }
}