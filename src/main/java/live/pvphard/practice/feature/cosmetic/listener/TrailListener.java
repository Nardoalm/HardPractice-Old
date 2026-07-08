package live.pvphard.practice.feature.cosmetic.listener;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.enums.ProfileState;
import live.pvphard.practice.feature.cosmetic.CosmeticService;
import live.pvphard.practice.feature.cosmetic.internal.repository.TrailRepository;
import live.pvphard.practice.feature.cosmetic.internal.repository.impl.trail.Trail;
import live.pvphard.practice.feature.cosmetic.model.CosmeticType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TrailListener implements Listener {
    public static final Map<Location, TrailBlockState> ACTIVE_TRAILS = new ConcurrentHashMap<>();

    public static class TrailBlockState {
        public final Material originalMaterial;
        public final byte originalData;
        public final Trail trail;
        public final long timestamp;
        public int ticks;

        public TrailBlockState(Material originalMaterial, byte originalData, Trail trail, long timestamp) {
            this.originalMaterial = originalMaterial;
            this.originalData = originalData;
            this.trail = trail;
            this.timestamp = timestamp;
            this.ticks = 0;
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        // Only trigger when moving across blocks
        if (from.getBlockX() == to.getBlockX() &&
                from.getBlockY() == to.getBlockY() &&
                from.getBlockZ() == to.getBlockZ()) {
            return;
        }

        Player player = event.getPlayer();
        if (!player.isOnGround()) {
            return;
        }

        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        if (profileService == null) return;

        Profile profile = profileService.getProfile(player.getUniqueId());
        if (profile == null || profile.getProfileData() == null) return;

        // Allow trails only in Lobby, Queue, or FFA states (not in active build matches where physical blocks matter)
        if (profile.getState() != ProfileState.LOBBY &&
                profile.getState() != ProfileState.QUEUE &&
                profile.getState() != ProfileState.FFA) {
            return;
        }

        String selectedTrailName = profile.getProfileData().getCosmeticData().getSelected(CosmeticType.TRAIL);
        if (selectedTrailName == null || selectedTrailName.equalsIgnoreCase("None")) {
            return;
        }

        CosmeticService cosmeticService = HardPractice.getInstance().getService(CosmeticService.class);
        if (cosmeticService == null) return;

        TrailRepository repository = cosmeticService.getRepository(CosmeticType.TRAIL, TrailRepository.class);
        if (repository == null) return;

        Trail trail = repository.getCosmetic(selectedTrailName);
        if (trail == null) return;

        // Block directly underneath player
        Block block = to.clone().subtract(0, 1, 0).getBlock();
        if (!isValidBlock(block)) return;

        Location loc = block.getLocation();

        // Avoid overriding if there's already a trail block here
        if (ACTIVE_TRAILS.containsKey(loc)) {
            return;
        }

        Material originalMaterial = block.getType();
        byte originalData = block.getData();

        TrailBlockState state = new TrailBlockState(originalMaterial, originalData, trail, System.currentTimeMillis());
        ACTIVE_TRAILS.put(loc, state);

        // Place the block
        block.setType(trail.getBlockMaterial());
        block.setData(trail.getBlockData(0));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (ACTIVE_TRAILS.containsKey(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (ACTIVE_TRAILS.containsKey(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    private boolean isValidBlock(Block block) {
        if (block == null) return false;
        Material type = block.getType();
        return type.isSolid() &&
                type != Material.AIR &&
                type != Material.WATER &&
                type != Material.STATIONARY_WATER &&
                type != Material.LAVA &&
                type != Material.STATIONARY_LAVA &&
                type != Material.CHEST &&
                type != Material.TRAPPED_CHEST &&
                type != Material.SIGN_POST &&
                type != Material.WALL_SIGN &&
                type != Material.WOOD_PLATE &&
                type != Material.STONE_PLATE &&
                type != Material.IRON_PLATE &&
                type != Material.GOLD_PLATE;
    }

    public static void purgeAllTrails() {
        if (ACTIVE_TRAILS.isEmpty()) return;
        for (Map.Entry<Location, TrailBlockState> entry : ACTIVE_TRAILS.entrySet()) {
            Block block = entry.getKey().getBlock();
            TrailBlockState state = entry.getValue();
            if (block.getType() == state.trail.getBlockMaterial()) {
                block.setType(state.originalMaterial);
                block.setData(state.originalData);
            }
        }
        ACTIVE_TRAILS.clear();
    }
}
