package live.pvphard.practice.feature.abilities.types;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.feature.abilities.Ability;
import live.pvphard.practice.feature.abilities.AbilityService;
import live.pvphard.practice.common.time.DurationFormatter;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.enums.GlobalCooldown;
import live.pvphard.practice.common.PlayerUtil;
import live.pvphard.practice.common.text.CC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Scrambler extends Ability {
    private final HardPractice plugin = HardPractice.getInstance();

    public Scrambler() {
        super("SCRAMBLER");
    }

    @EventHandler
    private void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();

            ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
            AbilityService abilityService = HardPractice.getInstance().getService(AbilityService.class);

            Profile profile = profileService.getProfile(damager.getUniqueId());

            if (!isAbility(damager.getItemInHand())) return;

            if (profile.getCooldown(Scrambler.class).onCooldown(damager)) {
                damager.sendMessage(CC.translate("&fVocê está no cooldown de &b&lScrambler &7por &4" + DurationFormatter.getRemaining(profile.getCooldown(Scrambler.class).getRemainingMillis(damager), true, true)));
                damager.updateInventory();
                return;
            }

            if (profile.getGlobalCooldown(GlobalCooldown.PARTNER_ITEM).onCooldown(damager)) {
                damager.sendMessage(CC.translate("&fVocê está no cooldown de &b&lPartner Item &fpor &b" + DurationFormatter.getRemaining(profile.getGlobalCooldown(GlobalCooldown.PARTNER_ITEM).getRemainingMillis(damager), true, true)));
                damager.updateInventory();
                return;
            }

            PlayerUtil.decrement(damager);

            Player victim = (Player) event.getEntity();

            profile.getCooldown(Scrambler.class).applyCooldown(damager, 60 * 1000);
            profile.getGlobalCooldown(GlobalCooldown.PARTNER_ITEM).applyCooldown(damager, 10 * 1000);

            this.random(victim);

            abilityService.sendCooldownExpiredMessage(damager, this.getName(), this.getAbility());
            abilityService.sendPlayerMessage(damager, this.getAbility());
            abilityService.sendTargetMessage(victim, damager, this.getAbility());
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
                    player.sendMessage(CC.translate("&fVocê está em cooldown por &4" + DurationFormatter.getRemaining(profile.getCooldown(Scrambler.class).getRemainingMillis(player), true)));
                    event.setCancelled(true);
                    player.updateInventory();
                }
            }
        }
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!isAbility(event.getItem())) return;
        AbilityService abilityService = HardPractice.getInstance().getService(AbilityService.class);

        if (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            event.setCancelled(true);

            if (this.hasCooldown(player)) {
                abilityService.sendCooldownMessage(player, this.getName(), this.getCooldown(player));
                player.updateInventory();
            }
        }
    }

    private void random(Player victim) {
        Inventory victimInventory = victim.getInventory();

        ItemStack slot1 = victimInventory.getItem(0);
        ItemStack slot2 = victimInventory.getItem(1);
        ItemStack slot3 = victimInventory.getItem(2);
        ItemStack slot4 = victimInventory.getItem(3);
        ItemStack slot5 = victimInventory.getItem(4);
        ItemStack slot6 = victimInventory.getItem(5);
        ItemStack slot7 = victimInventory.getItem(6);
        ItemStack slot8 = victimInventory.getItem(7);
        ItemStack slot9 = victimInventory.getItem(8);

        victimInventory.setItem(0, slot4);
        victimInventory.setItem(1, slot3);
        victimInventory.setItem(2, slot6);
        victimInventory.setItem(3, slot8);
        victimInventory.setItem(4, slot9);
        victimInventory.setItem(5, slot1);
        victimInventory.setItem(6, slot2);
        victimInventory.setItem(7, slot5);
        victimInventory.setItem(8, slot7);

        victim.updateInventory();
    }
}
