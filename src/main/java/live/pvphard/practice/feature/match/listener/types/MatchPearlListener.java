package live.pvphard.practice.feature.match.listener.types;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.common.InventoryUtil;
import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.config.ConfigService;
import live.pvphard.practice.core.locale.LocaleService;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.enums.ProfileState;
import live.pvphard.practice.feature.cooldown.Cooldown;
import live.pvphard.practice.feature.cooldown.CooldownService;
import live.pvphard.practice.feature.cooldown.CooldownType;
import live.pvphard.practice.feature.kit.setting.types.mode.KitSettingLives;
import live.pvphard.practice.feature.kit.setting.types.mode.KitSettingRaiding;
import live.pvphard.practice.feature.match.MatchState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.material.Gate;
import org.bukkit.material.Stairs;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Optional;

public class MatchPearlListener implements Listener {

    @EventHandler
    public void onPearlLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof EnderPearl) || !(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        EnderPearl enderPearl = (EnderPearl) event.getEntity();
        Player player = (Player) enderPearl.getShooter();

        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());

        if (profile == null || !isValidGameState(player, event)) {
            return;
        }

        if (hasPearlCooldown(player, event)) {
            return;
        }

        if (!profile.getMatch().getKit().isSettingEnabled(KitSettingRaiding.class)) {
            applyCooldown(player);
            return;
        }

        Block playerBlock = getBlockAtPlayerFeet(player);
        Block playerBlockAbove = getBlockAtPlayerHead(player);

        if (shouldCancelPearl(player, playerBlock, playerBlockAbove, event)) {
            return;
        }

        setPearlMetadata(enderPearl, player, playerBlock, playerBlockAbove);
        applyCooldown(player);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            return;
        }

        Player player = event.getPlayer();
        Location destination = event.getTo();

        destination.setYaw(player.getLocation().getYaw());
        destination.setPitch(player.getLocation().getPitch());

        if (shouldCancelTeleport(destination, player, event)) {
            event.setCancelled(true);
            return;
        }

        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());

        if (profile != null && profile.getState() == ProfileState.PLAYING
                && profile.getMatch().getKit().isSettingEnabled(KitSettingRaiding.class)) {
            adjustTeleportLocation(destination);
        }

        event.setTo(destination);
    }

    private boolean isValidGameState(Player player, ProjectileLaunchEvent event) {
        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());

        if (profile.getState() != ProfileState.PLAYING) {
            return false;
        }

        if (profile.getMatch().getState() != MatchState.RUNNING) {
            cancelPearlAndRefund(player, event, "&cYou cannot use ender pearls right now.");
            return false;
        }

        if (profile.getMatch().getKit().isSettingEnabled(KitSettingLives.class)) {
            return false;
        }

        return true;
    }

    private boolean hasPearlCooldown(Player player, ProjectileLaunchEvent event) {
        CooldownService cooldownService = HardPractice.getInstance().getService(CooldownService.class);
        Optional<Cooldown> optionalCooldown = Optional.ofNullable(
                cooldownService.getCooldown(player.getUniqueId(), CooldownType.ENDER_PEARL)
        );

        if (optionalCooldown.isPresent() && optionalCooldown.get().isActive()) {
            cancelPearlAndRefund(player, event,
                    "&cYou must wait " + optionalCooldown.get().remainingTime() + " seconds before using another ender pearl.");
            return true;
        }

        return false;
    }

    private boolean shouldCancelPearl(Player player, Block playerBlock, Block playerBlockAbove, ProjectileLaunchEvent event) {
        String playerDirection = getPlayerDirection(player);

        if (isFenceGateRestricted(playerBlock, playerDirection)) {
            sendInvalidPearlFeedback(player, event);
            return true;
        }

        if (isChestRestricted(playerBlock, playerDirection)) {
            sendInvalidPearlFeedback(player, event);
            return true;
        }

        if (isSlabStairRestricted(playerBlock, playerBlockAbove, playerDirection)) {
            sendInvalidPearlFeedback(player, event);
            return true;
        }

        if (isStairFacingRestricted(playerBlock, playerDirection)) {
            sendInvalidPearlFeedback(player, event);
            return true;
        }

        if (isGeneralBlockRestricted(playerBlock, playerBlockAbove)) {
            sendInvalidPearlFeedback(player, event);
            return true;
        }

        return false;
    }

    private boolean shouldCancelTeleport(Location destination, Player player, PlayerTeleportEvent event) {
        Block destinationBlock = destination.getBlock();

        if (isForbiddenTeleportBlock(destinationBlock)) {
            sendInvalidPearlFeedback(player, event);
            return true;
        }

        if (isFenceGateTeleportRestricted(destinationBlock, player, event)) {
            return true;
        }

        if (isDisabledBlockType(destinationBlock, player, event)) {
            return true;
        }

        return false;
    }

    private boolean isFenceGateRestricted(Block block, String direction) {
        if (!getConfig().getBoolean("TalibanPearl.fencegate-in-half")) {
            return false;
        }

        if (block.getType() != Material.FENCE_GATE) {
            return false;
        }

        if (block.getRelative(BlockFace.UP).getType() == Material.FENCE_GATE ||
                !block.getRelative(BlockFace.UP).getType().isSolid() ||
                !block.getRelative(BlockFace.DOWN).getType().isSolid()) {
            return false;
        }

        Gate gate = (Gate) block.getState().getData();
        BlockFace gateDirection = gate.getFacing();
        BlockFace playerDirection = BlockFace.valueOf(direction);

        return !isDirectionCompatible(gateDirection, playerDirection);
    }

    private boolean isChestRestricted(Block block, String direction) {
        if (!getConfig().getBoolean("TalibanPearl.chest")) {
            return false;
        }

        if (!isChest(block)) {
            return false;
        }

        if (!block.getRelative(BlockFace.UP).getType().isSolid() ||
                block.getRelative(BlockFace.UP).getType() == Material.FENCE_GATE) {
            return false;
        }

        BlockFace directionFace = BlockFace.valueOf(direction);
        return block.getRelative(directionFace).getType().isSolid();
    }

    private boolean isSlabStairRestricted(Block block, Block blockAbove, String direction) {
        if (!getConfig().getBoolean("TalibanPearl.Block-if-block-in-front")) {
            return false;
        }

        boolean isSlab = getConfig().getBoolean("TalibanPearl.Slabs") && isSlab(block);
        boolean isStair = getConfig().getBoolean("TalibanPearl.Stairs") && isStair(block);

        if (!isSlab && !isStair) {
            return false;
        }

        BlockFace directionFace = BlockFace.valueOf(direction);
        return blockAbove.getRelative(directionFace).getType().isSolid();
    }

    private boolean isStairFacingRestricted(Block block, String direction) {
        if (!getConfig().getBoolean("TalibanPearl.Stairs-block-if-incorrect-facing") ||
                !getConfig().getBoolean("TalibanPearl.Stairs") ||
                !isStair(block)) {
            return false;
        }

        Stairs stairs = (Stairs) block.getState().getData();
        BlockFace stairDirection = stairs.getFacing();
        BlockFace playerDirection = BlockFace.valueOf(direction);

        return isOppositeDirection(stairDirection, playerDirection);
    }

    private boolean isGeneralBlockRestricted(Block block, Block blockAbove) {
        if (!getConfig().getBoolean("invalid-if-block-in-front")) {
            return false;
        }

        return block.getType().isSolid() &&
                blockAbove.getType().isSolid() &&
                !isStair(block) &&
                !isSlab(block) &&
                !isCobbleWall(block) &&
                !isEndPortal(block) &&
                !isBed(block);
    }

    private boolean isForbiddenTeleportBlock(Block block) {
        Material type = block.getType();
        return type == Material.IRON_FENCE ||
                type == Material.NETHER_FENCE ||
                type == Material.ANVIL ||
                type == Material.THIN_GLASS ||
                type == Material.IRON_DOOR ||
                type == Material.WOOD_DOOR ||
                type == Material.WOODEN_DOOR ||
                type == Material.FENCE ||
                type == Material.STAINED_GLASS_PANE;
    }

    private boolean isFenceGateTeleportRestricted(Block block, Player player, PlayerTeleportEvent event) {
        Block upper = block.getRelative(BlockFace.UP);
        Block lower = block.getRelative(BlockFace.DOWN);

        if (upper.getType() == Material.FENCE_GATE &&
                block.getType() == Material.FENCE_GATE &&
                !((Gate) upper.getState().getData()).isOpen() &&
                !((Gate) block.getState().getData()).isOpen() &&
                lower.getType().isSolid()) {
            sendInvalidPearlFeedback(player, event);
            return true;
        }

        if (upper.getType() != Material.FENCE_GATE &&
                block.getType() == Material.FENCE_GATE &&
                !((Gate) block.getState().getData()).isOpen() &&
                lower.getType() == Material.FENCE_GATE &&
                !((Gate) lower.getState().getData()).isOpen()) {
            sendInvalidPearlFeedback(player, event);
            return true;
        }

        return false;
    }

    private boolean isDisabledBlockType(Block block, Player player, PlayerTeleportEvent event) {
        if (!getConfig().getBoolean("TalibanPearl.chest") && isChest(block) &&
                block.getRelative(BlockFace.UP).getType().isSolid() &&
                block.getRelative(BlockFace.UP).getType() != Material.FENCE_GATE) {
            sendInvalidPearlFeedback(player, event);
            return true;
        }

        if (!getConfig().getBoolean("TalibanPearl.endportal") && isEndPortal(block) &&
                block.getRelative(BlockFace.UP).getType().isSolid()) {
            sendInvalidPearlFeedback(player, event);
            return true;
        }

        if (!getConfig().getBoolean("TalibanPearl.bed") && isBed(block) &&
                block.getRelative(BlockFace.UP).getType().isSolid()) {
            sendInvalidPearlFeedback(player, event);
            return true;
        }

        if (!getConfig().getBoolean("TalibanPearl.cobblewall") && isCobbleWall(block)) {
            sendInvalidPearlFeedback(player, event);
            return true;
        }

        return false;
    }

    private void setPearlMetadata(EnderPearl pearl, Player player, Block block, Block blockAbove) {
        String direction = getPlayerDirection(player);

        if (shouldSetTaliMetadata(block, blockAbove, direction)) {
            pearl.setMetadata("tali", new FixedMetadataValue(HardPractice.getInstance(), true));
        }

        if (shouldSetCpearlMetadata(block, direction)) {
            pearl.setMetadata("cpearl", new FixedMetadataValue(HardPractice.getInstance(), true));
        }

        if (shouldSetSlabStairMetadata(block)) {
            pearl.setMetadata("tali", new FixedMetadataValue(HardPractice.getInstance(), true));
        }
    }

    private boolean shouldSetTaliMetadata(Block block, Block blockAbove, String direction) {
        if (!getConfig().getBoolean("block-down-equals-air")) {
            return false;
        }

        BlockFace directionFace = BlockFace.valueOf(direction);
        Block blockInDirection = block.getRelative(directionFace);
        Block blockAboveInDirection = blockAbove.getRelative(directionFace);

        return block.getRelative(BlockFace.UP).getType() != Material.FENCE_GATE &&
                block.getRelative(BlockFace.UP).getType().isSolid() &&
                !block.getType().isSolid() &&
                block.getRelative(BlockFace.DOWN).getType().isSolid() &&
                !blockInDirection.getType().isSolid() &&
                !blockAboveInDirection.getType().isSolid();
    }

    private boolean shouldSetCpearlMetadata(Block block, String direction) {
        if (getConfig().getBoolean("TalibanPearl.cobblewall") &&
                isCobbleWall(block) &&
                block.getRelative(BlockFace.UP).getType().isSolid()) {
            return true;
        }

        if (getConfig().getBoolean("TalibanPearl.chest") && isChest(block) &&
                block.getRelative(BlockFace.UP).getType().isSolid() &&
                block.getRelative(BlockFace.UP).getType() != Material.FENCE_GATE) {
            BlockFace directionFace = BlockFace.valueOf(direction);
            return !block.getRelative(directionFace).getType().isSolid();
        }

        if (getConfig().getBoolean("TalibanPearl.chest") &&
                isChest(block.getRelative(BlockFace.DOWN)) &&
                !isChest(block.getRelative(BlockFace.UP)) &&
                block.getRelative(BlockFace.UP).getType().isSolid() &&
                block.getRelative(BlockFace.UP).getType() != Material.FENCE_GATE) {
            return true;
        }

        if (getConfig().getBoolean("TalibanPearl.trapdoor") &&
                block.getType() == Material.TRAP_DOOR &&
                block.getRelative(BlockFace.UP).getType().isSolid()) {
            return true;
        }

        if (getConfig().getBoolean("TalibanPearl.piston-base") &&
                isPistonBase(block.getRelative(BlockFace.DOWN)) &&
                block.getRelative(BlockFace.UP).getType().isSolid()) {
            return true;
        }

        if (getConfig().getBoolean("TalibanPearl.endportal") &&
                isEndPortal(block) &&
                block.getRelative(BlockFace.UP).getType().isSolid()) {
            return true;
        }

        if (getConfig().getBoolean("TalibanPearl.bed") &&
                isBed(block) &&
                block.getRelative(BlockFace.UP).getType().isSolid()) {
            return true;
        }

        return false;
    }

    private boolean shouldSetSlabStairMetadata(Block block) {
        return (getConfig().getBoolean("TalibanPearl.Slabs") && isSlab(block)) ||
                (getConfig().getBoolean("TalibanPearl.Stairs") && isStair(block));
    }

    private void adjustTeleportLocation(Location destination) {
        Block block = destination.getBlock();

        if (isStair(block) && block.getRelative(BlockFace.UP).getType().isSolid()) {
            destination.setY(destination.getY() - 0.5);
        } else if (isSlab(block) && block.getRelative(BlockFace.UP).getType().isSolid()) {
            destination.setY(destination.getY() - 0.5);
        }
    }

    private Block getBlockAtPlayerFeet(Player player) {
        String direction = getDirectionFromPlayer(player);

        Location playerLoc = player.getLocation();
        double offsetX = 0, offsetZ = 0;

        switch (direction) {
            case "N":
                offsetZ = -0.7;
                break;
            case "W":
                offsetX = -0.7;
                break;
            case "S":
                offsetZ = 0.7;
                break;
            case "E":
                offsetX = 0.7;
                break;
        }

        Location blockLoc = new Location(player.getWorld(),
                playerLoc.getX() + offsetX,
                playerLoc.getY(),
                playerLoc.getZ() + offsetZ);
        return blockLoc.getBlock();
    }

    private Block getBlockAtPlayerHead(Player player) {
        String direction = getDirectionFromPlayer(player);

        Location playerLoc = player.getLocation();
        double offsetX = 0, offsetZ = 0;

        switch (direction) {
            case "N":
                offsetZ = -0.7;
                break;
            case "W":
                offsetX = -0.7;
                break;
            case "S":
                offsetZ = 0.7;
                break;
            case "E":
                offsetX = 0.7;
                break;
        }

        Location blockLoc = new Location(player.getWorld(),
                playerLoc.getX() + offsetX,
                playerLoc.getY() + 1,
                playerLoc.getZ() + offsetZ);
        return blockLoc.getBlock();
    }

    private String getDirectionFromPlayer(Player player) {
        return getDirectionFromYaw(player.getLocation().getYaw());
    }

    private String getDirectionFromYaw(float yaw) {
        double rotation = (yaw - 90) % 360;
        if (rotation < 0) rotation += 360.0;

        if (rotation < 45 || rotation >= 315) return "W";
        else if (rotation < 135) return "N";
        else if (rotation < 225) return "E";
        else return "S";
    }

    private String getPlayerDirection(Player player) {
        String shortDirection = getDirectionFromPlayer(player);
        switch (shortDirection) {
            case "N":
                return "NORTH";
            case "W":
                return "WEST";
            case "S":
                return "SOUTH";
            case "E":
                return "EAST";
            default:
                return "NORTH";
        }
    }

    private boolean isEndPortal(Block block) {
        return block.getType() == Material.ENDER_PORTAL_FRAME;
    }

    private boolean isBed(Block block) {
        Material type = block.getType();
        return type == Material.BED_BLOCK || type == Material.BED;
    }

    private boolean isChest(Block block) {
        Material type = block.getType();
        return type == Material.ENDER_CHEST || type == Material.CHEST || type == Material.TRAPPED_CHEST;
    }

    private boolean isCobbleWall(Block block) {
        return block.getType() == Material.COBBLE_WALL;
    }

    private boolean isSlab(Block block) {
        Material type = block.getType();
        return type == Material.WOOD_STEP || type == Material.STEP;
    }

    private boolean isStair(Block block) {
        Material type = block.getType();
        return type == Material.ACACIA_STAIRS ||
                type == Material.BIRCH_WOOD_STAIRS ||
                type == Material.BRICK_STAIRS ||
                type == Material.COBBLESTONE_STAIRS ||
                type == Material.DARK_OAK_STAIRS ||
                type == Material.JUNGLE_WOOD_STAIRS ||
                type == Material.NETHER_BRICK_STAIRS ||
                type == Material.QUARTZ_STAIRS ||
                type == Material.SANDSTONE_STAIRS ||
                type == Material.SPRUCE_WOOD_STAIRS ||
                type == Material.SMOOTH_STAIRS ||
                type == Material.WOOD_STAIRS;
    }

    private boolean isPistonBase(Block block) {
        Material type = block.getType();
        return type == Material.PISTON_BASE || type == Material.PISTON_STICKY_BASE;
    }

    private boolean isDirectionCompatible(BlockFace gateDirection, BlockFace playerDirection) {
        return (gateDirection == BlockFace.NORTH && playerDirection == BlockFace.NORTH) ||
                (gateDirection == BlockFace.SOUTH && playerDirection == BlockFace.NORTH) ||
                (gateDirection == BlockFace.NORTH && playerDirection == BlockFace.SOUTH) ||
                (gateDirection == BlockFace.SOUTH && playerDirection == BlockFace.SOUTH) ||
                (gateDirection == BlockFace.WEST && playerDirection == BlockFace.WEST) ||
                (gateDirection == BlockFace.EAST && playerDirection == BlockFace.WEST) ||
                (gateDirection == BlockFace.WEST && playerDirection == BlockFace.EAST) ||
                (gateDirection == BlockFace.EAST && playerDirection == BlockFace.EAST);
    }

    private boolean isOppositeDirection(BlockFace stairDirection, BlockFace playerDirection) {
        return (stairDirection == BlockFace.WEST && playerDirection == BlockFace.EAST) ||
                (stairDirection == BlockFace.EAST && playerDirection == BlockFace.WEST) ||
                (stairDirection == BlockFace.NORTH && playerDirection == BlockFace.SOUTH) ||
                (stairDirection == BlockFace.SOUTH && playerDirection == BlockFace.NORTH);
    }

    private void cancelPearlAndRefund(Player player, ProjectileLaunchEvent event, String message) {
        event.setCancelled(true);
        InventoryUtil.giveItem(player, Material.ENDER_PEARL, 1);
        player.updateInventory();
        player.sendMessage(CC.translate(message));
    }

    private void sendInvalidPearlFeedback(Player player, Event event) {
        if (getConfig().getBoolean("Invalid-Pearl-Message", true)) {
            player.sendMessage(CC.translate(getConfig().getString("Message", "&cInvalid pearl.")));
        }

        if (getConfig().getBoolean("return-pearl", true)) {
            InventoryUtil.giveItem(player, Material.ENDER_PEARL, 1);
            player.updateInventory();

            CooldownService cooldownService = HardPractice.getInstance().getService(CooldownService.class);
            cooldownService.removeCooldown(player.getUniqueId(), CooldownType.ENDER_PEARL);
        }

        if (event instanceof Cancellable) {
            ((Cancellable) event).setCancelled(true);
        }
    }

    private void applyCooldown(Player player) {
        CooldownService cooldownService = HardPractice.getInstance().getService(CooldownService.class);
        Optional<Cooldown> optionalCooldown = Optional.ofNullable(
                cooldownService.getCooldown(player.getUniqueId(), CooldownType.ENDER_PEARL)
        );

        Cooldown cooldown = optionalCooldown.orElseGet(() -> {
            Cooldown newCooldown = new Cooldown(CooldownType.ENDER_PEARL,
                    () -> player.sendMessage(HardPractice.getInstance().getService(LocaleService.class).getString(GlobalMessagesLocaleImpl.COOLDOWN_CAN_NOW_USE_PEARLS_AGAIN)));
            cooldownService.addCooldown(player.getUniqueId(), CooldownType.ENDER_PEARL, newCooldown);
            return newCooldown;
        });

        cooldown.resetCooldown();
    }

    private ConfigurationSection getConfig() {
        return HardPractice.getInstance().getService(ConfigService.class).getPearlConfig();
    }
}