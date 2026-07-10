package live.pvphard.practice.feature.cosmetic;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.common.PlayerUtil;
import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.profile.ProfileService;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockBreakAnimation;
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
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CosmeticListener implements Listener {
    private static final byte[] RAINBOW_WOOL_COLORS = {14, 1, 4, 5, 13, 9, 3, 11, 10, 2, 6};
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

    // ignoreCancelled = true garante que não vamos colocar cosméticos onde outros plugins (ex: WorldGuard) bloqueiam.
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player == null || !PlayerUtil.inLobby(player)) {
            return;
        }

        if(!player.hasPermission("kaoscore.block")){
            player.sendMessage("§cYou do not have permission to use this. Purchase the §d§lPRO§c rank at §bstore.pvphard.live§c.");
            event.setCancelled(true);
            return;
        }

        Block block = event.getBlockPlaced();
        if (block == null || block.getType() != Material.WOOL) {
            return;
        }

        Location loc = block.getLocation();

        if (PLACED_BLOCKS.containsKey(loc)) {
            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);

        BlockState originalState = event.getBlockReplacedState();

        int entityId = loc.hashCode();
        SavedBlockState state = new SavedBlockState(originalState.getType(), originalState.getRawData(), entityId);
        PLACED_BLOCKS.put(loc, state);

        // Prevenção de glitch visual da mão do jogador sem risco de bugar o hotbar
        HardPractice.getInstance().getServer().getScheduler().runTask(HardPractice.getInstance(), player::updateInventory);

        HardPractice.getInstance().getServer().getScheduler().runTask(HardPractice.getInstance(), () -> {
            // Correção da Bomba de Memória: Se o jogador não estiver online, limpa o mapa e aborta.
            if (!player.isOnline() || !PLACED_BLOCKS.containsKey(loc)) {
                PLACED_BLOCKS.remove(loc);
                return;
            }

            byte initialColor = RAINBOW_WOOL_COLORS[0];

            loc.getBlock().setType(Material.WOOL);
            loc.getBlock().setData(initialColor);

            state.animationTask = new BukkitRunnable() {
                int ticks = 0;
                int stage = 0;

                @Override
                public void run() {
                    if (!PLACED_BLOCKS.containsKey(loc)) {
                        sendBreakPacket(loc, entityId, -1);
                        this.cancel();
                        return;
                    }

                    int colorIndex = (ticks / 2) % RAINBOW_WOOL_COLORS.length;
                    loc.getBlock().setData(RAINBOW_WOOL_COLORS[colorIndex]);

                    if (ticks % 2 == 0) {
                        if (stage > 9) {
                            removeCosmeticBlock(loc, true);
                            this.cancel();
                            return;
                        }
                        sendBreakPacket(loc, entityId, stage);
                        stage++;
                    }

                    ticks++;
                }
            }.runTaskTimer(HardPractice.getInstance(), 2L, 2L);
        });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (PLACED_BLOCKS.containsKey(event.getBlock().getLocation())) {
            event.setCancelled(true);
            return;
        }

        Player player = event.getPlayer();
        if (PlayerUtil.inLobby(player)) {
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