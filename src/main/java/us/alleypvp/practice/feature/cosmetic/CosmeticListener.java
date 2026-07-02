package us.alleypvp.practice.feature.cosmetic;

import us.alleypvp.practice.AlleyPractice;
import us.alleypvp.practice.common.PlayerUtil;
import us.alleypvp.practice.common.text.CC;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockBreakAnimation;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CosmeticListener implements Listener {
    private static final Map<UUID, Long> lastMoveTimes = new ConcurrentHashMap<>();
    private static final Map<Location, SavedBlockState> PLACED_BLOCKS = new ConcurrentHashMap<>();

    private static class SavedBlockState {
        Material originalMaterial;
        byte originalData;
        int entityId;
        BukkitTask animationTask;

        SavedBlockState(Material material, byte data, int entityId) {
            this.originalMaterial = material;
            this.originalData = data;
            this.entityId = entityId;
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() != event.getTo().getBlockX() ||
                event.getFrom().getBlockY() != event.getTo().getBlockY() ||
                event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
            lastMoveTimes.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        lastMoveTimes.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();

        if (!PlayerUtil.inLobby(player)) {
            return;
        }

        Block block = event.getBlockPlaced();

        if (block.getType() == Material.WOOL) {
            if (!player.hasPermission("kaoscore.pro")) {
                event.setCancelled(true);
                player.sendMessage(CC.translate("&cYou need &d&lPRO&c rank to use this. Purchase in &bstore.pvphard.live&c."));
                return;
            }

            Location loc = block.getLocation();
            if (PLACED_BLOCKS.containsKey(loc)) {
                event.setCancelled(true);
                return;
            }

            BlockState replaced = event.getBlockReplacedState();
            int entityId = loc.hashCode();
            SavedBlockState state = new SavedBlockState(replaced.getType(), replaced.getRawData(), entityId);
            PLACED_BLOCKS.put(loc, state);

            state.animationTask = new BukkitRunnable() {
                int stage = 0;

                @Override
                public void run() {
                    if (!PLACED_BLOCKS.containsKey(loc)) {
                        sendBreakPacket(loc, entityId, -1);
                        this.cancel();
                        return;
                    }

                    if (stage > 9) {
                        removeCosmeticBlock(loc, true);
                        this.cancel();
                        return;
                    }

                    sendBreakPacket(loc, entityId, stage);
                    stage++;
                }
            }.runTaskTimer(AlleyPractice.getInstance(), 12L, 12L);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (PLACED_BLOCKS.containsKey(event.getBlock().getLocation())) {
            event.setCancelled(true);
            return;
        }

        Player player = event.getPlayer();
        if (PlayerUtil.inLobby(player) && !player.hasPermission("kaoscore.admin")) {
            event.setCancelled(true);
        }
    }

    private static void sendBreakPacket(Location loc, int entityId, int stage) {
        BlockPosition pos = new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(entityId, pos, stage);

        for (Player p : loc.getWorld().getPlayers()) {
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
        }
    }

    public static void removeCosmeticBlock(Location loc, boolean playEffects) {
        SavedBlockState state = PLACED_BLOCKS.remove(loc);
        if (state == null) return;

        if (state.animationTask != null) {
            state.animationTask.cancel();
        }

        sendBreakPacket(loc, state.entityId, -1);

        Block block = loc.getBlock();
        if (playEffects) {
            loc.getWorld().playEffect(loc, Effect.STEP_SOUND, block.getTypeId());
            loc.getWorld().playSound(loc, Sound.DIG_WOOL, 1.0f, 1.0f);
        }

        block.setType(state.originalMaterial);
        block.setData(state.originalData);
    }

    public static void purgeAllBlocks() {
        if (PLACED_BLOCKS.isEmpty()) return;
        for (Location loc : new HashSet<>(PLACED_BLOCKS.keySet())) {
            removeCosmeticBlock(loc, false);
        }
        PLACED_BLOCKS.clear();
    }

    public static boolean isPlayerStill(Player player, long delayMillis) {
        return (System.currentTimeMillis() - lastMoveTimes.getOrDefault(player.getUniqueId(), 0L)) > delayMillis;
    }
}