package live.pvphard.practice.feature.abilities.types;

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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

public class NinjaStar extends Ability {
    private final HardPractice plugin = HardPractice.getInstance();
    private final Map<UUID, UUID> TAGGED = Maps.newHashMap();

    public NinjaStar() {
        super("NINJA_STAR");
    }

    @EventHandler
    private void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();
            Player victim = (Player) event.getEntity();

            //AbilityCooldowns.addCooldown("TELEPORT", victim, 15);
            TAGGED.put(victim.getUniqueId(), damager.getUniqueId());
        }
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        if (!isAbility(event.getItem())) return;

        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            event.setCancelled(true);

            Player player = event.getPlayer();

            ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
            AbilityService abilityService = HardPractice.getInstance().getService(AbilityService.class);

            Profile profile = profileService.getProfile(player.getUniqueId());

            if (profile.getCooldown(NinjaStar.class).onCooldown(player)) {
                player.sendMessage(CC.translate("&fVocê está no cooldown de &b&lNinja Star &7por &4" + DurationFormatter.getRemaining(profile.getCooldown(NinjaStar.class).getRemainingMillis(player), true, true)));
                player.updateInventory();
                return;
            }

            if (profile.getGlobalCooldown(GlobalCooldown.PARTNER_ITEM).onCooldown(player)) {
                player.sendMessage(CC.translate("&fVocê está no cooldown de &b&lPartner Item &fpor &b" + DurationFormatter.getRemaining(profile.getGlobalCooldown(GlobalCooldown.PARTNER_ITEM).getRemainingMillis(player), true, true)));
                player.updateInventory();
                return;
            }

            //if (!AbilityCooldowns.isOnCooldown("TELEPORT", player)) return;

            PlayerUtil.decrement(player);

            Player target = Bukkit.getPlayer(TAGGED.get(player.getUniqueId()));
            if (target == null || !target.isOnline()) {
                player.sendMessage(CC.translate("&cNenhum alvo recente encontrado para o Ninja Star."));
                TAGGED.remove(player.getUniqueId());
                return;
            }

            profile.getCooldown(NinjaStar.class).applyCooldown(player, 60 * 1000);
            profile.getGlobalCooldown(GlobalCooldown.PARTNER_ITEM).applyCooldown(player, 10 * 1000);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (target == null) {
                        return;
                    }
                    player.teleport(target.getLocation());
                    player.sendMessage(CC.translate("&7Você foi teleportado com sucesso")); // you just got teleported back
                }
            }.runTaskLater(this.plugin, (5 * 10));

            abilityService.sendCooldownExpiredMessage(player, this.getName(), this.getAbility());
            abilityService.sendPlayerMessage(player, this.getAbility());
            abilityService.sendTargetMessage(target, player, this.getAbility());

            TAGGED.remove(player.getUniqueId());
        }
    }
}
