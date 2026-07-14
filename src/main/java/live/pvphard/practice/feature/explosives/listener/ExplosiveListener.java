package live.pvphard.practice.feature.explosives.listener;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.core.locale.LocaleService;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.enums.ProfileState;
import live.pvphard.practice.feature.cooldown.Cooldown;
import live.pvphard.practice.feature.cooldown.CooldownService;
import live.pvphard.practice.feature.cooldown.CooldownType;
import live.pvphard.practice.feature.explosives.ExplosiveService;
import live.pvphard.practice.feature.kit.setting.types.mechanic.KitSettingExplosiveImpl;
import live.pvphard.practice.feature.match.Match;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExplosiveListener implements Listener {
    private static final String PRACTICE_TNT_METADATA = "PRACTICE_TNT";

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null) return;

        Material itemType = item.getType();
        Action action = event.getAction();

        if (itemType == Material.FIREBALL && (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) {
            handleFireballUse(event);
        } else if (itemType == Material.TNT && action == Action.RIGHT_CLICK_BLOCK) {
            handleTntPlace(event);
        }
    }

    private void handleFireballUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        LocaleService localeService = HardPractice.getInstance().getService(LocaleService.class);

        Profile profile = HardPractice.getInstance().getService(ProfileService.class).getProfile(player.getUniqueId());
        if (profile == null || profile.getState() != ProfileState.PLAYING) return;

        Match match = profile.getMatch();
        if (match == null || !match.getKit().isSettingEnabled(KitSettingExplosiveImpl.class)) return;

        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.FIREBALL) return;

        event.setCancelled(true);

        CooldownType cooldownType = CooldownType.FIREBALL;
        CooldownService cooldownService = HardPractice.getInstance().getService(CooldownService.class);
        Optional<Cooldown> optionalCooldown = Optional.ofNullable(cooldownService.getCooldown(player.getUniqueId(), cooldownType));
        if (optionalCooldown.isPresent() && optionalCooldown.get().isActive()) {
            player.sendMessage(localeService.getString(GlobalMessagesLocaleImpl.COOLDOWN_FIREBALL_MUST_WAIT).replace("{time}", String.valueOf(optionalCooldown.get().remainingTimeInMinutes())));
            return;
        }

        Cooldown cooldown = optionalCooldown.orElseGet(() -> {
            Cooldown newCooldown = new Cooldown(cooldownType, () -> {});
            cooldownService.addCooldown(player.getUniqueId(), cooldownType, newCooldown);
            return newCooldown;
        });

        cooldown.resetCooldown();

        ExplosiveService explosiveService = HardPractice.getInstance().getService(ExplosiveService.class);
        Fireball fireball = player.launchProjectile(Fireball.class);
        fireball.setIsIncendiary(false);
        fireball.setYield(0.0F);
        fireball.setVelocity(player.getLocation().getDirection().normalize().multiply(explosiveService.getFireballThrowSpeed()));

        if (player.getGameMode() == GameMode.CREATIVE) return;

        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            player.setItemInHand(null);
        }
    }

    private void handleTntPlace(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;

        Profile profile = HardPractice.getInstance().getService(ProfileService.class).getProfile(player.getUniqueId());
        if (profile == null || profile.getState() != ProfileState.PLAYING) return;

        Match match = profile.getMatch();
        if (match == null || !match.getKit().isSettingEnabled(KitSettingExplosiveImpl.class)) return;

        event.setCancelled(true);

        if (player.getGameMode() != GameMode.CREATIVE) {
            ItemStack item = event.getItem();
            if (item.getAmount() > 1) {
                item.setAmount(item.getAmount() - 1);
            } else {
                player.getInventory().setItemInHand(null);
            }
            player.updateInventory();
        }

        Location tntLocation = clickedBlock.getRelative(event.getBlockFace()).getLocation().add(0.5, 0.0, 0.5);
        TNTPrimed tnt = (TNTPrimed) tntLocation.getWorld().spawnEntity(tntLocation, EntityType.PRIMED_TNT);

        tnt.setFuseTicks(HardPractice.getInstance().getService(ExplosiveService.class).getTntFuseTicks());
        tnt.setMetadata(PRACTICE_TNT_METADATA, new FixedMetadataValue(HardPractice.getInstance(), true));
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Fireball) || !(event.getEntity().getShooter() instanceof Player)) return;

        Fireball fireball = (Fireball) event.getEntity();
        Location explosionLocation = fireball.getLocation();
        Player shooter = (Player) fireball.getShooter();

        Profile profile = HardPractice.getInstance().getService(ProfileService.class).getProfile(shooter.getUniqueId());
        if (profile == null || profile.getMatch() == null) return;

        pushNearbyTnt(explosionLocation, fireball);
        handleFireballExplosion(explosionLocation, profile.getMatch());
        applyPlayerKnockback(fireball, explosionLocation);
    }

    @EventHandler
    public void onFireballDirectHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (event.getDamager() instanceof Fireball) {
            Player player = (Player) event.getEntity();
            Profile profile = HardPractice.getInstance().getService(ProfileService.class).getProfile(player.getUniqueId());
            if (profile != null && profile.getState() == ProfileState.PLAYING) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        event.setCancelled(true);

        if (event.getEntityType() != EntityType.PRIMED_TNT || !event.getEntity().hasMetadata(PRACTICE_TNT_METADATA)) {
            return;
        }

        TNTPrimed tnt = (TNTPrimed) event.getEntity();
        Location explosionLocation = tnt.getLocation();

        if (!(tnt.getSource() instanceof Player)) {
            tnt.remove();
            return;
        }

        Player placer = (Player) tnt.getSource();
        Profile profile = HardPractice.getInstance().getService(ProfileService.class).getProfile(placer.getUniqueId());
        if (profile == null || profile.getMatch() == null) {
            tnt.remove();
            return;
        }

        handleCustomTntExplosion(tnt, explosionLocation, profile.getMatch());
    }

    @EventHandler
    public void onExplosionDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        EntityDamageEvent.DamageCause cause = event.getCause();
        if (cause != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION && cause != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
            return;
        }

        Player player = (Player) event.getEntity();
        Profile profile = HardPractice.getInstance().getService(ProfileService.class).getProfile(player.getUniqueId());

        if (profile != null && profile.getState() == ProfileState.PLAYING) {
            event.setDamage(0.0);
        }
    }

    private void applyPlayerKnockback(Entity source, Location explosionLocation) {
        ExplosiveService explosiveService = HardPractice.getInstance().getService(ExplosiveService.class);
        double maxRange = explosiveService.getFireballExplosionRange();
        double maxHorizontal = explosiveService.getHorizontalFireballKnockback();
        double maxVertical = explosiveService.getVerticalFireballKnockback();

        source.getNearbyEntities(maxRange, maxRange, maxRange).forEach(entity -> {
            if (!(entity instanceof Player)) {
                return;
            }

            Player player = (Player) entity;
            Profile profile = HardPractice.getInstance().getService(ProfileService.class).getProfile(player.getUniqueId());
            if (profile == null || profile.getState() != ProfileState.PLAYING) {
                return;
            }

            double distance = player.getLocation().distance(explosionLocation);
            if (distance > maxRange) {
                return;
            }

            double falloff = (maxRange > 0) ? (1.0 - (distance / maxRange)) : 1.0;
            double scaledHorizontal = maxHorizontal * falloff;
            double scaledVertical = maxVertical * falloff;

            Vector knockbackDirection = player.getLocation().toVector().subtract(explosionLocation.toVector()).normalize();
            if (knockbackDirection.lengthSquared() == 0) {
                knockbackDirection.setY(1);
            }

            Vector finalVelocity = knockbackDirection.multiply(scaledHorizontal);
            finalVelocity.setY(scaledVertical);
            player.setVelocity(finalVelocity);
        });
    }

    private void pushNearbyTnt(Location explosionLocation, Entity source) {
        double maxPushStrength = 1;
        double pushRadius = 6.0;

        for (Entity entity : explosionLocation.getWorld().getNearbyEntities(explosionLocation, pushRadius, pushRadius, pushRadius)) {
            if (entity.getUniqueId().equals(source.getUniqueId())) {
                continue;
            }

            if (entity instanceof TNTPrimed && entity.hasMetadata(PRACTICE_TNT_METADATA)) {
                TNTPrimed tnt = (TNTPrimed) entity;
                double distance = tnt.getLocation().distance(explosionLocation);

                if (distance < 0.01) {
                    continue;
                }

                double pushForce = maxPushStrength * (1 - (distance / pushRadius));
                if (pushForce <= 0) {
                    continue;
                }

                Vector pushDirection = tnt.getLocation().toVector().subtract(explosionLocation.toVector()).normalize();
                Vector finalVelocity = tnt.getVelocity().add(pushDirection.multiply(pushForce));
                tnt.setVelocity(finalVelocity);
            }
        }
    }

    private void handleFireballExplosion(Location explosionLocation, Match match) {
        ExplosiveService explosiveService = HardPractice.getInstance().getService(ExplosiveService.class);
        double range = explosiveService.getFireballExplosionRange();

        List<Block> blocksToBreak = new ArrayList<>();
        int radius = (int) Math.ceil(range);

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Location blockLoc = explosionLocation.clone().add(x, y, z);
                    if (blockLoc.distance(explosionLocation) <= range) {
                        Block block = blockLoc.getBlock();
                        if (block.getType() == Material.WOOL && match.getPlacedBlocks().containsKey(block.getState())) {
                            blocksToBreak.add(block);
                        }
                    }
                }
            }
        }

        for (Block block : blocksToBreak) {
            match.addBlockToBrokenBlocksMap(block.getState(), block.getLocation());
            block.setType(Material.AIR);
        }

        explosionLocation.getWorld().createExplosion(explosionLocation, 0F, false);
    }

    private void handleCustomTntExplosion(TNTPrimed tnt, Location explosionLocation, Match match) {
        ExplosiveService explosiveService = HardPractice.getInstance().getService(ExplosiveService.class);
        double range = explosiveService.getTntExplosionRange();

        List<Block> blocksToBreak = new ArrayList<>();
        int radius = (int) Math.ceil(range);

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Location blockLoc = explosionLocation.clone().add(x, y, z);
                    if (blockLoc.distance(explosionLocation) <= range) {
                        Block block = blockLoc.getBlock();
                        Material type = block.getType();

                        if ((type == Material.WOOD || type == Material.ENDER_STONE || type == Material.WOOL)
                                && match.getPlacedBlocks().containsKey(block.getState())) {
                            blocksToBreak.add(block);
                        }
                    }
                }
            }
        }

        for (Block block : blocksToBreak) {
            match.addBlockToBrokenBlocksMap(block.getState(), block.getLocation());
            block.setType(Material.AIR);
        }

        explosionLocation.getWorld().createExplosion(explosionLocation, 0F, false);
        pushNearbyTnt(explosionLocation, tnt);
        applyPlayerKnockback(tnt, explosionLocation);
        tnt.remove();
    }
}