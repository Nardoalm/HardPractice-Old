package live.pvphard.practice.feature.abilities.types;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.feature.abilities.Ability;
import live.pvphard.practice.feature.abilities.AbilityService;
import live.pvphard.practice.common.time.DurationFormatter;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.enums.GlobalCooldown;
import live.pvphard.practice.common.text.CC;
import org.bukkit.Location;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class Switcher extends Ability {
    private final HardPractice plugin = HardPractice.getInstance();

    public Switcher() {
        super("SWITCHER");
    }

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            Player shooter = (Player) event.getEntity().getShooter();
            if (isAbility(shooter.getItemInHand())) {
                event.getEntity().setMetadata(this.getAbility(), new FixedMetadataValue(this.plugin, true));
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!isAbility(event.getItem())) return;

        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Player shooter = event.getPlayer();

            ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
            AbilityService abilityService = HardPractice.getInstance().getService(AbilityService.class);

            Profile profile = profileService.getProfile(shooter.getUniqueId());

            if (profile.getCooldown(Switcher.class).onCooldown(shooter)) {
                shooter.sendMessage(CC.translate("&fVocê está no cooldown de &b&lSwitcher &7por &4" + DurationFormatter.getRemaining(profile.getCooldown(Switcher.class).getRemainingMillis(shooter), true, true)));
                shooter.updateInventory();
                event.setCancelled(true);
                return;
            }

            if(profile.getGlobalCooldown(GlobalCooldown.PARTNER_ITEM).onCooldown(shooter)){
                shooter.sendMessage(CC.translate("&fVocê está no cooldown de &b&lPartner Item &fpor &b" + DurationFormatter.getRemaining(profile.getGlobalCooldown(GlobalCooldown.PARTNER_ITEM).getRemainingMillis(shooter), true, true)));
                shooter.updateInventory();
                event.setCancelled(true);
                return;
            }

            profile.getCooldown(Switcher.class).applyCooldown(shooter, 8 * 1000);
            profile.getGlobalCooldown(GlobalCooldown.PARTNER_ITEM).applyCooldown(shooter,  10 * 1000);

            abilityService.sendCooldownExpiredMessage(shooter, this.getName(), this.getAbility());
            abilityService.sendPlayerMessage(shooter, this.getAbility());
        }
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
                    player.sendMessage(CC.translate("&fVocê está em cooldown por &4" + DurationFormatter.getRemaining(profile.getCooldown(Switcher.class).getRemainingMillis(player), true)));
                    event.setCancelled(true);
                    player.updateInventory();
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();

            if (projectile instanceof Egg && projectile.hasMetadata(this.getAbility())) {
                Player player = (Player) event.getEntity();
                if (!(projectile.getShooter() instanceof Player)) {
                    return;
                }
                Player shooter = (Player) projectile.getShooter();

                Location playerLocation = player.getLocation().clone();
                Location shooterLocation = shooter.getLocation().clone();

                player.teleport(shooterLocation);
                shooter.teleport(playerLocation);

                HardPractice.getInstance().getService(AbilityService.class).sendTargetMessage(player, shooter, this.getAbility());
            }
            else if (projectile instanceof Snowball && projectile.hasMetadata(this.getAbility())) {
                Player player = (Player) event.getEntity();
                if (!(projectile.getShooter() instanceof Player)) {
                    return;
                }
                Player shooter = (Player) projectile.getShooter();

                Location playerLocation = player.getLocation().clone();
                Location shooterLocation = shooter.getLocation().clone();

                player.teleport(shooterLocation);
                shooter.teleport(playerLocation);

                HardPractice.getInstance().getService(AbilityService.class).sendTargetMessage(player, shooter, this.getAbility());
            }
        }
    }
}
