package live.pvphard.practice.feature.cosmetic.listener;

import com.knowplugins.ysubz.kaoscore.bukkit.manager.player.VanishManager;
import live.pvphard.practice.HardPractice;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.enums.ProfileState;
import live.pvphard.practice.feature.cosmetic.CosmeticService;
import live.pvphard.practice.feature.cosmetic.internal.repository.TrailRepository;
import live.pvphard.practice.feature.cosmetic.internal.repository.impl.trail.Trail;
import live.pvphard.practice.feature.cosmetic.model.CosmeticType;
import live.pvphard.practice.feature.match.internal.types.BedMatch;
import org.bukkit.Bukkit;
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
        Player player = event.getPlayer();

        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        if (profileService == null) return;

        Profile profile = profileService.getProfile(player.getUniqueId());
        if (profile == null || profile.getProfileData() == null) return;
        if (profile.getState() == ProfileState.SPECTATING || profile.getState() == ProfileState.EDITING) return;

        if (Bukkit.getPluginManager().isPluginEnabled("KaosCore")) {
            if (VanishManager.isVanished(player)) return;
        }

        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.getBlockX() == to.getBlockX() &&
                from.getBlockY() == to.getBlockY() &&
                from.getBlockZ() == to.getBlockZ()) {
            return;
        }

        if (!player.isOnGround()) {
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

        Block block = to.clone().subtract(0, 1, 0).getBlock();
        if (!isValidBlock(block)) return;

        Material originalMaterial = block.getType();

        if (originalMaterial == Material.PISTON_BASE || originalMaterial == Material.PISTON_STICKY_BASE) {
            return;
        }

        if (profile.getMatch() != null && profile.getMatch() instanceof BedMatch) {
            if (originalMaterial == Material.ENDER_STONE ||
                    originalMaterial == Material.WOOD ||
                    originalMaterial == Material.BED_BLOCK ||
                    originalMaterial == Material.BED) {
                return;
            }
        }

        Location loc = block.getLocation();

        if (ACTIVE_TRAILS.containsKey(loc)) {
            return;
        }

        byte originalData = block.getData();

        TrailBlockState state = new TrailBlockState(originalMaterial, originalData, trail, System.currentTimeMillis());
        ACTIVE_TRAILS.put(loc, state);

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
        if (!type.isSolid() ||
                type == Material.AIR ||
                type == Material.WATER ||
                type == Material.STATIONARY_WATER ||
                type == Material.LAVA ||
                type == Material.STATIONARY_LAVA ||
                type == Material.CHEST ||
                type == Material.TRAPPED_CHEST ||
                type == Material.SIGN_POST ||
                type == Material.WALL_SIGN ||
                type == Material.WOOD_PLATE ||
                type == Material.STONE_PLATE ||
                type == Material.IRON_PLATE ||
                type == Material.GOLD_PLATE) {
            return false;
        }

        Block blockAbove = block.getRelative(org.bukkit.block.BlockFace.UP);
        Material typeAbove = blockAbove.getType();
        if (typeAbove != Material.AIR) {
            if (typeAbove == Material.LONG_GRASS ||
                    typeAbove == Material.YELLOW_FLOWER ||
                    typeAbove == Material.RED_ROSE ||
                    typeAbove == Material.DOUBLE_PLANT ||
                    typeAbove == Material.SAPLING ||
                    typeAbove == Material.DEAD_BUSH ||
                    typeAbove == Material.SUGAR_CANE_BLOCK ||
                    typeAbove == Material.CACTUS ||
                    typeAbove == Material.BROWN_MUSHROOM ||
                    typeAbove == Material.RED_MUSHROOM ||
                    typeAbove == Material.CROPS ||
                    typeAbove == Material.CARROT ||
                    typeAbove == Material.POTATO ||
                    typeAbove == Material.PUMPKIN_STEM ||
                    typeAbove == Material.MELON_STEM) {
                return false;
            }
        }

        return true;
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