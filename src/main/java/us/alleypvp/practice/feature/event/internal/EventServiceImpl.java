package us.alleypvp.practice.feature.event.internal;

import us.alleypvp.practice.AlleyPractice;
import us.alleypvp.practice.adapter.knockback.KnockbackAdapter;
import us.alleypvp.practice.bootstrap.KaosContext;
import us.alleypvp.practice.bootstrap.annotation.Service;
import us.alleypvp.practice.common.InventoryUtil;
import us.alleypvp.practice.common.PlayerUtil;
import us.alleypvp.practice.common.constants.PluginConstant;
import us.alleypvp.practice.common.item.ItemBuilder;
import us.alleypvp.practice.common.logger.Logger;
import us.alleypvp.practice.common.serializer.Serializer;
import us.alleypvp.practice.common.text.CC;
import us.alleypvp.practice.common.text.ClickableUtil;
import us.alleypvp.practice.core.config.ConfigService;
import us.alleypvp.practice.core.profile.Profile;
import us.alleypvp.practice.core.profile.ProfileService;
import us.alleypvp.practice.core.profile.enums.ProfileState;
import us.alleypvp.practice.feature.arena.Arena;
import us.alleypvp.practice.feature.arena.ArenaService;
import us.alleypvp.practice.feature.arena.ArenaType;
import us.alleypvp.practice.feature.arena.internal.types.StandAloneArena;
import us.alleypvp.practice.feature.event.EventDefinition;
import us.alleypvp.practice.feature.event.EventPhase;
import us.alleypvp.practice.feature.event.EventPlayerState;
import us.alleypvp.practice.feature.event.EventService;
import us.alleypvp.practice.feature.event.EventType;
import us.alleypvp.practice.feature.event.model.ActiveEvent;
import us.alleypvp.practice.feature.event.model.EventParticipant;
import us.alleypvp.practice.feature.hotbar.HotbarService;
import us.alleypvp.practice.feature.kit.Kit;
import us.alleypvp.practice.feature.kit.KitService;
import us.alleypvp.practice.feature.kit.setting.types.mode.KitSettingSumo;
import us.alleypvp.practice.feature.spawn.SpawnService;
import us.alleypvp.practice.feature.visibility.VisibilityService;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service(provides = EventService.class, priority = 170)
public class EventServiceImpl implements EventService {
    private static final String HOST_PERMISSION = "kaos.command.donator.host";
    private static final String LEAVE_ITEM_NAME = "&cLeave Event";
    private static final String STOPLIGHT_STATE_KEY = "stoplight-go";
    private static final String STOPLIGHT_MOVERS_KEY = "stoplight-movers";
    private static final String TNT_TAGGED_KEY = "tnt-tagged";
    private static final String CORNERS_REMOVED_KEY = "corners-removed";
    private static final List<Byte> CORNER_COLORS = Arrays.asList((byte) 14, (byte) 11, (byte) 5, (byte) 4);

    private final AlleyPractice plugin;
    private final ConfigService configService;
    private final ProfileService profileService;
    private final ArenaService arenaService;
    private final KitService kitService;
    private final SpawnService spawnService;
    private final HotbarService hotbarService;
    private final VisibilityService visibilityService;

    private final Map<String, EventDefinition> eventDefinitions = new LinkedHashMap<>();

    private ActiveEvent activeEvent;

    public EventServiceImpl(AlleyPractice plugin, ConfigService configService, ProfileService profileService,
                            ArenaService arenaService, KitService kitService, SpawnService spawnService,
                            HotbarService hotbarService, VisibilityService visibilityService) {
        this.plugin = plugin;
        this.configService = configService;
        this.profileService = profileService;
        this.arenaService = arenaService;
        this.kitService = kitService;
        this.spawnService = spawnService;
        this.hotbarService = hotbarService;
        this.visibilityService = visibilityService;
    }

    @Override
    public void initialize(KaosContext context) {
        this.loadEventDefinitions();
    }

    @Override
    public void shutdown(KaosContext context) {
        this.stopActiveEvent("&cThe event has been terminated because the server is shutting down.");
    }

    @Override
    public Collection<EventDefinition> getEventDefinitions() {
        return Collections.unmodifiableCollection(this.eventDefinitions.values());
    }

    @Override
    public EventDefinition getEventDefinition(String key) {
        if (key == null) {
            return null;
        }
        return this.eventDefinitions.get(key.toLowerCase());
    }

    @Override
    public ActiveEvent getActiveEvent() {
        return this.activeEvent;
    }

    @Override
    public boolean hostEvent(Player host, String key) {
        if (host == null || !host.isOnline()) {
            return false;
        }

        if (!host.hasPermission(HOST_PERMISSION) && !host.hasPermission(this.getAdminPermission())) {
            host.sendMessage(CC.translate(this.plugin.getService(PluginConstant.class).getPermissionLackMessage()));
            return false;
        }

        if (this.activeEvent != null) {
            host.sendMessage(CC.translate("&cThere is already an active event."));
            return false;
        }

        Profile profile = this.profileService.getProfile(host.getUniqueId());
        if (profile == null || profile.getState() != ProfileState.LOBBY) {
            host.sendMessage(CC.translate("&cYou need to be in the lobby to host an event."));
            return false;
        }

        EventDefinition definition = this.getEventDefinition(key);
        if (definition == null || !definition.isEnabled()) {
            host.sendMessage(CC.translate("&cThis event does not exist or is disabled."));
            return false;
        }

        Arena baseArena = this.getArena(definition);
        if (baseArena == null) {
            host.sendMessage(CC.translate("&cThe arena configured for this event was not found."));
            return false;
        }

        String validation = this.validateDefinition(definition, baseArena);
        if (validation != null) {
            host.sendMessage(CC.translate(validation));
            return false;
        }

        Arena selectedArena = this.arenaService.selectArenaWithPotentialTemporaryCopy(baseArena);
        Kit selectedKit = this.resolveKit(definition);

        this.activeEvent = new ActiveEvent(definition, host.getUniqueId(), host.getName(), selectedArena, selectedKit);
        this.joinActiveEvent(host);

        this.broadcast("&b[Event] &f" + host.getName() + " &ahas hosted " + definition.getDisplayName() + "&a.");
        this.broadcast("&b[Event] &fUse &b/event join &for open the event menu to participate.");
        this.startCountdown(this.activeEvent);
        return true;
    }

    @Override
    public boolean joinActiveEvent(Player player) {
        if (player == null || !player.isOnline()) {
            return false;
        }

        ActiveEvent event = this.activeEvent;
        if (event == null) {
            player.sendMessage(CC.translate("&cThere is no active event at the moment."));
            return false;
        }

        if (event.getPhase() == EventPhase.RUNNING || event.getPhase() == EventPhase.ENDING) {
            player.sendMessage(CC.translate("&cThe event has already started."));
            return false;
        }

        if (event.isParticipant(player.getUniqueId())) {
            player.sendMessage(CC.translate("&cYou are already in the event."));
            return false;
        }

        Profile profile = this.profileService.getProfile(player.getUniqueId());
        if (profile == null || profile.getState() != ProfileState.LOBBY) {
            player.sendMessage(CC.translate("&cYou need to be in the lobby to join an event."));
            return false;
        }

        if (event.getParticipants().size() >= event.getDefinition().getMaximumPlayers()) {
            player.sendMessage(CC.translate("&cThe event has reached its player limit."));
            return false;
        }

        event.getParticipants().put(player.getUniqueId(), new EventParticipant(player.getUniqueId()));
        this.prepareProfileForEvent(player);
        this.sendPlayerToWaitingArea(player, event);
        this.visibilityService.updateVisibility(player);

        this.sendEventParticipantsMessage("&b[Event] &f" + player.getName() + " &ahas joined the event. &7("
                + event.getParticipants().size() + "/" + event.getDefinition().getMaximumPlayers() + ")");
        player.sendMessage(CC.translate("&aYou have joined " + event.getDefinition().getDisplayName() + "&a."));
        return true;
    }

    @Override
    public boolean leaveActiveEvent(Player player, boolean silent) {
        if (player == null) {
            return false;
        }

        ActiveEvent event = this.activeEvent;
        if (event == null || !event.isParticipant(player.getUniqueId())) {
            if (!silent) {
                player.sendMessage(CC.translate("&cYou are not in an event."));
            }
            return false;
        }

        if (event.getPhase() == EventPhase.RUNNING) {
            if (this.isBracketType(event.getDefinition().getType()) && event.isCurrentFighter(player.getUniqueId())) {
                this.handleFighterRemoval(player, silent ? "disconnected" : "left");
                return true;
            }

            this.handleParticipantRemoval(player, silent ? "disconnected" : "left the event", !silent);
            return true;
        }

        event.getParticipants().remove(player.getUniqueId());
        this.restorePlayerToLobby(player);

        if (!silent) {
            player.sendMessage(CC.translate("&cYou have left the event."));
            this.sendEventParticipantsMessage("&b[Event] &f" + player.getName() + " &chas left the event.");
        }

        if (event.getParticipants().isEmpty()) {
            this.stopActiveEvent("&cThe event has been terminated because all players left.");
        }

        return true;
    }

    @Override
    public void stopActiveEvent(String reason) {
        ActiveEvent event = this.activeEvent;
        if (event == null) {
            return;
        }

        this.cancelEventTasks(event);
        this.restoreModifiedBlocks(event);

        if (reason != null && !reason.isEmpty()) {
            this.broadcast("&b[Event] " + reason);
        }

        this.activeEvent = null;
        List<UUID> participants = new ArrayList<>(event.getParticipants().keySet());
        for (UUID uuid : participants) {
            Player participant = org.bukkit.Bukkit.getPlayer(uuid);
            if (participant != null) {
                this.restorePlayerToLobby(participant);
            }
        }

        if (event.getArena() instanceof StandAloneArena && ((StandAloneArena) event.getArena()).isTemporaryCopy()) {
            this.arenaService.deleteTemporaryArena((StandAloneArena) event.getArena());
        }
    }

    @Override
    public boolean canManageActiveEvent(Player player) {
        return player != null
                && this.activeEvent != null
                && (player.getUniqueId().equals(this.activeEvent.getHostUniqueId())
                || player.hasPermission(this.getAdminPermission()));
    }

    @Override
    public boolean isEventParticipant(UUID uuid) {
        return this.activeEvent != null && this.activeEvent.isParticipant(uuid);
    }

    @Override
    public boolean isCurrentFighter(UUID uuid) {
        return this.activeEvent != null && this.activeEvent.isCurrentFighter(uuid);
    }

    @Override
    public boolean isPreparingFighter(UUID uuid) {
        if (this.activeEvent == null || uuid == null) {
            return false;
        }
        EventParticipant participant = this.activeEvent.getParticipant(uuid);
        return participant != null && participant.getState() == EventPlayerState.PREPARING && this.isBracketType(this.activeEvent.getDefinition().getType());
    }

    @Override
    public Location getAssignedSpawn(UUID uuid) {
        if (this.activeEvent == null || uuid == null || this.activeEvent.getArena() == null) {
            return null;
        }

        if (uuid.equals(this.activeEvent.getCurrentFighterA())) {
            return this.activeEvent.getArena().getPos1();
        }
        if (uuid.equals(this.activeEvent.getCurrentFighterB())) {
            return this.activeEvent.getArena().getPos2();
        }
        return null;
    }

    @Override
    public boolean canPlayersFight(Player attacker, Player victim) {
        ActiveEvent event = this.activeEvent;
        if (attacker == null || victim == null || event == null || event.getPhase() != EventPhase.RUNNING) {
            return false;
        }

        EventParticipant attackerData = event.getParticipant(attacker.getUniqueId());
        EventParticipant victimData = event.getParticipant(victim.getUniqueId());
        if (attackerData == null || victimData == null) {
            return false;
        }

        switch (event.getDefinition().getType()) {
            case SUMO:
            case GULAG:
            case BRACKETS:
                return event.isCurrentFighter(attacker.getUniqueId())
                        && event.isCurrentFighter(victim.getUniqueId())
                        && attackerData.getState() == EventPlayerState.FIGHTING
                        && victimData.getState() == EventPlayerState.FIGHTING;
            case LMS:
            case KNOCKOUT:
            case OITC:
            case SKYWARS:
            case TNTTAG:
                return attackerData.getState() == EventPlayerState.FIGHTING
                        && victimData.getState() == EventPlayerState.FIGHTING;
            default:
                return false;
        }
    }

    @Override
    public void handleDisconnect(Player player) {
        if (player != null && this.isEventParticipant(player.getUniqueId())) {
            this.leaveActiveEvent(player, true);
        }
    }

    @Override
    public void eliminate(Player player, String reason) {
        if (player == null || this.activeEvent == null || !this.activeEvent.isParticipant(player.getUniqueId())) {
            return;
        }

        EventType type = this.activeEvent.getDefinition().getType();
        if (this.isBracketType(type)) {
            if (this.activeEvent.isCurrentFighter(player.getUniqueId())) {
                this.handleFighterRemoval(player, reason);
            } else {
                this.handleParticipantRemoval(player, reason, true);
            }
            return;
        }

        if (type == EventType.OITC) {
            this.handleOitcDeath(player, null, reason);
            return;
        }

        if (type == EventType.DROPPER) {
            this.scheduleDropperRespawn(player);
            return;
        }

        this.handleParticipantRemoval(player, reason, true);
    }

    @Override
    public void handleInteract(PlayerInteractEvent event) {
        if (event.getPlayer() == null || !this.isEventParticipant(event.getPlayer().getUniqueId())) {
            return;
        }

        ItemStack itemStack = event.getItem();
        if (itemStack == null || itemStack.getType() != Material.REDSTONE || !itemStack.hasItemMeta()) {
            return;
        }

        if (CC.translate(LEAVE_ITEM_NAME).equals(itemStack.getItemMeta().getDisplayName())) {
            event.setCancelled(true);
            this.leaveActiveEvent(event.getPlayer(), false);
        }
    }

    @Override
    public void handleMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ActiveEvent active = this.activeEvent;
        if (active == null || !active.isParticipant(player.getUniqueId()) || event.getTo() == null) {
            return;
        }

        if (this.isPreparingFighter(player.getUniqueId())) {
            Location assignedSpawn = this.getAssignedSpawn(player.getUniqueId());
            if (assignedSpawn != null) {
                double deltaX = Math.abs(event.getTo().getX() - assignedSpawn.getX());
                double deltaZ = Math.abs(event.getTo().getZ() - assignedSpawn.getZ());
                if (deltaX > 0.2D || deltaZ > 0.2D) {
                    player.teleport(new Location(
                            assignedSpawn.getWorld(),
                            assignedSpawn.getX(),
                            player.getLocation().getY(),
                            assignedSpawn.getZ(),
                            player.getLocation().getYaw(),
                            player.getLocation().getPitch()
                    ));
                    return;
                }
            }
        }

        EventParticipant participant = active.getParticipant(player.getUniqueId());
        if (participant == null) {
            return;
        }

        participant.setLastKnownLocation(event.getTo().clone());

        if (participant.getState() == EventPlayerState.WAITING) {
            if (this.isOnLiquid(player) || this.isBelowArena(player, active.getArena())) {
                Location waitingLoc = this.getWaitingLocation(active);
                if (waitingLoc != null) {
                    player.teleport(waitingLoc);
                }
                return;
            }
        }

        switch (active.getDefinition().getType()) {
            case SUMO:
            case KNOCKOUT:
            case SPLEEF:
            case FOUR_CORNERS:
                if (participant.getState() == EventPlayerState.FIGHTING
                        && (this.isOnLiquid(player) || this.isBelowArena(player, active.getArena()))) {
                    this.eliminate(player, "fell from the arena");
                }
                break;
            case LMS:
            case SKYWARS:
                if (participant.getState() == EventPlayerState.FIGHTING && this.isBelowArena(player, active.getArena())) {
                    this.eliminate(player, "fell into the void");
                }
                break;
            case OITC:
                if (participant.getState() == EventPlayerState.FIGHTING && this.isBelowArena(player, active.getArena())) {
                    this.handleOitcDeath(player, null, "fell into the void");
                }
                break;
            case PARKOUR:
                this.handleParkourMove(player, participant);
                break;
            case DROPPER:
                this.handleDropperMove(player, participant);
                break;
            case THIMBLE:
                this.handleThimbleMove(player, participant);
                break;
            case STOPLIGHT:
                this.handleStoplightMove(event, participant);
                break;
            default:
                break;
        }
    }

    @Override
    public void handleDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        ActiveEvent active = this.activeEvent;
        if (active == null || !active.isParticipant(player.getUniqueId())) {
            return;
        }

        if (event instanceof EntityDamageByEntityEvent) {
            return;
        }

        if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
            event.setCancelled(true);
            switch (active.getDefinition().getType()) {
                case OITC:
                    this.handleOitcDeath(player, null, "fell into the void");
                    break;
                case DROPPER:
                    this.scheduleDropperRespawn(player);
                    break;
                default:
                    this.eliminate(player, "fell into the void");
                    break;
            }
            return;
        }

        switch (active.getDefinition().getType()) {
            case BRACKETS:
            case GULAG:
            case LMS:
            case SKYWARS:
            case OITC:
                if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    event.setCancelled(true);
                } else if (event.getCause() != EntityDamageEvent.DamageCause.CUSTOM) {
                    event.setCancelled(true);
                }
                break;
            case DROPPER:
                if (event.getCause() == EntityDamageEvent.DamageCause.FIRE
                        || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK
                        || event.getCause() == EntityDamageEvent.DamageCause.LAVA) {
                    event.setCancelled(true);
                }
                break;
            case THIMBLE:
                if (event.getCause() != EntityDamageEvent.DamageCause.FALL) {
                    event.setCancelled(true);
                }
                break;
            default:
                event.setCancelled(true);
                break;
        }
    }

    @Override
    public void handleDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player victim = (Player) event.getEntity();
        Player attacker = this.resolveDamager(event);
        ActiveEvent active = this.activeEvent;
        if (active == null || !active.isParticipant(victim.getUniqueId())) {
            return;
        }

        if (attacker == null || !active.isParticipant(attacker.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        EventType type = active.getDefinition().getType();
        if (!this.canPlayersFight(attacker, victim)) {
            event.setCancelled(true);
            return;
        }

        switch (type) {
            case SUMO:
            case KNOCKOUT:
                event.setDamage(0.0D);
                break;
            case GULAG:
                event.setDamage(event.getDamager() instanceof Projectile ? 7.0D : 1.5D);
                break;
            case OITC:
                event.setDamage(event.getDamager() instanceof Projectile ? victim.getMaxHealth() : 2.0D);
                break;
            case TNTTAG:
                event.setDamage(0.0D);
                this.transferTag(attacker, victim);
                break;
            default:
                break;
        }
    }

    @Override
    public void handleDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        ActiveEvent active = this.activeEvent;
        if (active == null || !active.isParticipant(player.getUniqueId())) {
            return;
        }

        event.setDeathMessage(null);
        event.getDrops().clear();
        event.setDroppedExp(0);

        Player killer = player.getKiller();
        switch (active.getDefinition().getType()) {
            case SUMO:
            case BRACKETS:
            case GULAG:
                this.scheduleRespawn(player, () -> this.handleFighterRemoval(player, killer != null ? "was defeated by " + killer.getName() : "was defeated"));
                break;
            case LMS:
            case KNOCKOUT:
            case SKYWARS:
            case SPLEEF:
            case FOUR_CORNERS:
            case STOPLIGHT:
            case TNTTAG:
            case THIMBLE:
                this.scheduleRespawn(player, () -> this.handleParticipantRemoval(player, killer != null ? "was eliminated by " + killer.getName() : "was eliminated", true));
                break;
            case OITC:
                this.scheduleRespawn(player, () -> this.handleOitcDeath(player, killer, "died"));
                break;
            case DROPPER:
                this.scheduleRespawn(player, () -> this.scheduleDropperRespawn(player));
                break;
            case PARKOUR:
                this.scheduleRespawn(player, () -> this.teleportParkourPlayer(player, active.getParticipant(player.getUniqueId())));
                break;
            default:
                break;
        }
    }

    @Override
    public void handleRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        ActiveEvent active = this.activeEvent;
        if (active == null || !active.isParticipant(player.getUniqueId())) {
            return;
        }

        Location respawnLocation = this.getWaitingLocation(active);
        if (respawnLocation == null && active.getArena() != null) {
            respawnLocation = active.getArena().getCenter();
        }
        if (respawnLocation == null) {
            respawnLocation = this.spawnService.getLocation();
        }
        if (respawnLocation != null) {
            event.setRespawnLocation(respawnLocation);
        }
    }

    @Override
    public void handleInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        ActiveEvent active = this.activeEvent;
        if (active == null || !active.isParticipant(player.getUniqueId())) {
            return;
        }

        EventParticipant participant = active.getParticipant(player.getUniqueId());
        if (participant == null || active.getPhase() != EventPhase.RUNNING) {
            event.setCancelled(true);
            return;
        }

        switch (active.getDefinition().getType()) {
            case BRACKETS:
            case GULAG:
            case LMS:
            case OITC:
            case SKYWARS:
                event.setCancelled(participant.getState() != EventPlayerState.FIGHTING);
                break;
            default:
                event.setCancelled(true);
                break;
        }
    }

    @Override
    public void handleBlockBreak(BlockBreakEvent event) {
        ActiveEvent active = this.activeEvent;
        if (active == null || !active.isParticipant(event.getPlayer().getUniqueId())) {
            return;
        }

        EventParticipant participant = active.getParticipant(event.getPlayer().getUniqueId());
        if (participant == null || participant.getState() != EventPlayerState.FIGHTING) {
            event.setCancelled(true);
            return;
        }

        switch (active.getDefinition().getType()) {
            case SPLEEF:
                if (event.getBlock().getType() == Material.SNOW || event.getBlock().getType() == Material.SNOW_BLOCK) {
                    active.getModifiedBlocks().add(event.getBlock().getLocation());
                    return;
                }
                event.setCancelled(true);
                break;
            case SKYWARS:
                break;
            default:
                event.setCancelled(true);
                break;
        }
    }

    @Override
    public void handleBlockPlace(BlockPlaceEvent event) {
        ActiveEvent active = this.activeEvent;
        if (active == null || !active.isParticipant(event.getPlayer().getUniqueId())) {
            return;
        }

        EventParticipant participant = active.getParticipant(event.getPlayer().getUniqueId());
        if (participant == null || participant.getState() != EventPlayerState.FIGHTING) {
            event.setCancelled(true);
            return;
        }

        if (active.getDefinition().getType() != EventType.SKYWARS) {
            event.setCancelled(true);
        }
    }

    @Override
    public boolean isDropLocked(Player player) {
        return this.isInventoryActionLocked(player, false);
    }

    @Override
    public boolean isPickupLocked(Player player) {
        return this.isInventoryActionLocked(player, true);
    }

    @Override
    public boolean isItemDamageLocked(Player player) {
        ActiveEvent active = this.activeEvent;
        if (player == null || active == null || !active.isParticipant(player.getUniqueId())) {
            return false;
        }

        switch (active.getDefinition().getType()) {
            case BRACKETS:
            case GULAG:
            case LMS:
            case OITC:
            case SKYWARS:
            case SPLEEF:
                return false;
            default:
                return true;
        }
    }

    private void loadEventDefinitions() {
        this.eventDefinitions.clear();

        FileConfiguration eventsConfig = this.configService.getEventsConfig();
        if (eventsConfig == null) {
            return;
        }

        ConfigurationSection eventsSection = eventsConfig.getConfigurationSection("events");
        if (eventsSection == null) {
            return;
        }

        Set<String> fixedKeys = new HashSet<>(Arrays.asList(
                "enabled", "type", "display-name", "material", "durability",
                "arena", "kit", "minimum-players", "maximum-players",
                "countdown-seconds", "description", "spawn-locations",
                "locations", "settings"
        ));

        for (String key : eventsSection.getKeys(false)) {
            ConfigurationSection section = eventsSection.getConfigurationSection(key);
            if (section == null) {
                continue;
            }

            Material material = Material.matchMaterial(section.getString("material", "STONE").toUpperCase());
            if (material == null) {
                material = Material.STONE;
            }

            EventType type;
            try {
                type = EventType.valueOf(section.getString("type", "SUMO").toUpperCase());
            } catch (IllegalArgumentException exception) {
                Logger.error("Invalid event type in events.yml for '" + key + "'.");
                continue;
            }

            List<Location> spawnLocations = section.getStringList("spawn-locations").stream()
                    .map(Serializer::deserializeLocation)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            Map<String, Location> locations = new HashMap<>();
            ConfigurationSection locationsSection = section.getConfigurationSection("locations");
            if (locationsSection != null) {
                for (String locationKey : locationsSection.getKeys(false)) {
                    Location location = Serializer.deserializeLocation(locationsSection.getString(locationKey));
                    if (location != null) {
                        locations.put(locationKey.toLowerCase(), location);
                    }
                }
            }

            Map<String, Object> settings = new HashMap<>();
            ConfigurationSection settingsSection = section.getConfigurationSection("settings");
            if (settingsSection != null) {
                settings.putAll(settingsSection.getValues(false));
            }

            for (String rootKey : section.getKeys(false)) {
                if (!fixedKeys.contains(rootKey)) {
                    settings.put(rootKey, section.get(rootKey));
                }
            }

            int minimumPlayers = Math.max(2, section.getInt("minimum-players", 2));
            int maximumPlayers = Math.max(minimumPlayers, section.getInt("maximum-players", minimumPlayers));

            EventDefinition definition = new EventDefinition(
                    key.toLowerCase(),
                    section.getBoolean("enabled", true),
                    type,
                    section.getString("display-name", "&b&l" + key),
                    material,
                    section.getInt("durability", 0),
                    section.getString("arena", ""),
                    section.getString("kit", ""),
                    minimumPlayers,
                    maximumPlayers,
                    Math.max(10, section.getInt("countdown-seconds", 30)),
                    section.getStringList("description"),
                    spawnLocations,
                    locations,
                    settings
            );

            this.eventDefinitions.put(definition.getKey(), definition);
        }
    }

    private void startCountdown(ActiveEvent event) {
        event.setPhase(EventPhase.STARTING);
        event.setCountdownTask(new BukkitRunnable() {
            @Override
            public void run() {
                if (activeEvent != event) {
                    this.cancel();
                    return;
                }

                if (event.getCountdownRemaining() <= 0) {
                    this.cancel();
                    event.setCountdownTask(null);

                    if (event.getParticipants().size() < event.getDefinition().getMinimumPlayers()) {
                        stopActiveEvent("&cThe event has been cancelled due to a lack of players.");
                        return;
                    }

                    startEvent(event);
                    return;
                }

                if (shouldAnnounce(event.getCountdownRemaining())) {
                    announceCountdown(event);
                }

                event.setCountdownRemaining(event.getCountdownRemaining() - 1);
            }
        }.runTaskTimer(this.plugin, 20L, 20L));
    }

    private boolean shouldAnnounce(int remaining) {
        return remaining == 60 || remaining == 30 || remaining == 15
                || remaining == 10 || remaining == 5 || remaining == 4
                || remaining == 3 || remaining == 2 || remaining == 1;
    }

    private void announceCountdown(ActiveEvent event) {
        String message = "&b[Event] &f" + event.getDefinition().getDisplayName()
                + " &fstarts in &b" + event.getCountdownRemaining() + "s"
                + " &7(" + event.getParticipants().size() + "/" + event.getDefinition().getMaximumPlayers() + ")";

        TextComponent component = ClickableUtil.createComponent("&a&l[Click to join]", "/event join", "&7Click to join the event.");

        for (Player onlinePlayer : org.bukkit.Bukkit.getOnlinePlayers()) {
            onlinePlayer.sendMessage(CC.translate(message));
            if (!event.isParticipant(onlinePlayer.getUniqueId())) {
                onlinePlayer.spigot().sendMessage(component);
            }
        }
    }

    private void startEvent(ActiveEvent event) {
        event.setPhase(EventPhase.RUNNING);
        this.sendEventParticipantsMessage("&b[Event] &aThe event has started.");

        switch (event.getDefinition().getType()) {
            case SUMO:
            case GULAG:
            case BRACKETS:
                this.pickNextFight();
                break;
            case LMS:
            case KNOCKOUT:
            case SKYWARS:
            case SPLEEF:
                this.startEliminationEvent(event);
                break;
            case OITC:
                this.startOitcEvent(event);
                break;
            case PARKOUR:
                this.startParkourEvent(event);
                break;
            case DROPPER:
                this.startDropperEvent(event);
                break;
            case THIMBLE:
                this.startThimbleEvent(event);
                break;
            case STOPLIGHT:
                this.startStoplightEvent(event);
                break;
            case FOUR_CORNERS:
                this.startFourCornersEvent(event);
                break;
            case TNTTAG:
                this.startTntTagEvent(event);
                break;
            default:
                this.stopActiveEvent("&cThis event type is not available yet.");
                break;
        }
    }

    private void pickNextFight() {
        ActiveEvent event = this.activeEvent;
        if (event == null) {
            return;
        }

        List<UUID> waiting = event.getWaitingParticipants();
        if (waiting.size() == 1 && event.getParticipants().size() == 1) {
            Player winner = org.bukkit.Bukkit.getPlayer(waiting.get(0));
            this.finishWithWinner(winner);
            return;
        }

        if (waiting.size() < 2) {
            return;
        }

        Collections.shuffle(waiting);
        UUID fighterA = waiting.get(0);
        UUID fighterB = waiting.get(1);

        EventParticipant participantA = event.getParticipant(fighterA);
        EventParticipant participantB = event.getParticipant(fighterB);
        if (participantA == null || participantB == null) {
            return;
        }

        participantA.setState(EventPlayerState.PREPARING);
        participantB.setState(EventPlayerState.PREPARING);

        event.setCurrentFighterA(fighterA);
        event.setCurrentFighterB(fighterB);
        event.setCurrentRound(event.getCurrentRound() + 1);

        Player playerA = org.bukkit.Bukkit.getPlayer(fighterA);
        Player playerB = org.bukkit.Bukkit.getPlayer(fighterB);
        if (playerA == null || playerB == null) {
            participantA.setState(EventPlayerState.WAITING);
            participantB.setState(EventPlayerState.WAITING);
            event.clearCurrentFight();
            org.bukkit.Bukkit.getScheduler().runTaskLater(this.plugin, this::pickNextFight, 20L);
            return;
        }

        this.prepareRoundPlayer(playerA, event.getArena().getPos1(), event);
        this.prepareRoundPlayer(playerB, event.getArena().getPos2(), event);

        this.sendEventParticipantsMessage("&b[Event] &fRound &b#" + event.getCurrentRound()
                + " &fbetween &a" + playerA.getName() + " &fand &c" + playerB.getName() + "&f.");

        event.setPreparationTask(new BukkitRunnable() {
            private int countdown = 3;

            @Override
            public void run() {
                if (activeEvent != event) {
                    this.cancel();
                    return;
                }

                if (!playerA.isOnline() || !playerB.isOnline()) {
                    this.cancel();
                    event.setPreparationTask(null);
                    return;
                }

                if (this.countdown <= 0) {
                    participantA.setState(EventPlayerState.FIGHTING);
                    participantB.setState(EventPlayerState.FIGHTING);

                    playerA.sendMessage(CC.translate("&aFight!"));
                    playerB.sendMessage(CC.translate("&aFight!"));
                    event.setPreparationTask(null);
                    startRoundTimer(event, playerA, playerB);
                    if (event.getDefinition().getType() == EventType.SUMO) {
                        startWaterCheck(event);
                    }
                    this.cancel();
                    return;
                }

                sendEventParticipantsMessage("&b[Event] &fRound starts in &b" + this.countdown + "&f.");
                this.countdown--;
            }
        }.runTaskTimer(this.plugin, 0L, 20L));
    }

    private void startRoundTimer(ActiveEvent event, Player playerA, Player playerB) {
        this.cancelSecondaryTask(event);

        event.setSecondaryTask(new BukkitRunnable() {
            private int time = event.getDefinition().getIntSetting("round-seconds",
                    event.getDefinition().getType() == EventType.SUMO ? 90 : 180);

            @Override
            public void run() {
                if (activeEvent != event || event.getCurrentFighterA() == null || event.getCurrentFighterB() == null) {
                    this.cancel();
                    event.setSecondaryTask(null);
                    return;
                }

                if (this.time <= 0) {
                    List<Player> players = Arrays.asList(playerA, playerB);
                    Player winner = players.get(ThreadLocalRandom.current().nextInt(players.size()));
                    players.stream()
                            .filter(current -> !current.equals(winner))
                            .forEach(current -> eliminate(current, "lost on time"));
                    this.cancel();
                    event.setSecondaryTask(null);
                    return;
                }

                if (Arrays.asList(30, 25, 20, 15, 10, 5, 4, 3, 2, 1).contains(this.time)) {
                    sendEventParticipantsMessage("&b[Event] &fRound ends in &b" + this.time + "s&f.");
                }

                this.time--;
            }
        }.runTaskTimer(this.plugin, 20L, 20L));
    }

    private void startWaterCheck(ActiveEvent event) {
        this.cancelWaterCheck(event);

        event.setWaterCheckTask(new BukkitRunnable() {
            @Override
            public void run() {
                if (activeEvent != event || event.getCurrentFighterA() == null || event.getCurrentFighterB() == null) {
                    this.cancel();
                    event.setWaterCheckTask(null);
                    return;
                }

                Player playerA = org.bukkit.Bukkit.getPlayer(event.getCurrentFighterA());
                Player playerB = org.bukkit.Bukkit.getPlayer(event.getCurrentFighterB());
                if (playerA == null || playerB == null) {
                    this.cancel();
                    event.setWaterCheckTask(null);
                    return;
                }

                if (isOnLiquid(playerA) || isBelowArena(playerA, event.getArena())) {
                    eliminate(playerA, "fell into the water");
                    this.cancel();
                    event.setWaterCheckTask(null);
                    return;
                }

                if (isOnLiquid(playerB) || isBelowArena(playerB, event.getArena())) {
                    eliminate(playerB, "fell into the water");
                    this.cancel();
                    event.setWaterCheckTask(null);
                }
            }
        }.runTaskTimer(this.plugin, 0L, 5L));
    }

    private void startEliminationEvent(ActiveEvent event) {
        List<Player> players = this.getOnlineParticipants(event);
        List<Location> spawns = new ArrayList<>(event.getDefinition().getSpawnLocations());
        if (spawns.isEmpty()) {
            Location fallback = this.getWaitingLocation(event);
            if (fallback != null) {
                spawns.add(fallback);
            }
        }

        for (EventParticipant participant : event.getParticipants().values()) {
            participant.setState(EventPlayerState.PREPARING);
        }

        if (!spawns.isEmpty()) {
            Collections.shuffle(spawns);
            for (int index = 0; index < players.size(); index++) {
                this.prepareEliminationPlayer(players.get(index), spawns.get(index % spawns.size()), event);
            }
        } else {
            players.forEach(player -> this.prepareEliminationPlayer(player, null, event));
        }

        this.runGenericStartCountdown(event, () -> {
            for (EventParticipant participant : event.getParticipants().values()) {
                participant.setState(EventPlayerState.FIGHTING);
            }
            this.startDurationTimer(event);
        });
    }

    private void startOitcEvent(ActiveEvent event) {
        List<Location> spawns = new ArrayList<>(event.getDefinition().getSpawnLocations());
        if (spawns.isEmpty()) {
            Location fallback = this.getWaitingLocation(event);
            if (fallback != null) {
                spawns.add(fallback);
            }
        }

        for (EventParticipant participant : event.getParticipants().values()) {
            participant.setState(EventPlayerState.PREPARING);
            participant.setScore(0);
        }

        for (Player player : this.getOnlineParticipants(event)) {
            this.prepareOitcPlayer(player, this.pickRandomLocation(spawns));
        }

        this.runGenericStartCountdown(event, () -> {
            for (EventParticipant participant : event.getParticipants().values()) {
                participant.setState(EventPlayerState.FIGHTING);
            }
            this.startDurationTimer(event);
        });
    }

    private void startParkourEvent(ActiveEvent event) {
        Location start = event.getDefinition().getLocation("start");
        if (start == null) {
            start = this.getWaitingLocation(event);
        }

        for (EventParticipant participant : event.getParticipants().values()) {
            participant.setState(EventPlayerState.PREPARING);
            participant.setCheckpoint(start);
            participant.setProgress(0);
        }

        for (Player player : this.getOnlineParticipants(event)) {
            this.prepareParkourPlayer(player, start);
        }

        this.runGenericStartCountdown(event, () -> {
            for (EventParticipant participant : event.getParticipants().values()) {
                participant.setState(EventPlayerState.FIGHTING);
            }
            this.startDurationTimer(event);
        });
    }

    private void startDropperEvent(ActiveEvent event) {
        List<Location> stages = event.getDefinition().getSpawnLocations();
        if (stages.isEmpty()) {
            this.stopActiveEvent("&cThe Dropper event requires configured spawn-locations.");
            return;
        }

        for (EventParticipant participant : event.getParticipants().values()) {
            participant.setState(EventPlayerState.PREPARING);
            participant.setProgress(0);
        }

        for (Player player : this.getOnlineParticipants(event)) {
            this.prepareDropperPlayer(player, stages.get(0));
        }

        this.runGenericStartCountdown(event, () -> {
            for (EventParticipant participant : event.getParticipants().values()) {
                participant.setState(EventPlayerState.FIGHTING);
            }
            this.startDurationTimer(event);
        });
    }

    private void startThimbleEvent(ActiveEvent event) {
        Location waiting = this.getWaitingLocation(event);
        if (waiting == null) {
            this.stopActiveEvent("&cThe Thimble event requires a configured locations.waiting.");
            return;
        }

        for (EventParticipant participant : event.getParticipants().values()) {
            participant.setState(EventPlayerState.PREPARING);
            participant.setMarked(false);
        }

        for (Player player : this.getOnlineParticipants(event)) {
            this.prepareUtilityPlayer(player, waiting, event);
        }

        this.runGenericStartCountdown(event, () -> {
            this.startDurationTimer(event);
            this.startThimbleRound(event);
        });
    }

    private void startStoplightEvent(ActiveEvent event) {
        if (event.getDefinition().getSpawnLocations().isEmpty() || event.getDefinition().getLocation("finish") == null) {
            this.stopActiveEvent("&cThe Stoplight event requires spawn-locations and locations.finish.");
            return;
        }

        for (EventParticipant participant : event.getParticipants().values()) {
            participant.setState(EventPlayerState.PREPARING);
        }

        for (Player player : this.getOnlineParticipants(event)) {
            this.prepareUtilityPlayer(player, this.pickRandomLocation(event.getDefinition().getSpawnLocations()), event);
        }

        this.runGenericStartCountdown(event, () -> {
            for (EventParticipant participant : event.getParticipants().values()) {
                participant.setState(EventPlayerState.FIGHTING);
            }

            event.getMetadata().put(STOPLIGHT_STATE_KEY, Boolean.TRUE);
            event.getMetadata().put(STOPLIGHT_MOVERS_KEY, new HashSet<UUID>());

            for (Player player : this.getOnlineParticipants(event)) {
                this.giveStoplightIndicator(player, true);
            }

            this.startDurationTimer(event);
            this.startStoplightCycle(event);
        });
    }

    private void startFourCornersEvent(ActiveEvent event) {
        if (event.getArena() == null || event.getArena().getMinimum() == null || event.getArena().getMaximum() == null) {
            this.stopActiveEvent("&cThe 4Corners event requires arena minimum/maximum limits.");
            return;
        }

        Location waiting = this.getWaitingLocation(event);
        for (EventParticipant participant : event.getParticipants().values()) {
            participant.setState(EventPlayerState.PREPARING);
        }

        for (Player player : this.getOnlineParticipants(event)) {
            this.prepareUtilityPlayer(player, waiting, event);
        }

        this.runGenericStartCountdown(event, () -> {
            for (EventParticipant participant : event.getParticipants().values()) {
                participant.setState(EventPlayerState.FIGHTING);
            }
            this.startDurationTimer(event);
            this.startFourCornersCycle(event);
        });
    }

    private void startTntTagEvent(ActiveEvent event) {
        List<Location> spawns = new ArrayList<>(event.getDefinition().getSpawnLocations());
        if (spawns.isEmpty()) {
            Location waiting = this.getWaitingLocation(event);
            if (waiting != null) {
                spawns.add(waiting);
            }
        }

        for (EventParticipant participant : event.getParticipants().values()) {
            participant.setState(EventPlayerState.PREPARING);
            participant.setMarked(false);
        }

        for (Player player : this.getOnlineParticipants(event)) {
            this.prepareUtilityPlayer(player, this.pickRandomLocation(spawns), event);
        }

        this.runGenericStartCountdown(event, () -> {
            for (EventParticipant participant : event.getParticipants().values()) {
                participant.setState(EventPlayerState.FIGHTING);
            }

            this.getTaggedPlayers(event).clear();
            this.assignTaggedPlayers(event);
            this.startDurationTimer(event);
            this.startTntTagTimer(event);
        });
    }

    private void runGenericStartCountdown(ActiveEvent event, Runnable onStart) {
        this.cancelTertiaryTask(event);

        event.setTertiaryTask(new BukkitRunnable() {
            private int countdown = 3;

            @Override
            public void run() {
                if (activeEvent != event) {
                    this.cancel();
                    event.setTertiaryTask(null);
                    return;
                }

                if (this.countdown <= 0) {
                    sendEventParticipantsMessage("&b[Event] &aGo!");
                    onStart.run();
                    this.cancel();
                    event.setTertiaryTask(null);
                    return;
                }

                sendEventParticipantsMessage("&b[Event] &fStarting in &b" + this.countdown + "&f...");
                this.countdown--;
            }
        }.runTaskTimer(this.plugin, 0L, 20L));
    }

    private void startDurationTimer(ActiveEvent event) {
        this.cancelPrimaryTask(event);
        int durationSeconds = Math.max(30, event.getDefinition().getIntSetting("duration-seconds", 300));

        event.setPrimaryTask(new BukkitRunnable() {
            private int time = durationSeconds;

            @Override
            public void run() {
                if (activeEvent != event) {
                    this.cancel();
                    event.setPrimaryTask(null);
                    return;
                }

                if (time <= 0) {
                    finishWithWinner(pickRandomParticipant(event));
                    this.cancel();
                    event.setPrimaryTask(null);
                    return;
                }

                if (event.getParticipants().size() == 1) {
                    finishWithWinner(org.bukkit.Bukkit.getPlayer(event.getParticipants().keySet().iterator().next()));
                    this.cancel();
                    event.setPrimaryTask(null);
                    return;
                }

                if (Arrays.asList(60, 50, 40, 30, 25, 20, 15, 10, 5, 4, 3, 2, 1).contains(time)) {
                    sendEventParticipantsMessage("&b[Event] &fEnds in &b" + time + "s&f.");
                }

                time--;
            }
        }.runTaskTimer(this.plugin, 20L, 20L));
    }

    private void startStoplightCycle(ActiveEvent event) {
        this.cancelSecondaryTask(event);
        event.setSecondaryTask(new BukkitRunnable() {
            private boolean goState = true;
            private int time = randomBetween(
                    event.getDefinition().getIntSetting("go-min-seconds", 2),
                    event.getDefinition().getIntSetting("go-max-seconds", 4)
            );

            @Override
            public void run() {
                if (activeEvent != event) {
                    this.cancel();
                    event.setSecondaryTask(null);
                    return;
                }

                if (event.getParticipants().size() <= 1) {
                    this.cancel();
                    event.setSecondaryTask(null);
                    return;
                }

                if (time <= 0) {
                    if (!goState) {
                        Set<UUID> movers = getStoplightMovers(event);
                        new ArrayList<>(movers).forEach(uuid -> {
                            Player movingPlayer = org.bukkit.Bukkit.getPlayer(uuid);
                            if (movingPlayer != null && event.isParticipant(uuid)) {
                                handleParticipantRemoval(movingPlayer, "moved during STOP", true);
                            }
                        });
                        movers.clear();
                    }

                    goState = !goState;
                    event.getMetadata().put(STOPLIGHT_STATE_KEY, goState);

                    for (Player player : getOnlineParticipants(event)) {
                        giveStoplightIndicator(player, goState);
                        player.playSound(player.getLocation(), goState ? Sound.NOTE_PLING : Sound.ANVIL_LAND, 1.0F, 1.0F);
                    }

                    time = goState
                            ? randomBetween(event.getDefinition().getIntSetting("go-min-seconds", 2), event.getDefinition().getIntSetting("go-max-seconds", 4))
                            : randomBetween(event.getDefinition().getIntSetting("stop-min-seconds", 2), event.getDefinition().getIntSetting("stop-max-seconds", 4));
                }

                time--;
            }
        }.runTaskTimer(this.plugin, 20L, 20L));
    }

    private void startFourCornersCycle(ActiveEvent event) {
        this.cancelSecondaryTask(event);
        event.setSecondaryTask(new BukkitRunnable() {
            private int countdown = event.getDefinition().getIntSetting("round-seconds", 10);
            private final Map<Location, Byte> removed = new HashMap<>();

            @Override
            public void run() {
                if (activeEvent != event) {
                    restoreCornersBlocks(this.removed);
                    this.cancel();
                    event.setSecondaryTask(null);
                    return;
                }

                if (event.getParticipants().size() <= 1) {
                    restoreCornersBlocks(this.removed);
                    this.cancel();
                    event.setSecondaryTask(null);
                    return;
                }

                if (countdown <= 0) {
                    byte removedColor = CORNER_COLORS.get(ThreadLocalRandom.current().nextInt(CORNER_COLORS.size()));
                    removeCornerColor(event, removedColor, this.removed);
                    sendEventParticipantsMessage("&b[Event] &fThe removed color was &b" + describeWool(removedColor) + "&f.");
                    org.bukkit.Bukkit.getScheduler().runTaskLater(plugin, () -> restoreCornersBlocks(this.removed), 60L);
                    countdown = event.getDefinition().getIntSetting("round-seconds", 10);
                    return;
                }

                if (Arrays.asList(10, 5, 4, 3, 2, 1).contains(countdown)) {
                    sendEventParticipantsMessage("&b[Event] &fA color will be removed in &b" + countdown + "s&f.");
                }

                countdown--;
            }
        }.runTaskTimer(this.plugin, 20L, 20L));
    }

    private void startTntTagTimer(ActiveEvent event) {
        this.cancelSecondaryTask(event);
        event.setSecondaryTask(new BukkitRunnable() {
            private int time = event.getDefinition().getIntSetting("tag-seconds", 30);

            @Override
            public void run() {
                if (activeEvent != event) {
                    this.cancel();
                    event.setSecondaryTask(null);
                    return;
                }

                if (time <= 0) {
                    Set<UUID> taggedPlayers = new HashSet<>(getTaggedPlayers(event));
                    for (UUID uuid : taggedPlayers) {
                        Player tagged = org.bukkit.Bukkit.getPlayer(uuid);
                        if (tagged != null && event.isParticipant(uuid)) {
                            handleParticipantRemoval(tagged, "exploded with TNT", true);
                        }
                    }

                    if (event.getParticipants().size() <= 1) {
                        this.cancel();
                        event.setSecondaryTask(null);
                        return;
                    }

                    getTaggedPlayers(event).clear();
                    assignTaggedPlayers(event);
                    time = event.getDefinition().getIntSetting("tag-seconds", 30);
                    return;
                }

                time--;
            }
        }.runTaskTimer(this.plugin, 20L, 20L));
    }

    private void startThimbleRound(ActiveEvent event) {
        Location jumpLocation = event.getDefinition().getLocation("jump");
        Location waitingLocation = this.getWaitingLocation(event);
        if (jumpLocation == null || waitingLocation == null) {
            this.stopActiveEvent("&cThe Thimble event requires locations.jump and locations.waiting.");
            return;
        }

        for (EventParticipant participant : event.getParticipants().values()) {
            participant.setMarked(false);
            participant.setState(EventPlayerState.PREPARING);
        }

        for (Player player : this.getOnlineParticipants(event)) {
            this.prepareUtilityPlayer(player, waitingLocation, event);
        }

        this.runGenericStartCountdown(event, () -> {
            for (EventParticipant participant : event.getParticipants().values()) {
                participant.setState(EventPlayerState.FIGHTING);
            }

            for (Player player : this.getOnlineParticipants(event)) {
                this.prepareUtilityPlayer(player, jumpLocation, event);
            }

            this.cancelSecondaryTask(event);
            event.setSecondaryTask(new BukkitRunnable() {
                private int time = event.getDefinition().getIntSetting("round-seconds", 20);

                @Override
                public void run() {
                    if (activeEvent != event) {
                        this.cancel();
                        event.setSecondaryTask(null);
                        return;
                    }

                    if (time <= 0) {
                        List<Player> failedPlayers = event.getParticipants().values().stream()
                                .filter(participant -> participant.getState() == EventPlayerState.FIGHTING)
                                .map(participant -> org.bukkit.Bukkit.getPlayer(participant.getUuid()))
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList());

                        failedPlayers.forEach(player -> handleParticipantRemoval(player, "missed the drop", true));
                        if (event.getParticipants().size() <= 1) {
                            this.cancel();
                            event.setSecondaryTask(null);
                            return;
                        }

                        org.bukkit.Bukkit.getScheduler().runTaskLater(plugin, () -> startThimbleRound(event), 20L);
                        this.cancel();
                        event.setSecondaryTask(null);
                        return;
                    }

                    time--;
                }
            }.runTaskTimer(this.plugin, 20L, 20L));
        });
    }

    private void handleParkourMove(Player player, EventParticipant participant) {
        ActiveEvent event = this.activeEvent;
        if (event == null || participant.getState() != EventPlayerState.FIGHTING) {
            return;
        }

        if (this.isOn(player, Material.GOLD_PLATE)) {
            this.finishWithWinner(player);
            return;
        }

        if (this.isOnLiquid(player) || this.isBelowArena(player, event.getArena())) {
            this.teleportParkourPlayer(player, participant);
            return;
        }

        if (this.isOn(player, Material.STONE_PLATE) || this.isOn(player, Material.IRON_PLATE) || this.isOn(player, Material.WOOD_PLATE)) {
            Location current = player.getLocation();
            if (!this.isSameBlock(participant.getCheckpoint(), current)) {
                participant.setCheckpoint(current.clone());
                participant.setProgress(participant.getProgress() + 1);
                player.sendMessage(CC.translate("&b[Event] &fCheckpoint &b#" + participant.getProgress() + " &fsaved."));
            }
        }
    }

    private void handleDropperMove(Player player, EventParticipant participant) {
        ActiveEvent event = this.activeEvent;
        if (event == null || participant.getState() != EventPlayerState.FIGHTING || !this.isOnLiquid(player)) {
            return;
        }

        participant.setProgress(participant.getProgress() + 1);
        if (participant.getProgress() >= event.getDefinition().getSpawnLocations().size()) {
            this.finishWithWinner(player);
            return;
        }

        player.teleport(event.getDefinition().getSpawnLocations().get(participant.getProgress()));
    }

    private void handleThimbleMove(Player player, EventParticipant participant) {
        ActiveEvent event = this.activeEvent;
        if (event == null || participant.getState() != EventPlayerState.FIGHTING) {
            return;
        }

        if (this.isOnLiquid(player)) {
            participant.setMarked(true);
            participant.setState(EventPlayerState.WAITING);
            Location waiting = this.getWaitingLocation(event);
            if (waiting != null) {
                this.prepareUtilityPlayer(player, waiting, event);
            }

            boolean anyoneStillJumping = event.getParticipants().values().stream()
                    .anyMatch(data -> data.getState() == EventPlayerState.FIGHTING);
            if (!anyoneStillJumping) {
                List<UUID> qualified = event.getParticipants().values().stream()
                        .filter(EventParticipant::isMarked)
                        .map(EventParticipant::getUuid)
                        .collect(Collectors.toList());

                if (qualified.size() == 1) {
                    this.finishWithWinner(org.bukkit.Bukkit.getPlayer(qualified.get(0)));
                } else if (!qualified.isEmpty()) {
                    org.bukkit.Bukkit.getScheduler().runTaskLater(this.plugin, () -> this.startThimbleRound(event), 20L);
                }
            }
        } else if (this.isBelowArena(player, event.getArena())) {
            this.handleParticipantRemoval(player, "missed the drop", true);
        }
    }

    private void handleStoplightMove(PlayerMoveEvent event, EventParticipant participant) {
        ActiveEvent active = this.activeEvent;
        if (active == null || participant.getState() != EventPlayerState.FIGHTING) {
            return;
        }

        Location finish = active.getDefinition().getLocation("finish");
        if (finish != null && event.getTo().distanceSquared(finish) <= Math.pow(active.getDefinition().getDoubleSetting("finish-radius", 2.5D), 2)) {
            this.finishWithWinner(event.getPlayer());
            return;
        }

        boolean goState = this.getBooleanMetadata(active, STOPLIGHT_STATE_KEY, true);
        if (goState) {
            return;
        }

        Location from = event.getFrom();
        Location to = event.getTo();
        if (to != null && (Math.abs(from.getX() - to.getX()) > 0.05D || Math.abs(from.getZ() - to.getZ()) > 0.05D)) {
            this.getStoplightMovers(active).add(event.getPlayer().getUniqueId());
        }
    }

    private void handleOitcDeath(Player player, Player killer, String reason) {
        ActiveEvent event = this.activeEvent;
        if (event == null || !event.isParticipant(player.getUniqueId())) {
            return;
        }

        EventParticipant victim = event.getParticipant(player.getUniqueId());
        if (victim == null) {
            return;
        }

        victim.setState(EventPlayerState.RESPAWNING);
        PlayerUtil.reset(player, true, true);

        if (killer != null && !killer.getUniqueId().equals(player.getUniqueId()) && event.isParticipant(killer.getUniqueId())) {
            EventParticipant killerData = event.getParticipant(killer.getUniqueId());
            if (killerData != null) {
                killerData.setScore(killerData.getScore() + 1);
                this.applyOitcReward(killer);
                if (killerData.getScore() >= event.getDefinition().getIntSetting("score-to-win", 20)) {
                    this.finishWithWinner(killer);
                    return;
                }
            }
        }

        player.sendMessage(CC.translate("&cYou died: " + reason + "."));
        org.bukkit.Bukkit.getScheduler().runTaskLater(this.plugin, () -> this.respawnOitcPlayer(player), 60L);
    }

    private void respawnOitcPlayer(Player player) {
        ActiveEvent event = this.activeEvent;
        if (event == null || !event.isParticipant(player.getUniqueId())) {
            return;
        }

        EventParticipant participant = event.getParticipant(player.getUniqueId());
        if (participant == null) {
            return;
        }

        participant.setState(EventPlayerState.FIGHTING);
        this.prepareOitcPlayer(player, this.pickRandomLocation(event.getDefinition().getSpawnLocations()));
    }

    private void scheduleDropperRespawn(Player player) {
        ActiveEvent event = this.activeEvent;
        if (event == null || !event.isParticipant(player.getUniqueId())) {
            return;
        }

        EventParticipant participant = event.getParticipant(player.getUniqueId());
        if (participant == null) {
            return;
        }

        participant.setState(EventPlayerState.RESPAWNING);
        PlayerUtil.reset(player, true, true);

        org.bukkit.Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            ActiveEvent current = this.activeEvent;
            if (current == null || !current.isParticipant(player.getUniqueId())) {
                return;
            }

            EventParticipant currentParticipant = current.getParticipant(player.getUniqueId());
            if (currentParticipant == null) {
                return;
            }

            currentParticipant.setState(EventPlayerState.FIGHTING);
            Location stage = current.getDefinition().getSpawnLocations().get(Math.min(currentParticipant.getProgress(), current.getDefinition().getSpawnLocations().size() - 1));
            this.prepareDropperPlayer(player, stage);
        }, 20L);
    }

    private void handleFighterRemoval(Player loser, String reason) {
        ActiveEvent event = this.activeEvent;
        if (event == null) {
            return;
        }

        UUID loserId = loser.getUniqueId();
        UUID winnerId = loserId.equals(event.getCurrentFighterA()) ? event.getCurrentFighterB() : event.getCurrentFighterA();
        Player winner = winnerId == null ? null : org.bukkit.Bukkit.getPlayer(winnerId);

        this.cancelFightTasks(event);
        event.getParticipants().remove(loserId);
        event.clearCurrentFight();

        EventParticipant winnerParticipant = winnerId == null ? null : event.getParticipant(winnerId);
        if (winnerParticipant != null) {
            winnerParticipant.setState(EventPlayerState.WAITING);
            if (winner != null) {
                this.sendPlayerToWaitingArea(winner, event);
            }
        }

        this.restorePlayerToLobby(loser);
        this.sendEventParticipantsMessage("&b[Event] &c" + loser.getName() + " &fwas eliminated"
                + (winner != null ? " by &a" + winner.getName() : "") + "&f.");

        if (event.getParticipants().size() <= 1) {
            Player finalWinner = event.getParticipants().isEmpty()
                    ? null
                    : org.bukkit.Bukkit.getPlayer(event.getParticipants().keySet().iterator().next());
            this.finishWithWinner(finalWinner);
            return;
        }

        if (reason != null && !reason.isEmpty()) {
            loser.sendMessage(CC.translate("&cYou were eliminated: " + reason + "."));
        }

        org.bukkit.Bukkit.getScheduler().runTaskLater(this.plugin, this::pickNextFight, 40L);
    }

    private void handleParticipantRemoval(Player player, String reason, boolean announce) {
        ActiveEvent event = this.activeEvent;
        if (event == null || !event.isParticipant(player.getUniqueId())) {
            return;
        }

        event.getParticipants().remove(player.getUniqueId());
        if (player.isOnline()) {
            this.restorePlayerToLobby(player);
        }

        if (announce) {
            this.sendEventParticipantsMessage("&b[Event] &c" + player.getName() + " &fwas removed" + (reason != null ? " &7(" + reason + ")" : "") + "&f.");
        }

        if (event.getParticipants().isEmpty()) {
            this.stopActiveEvent("&cThe event has been terminated because all players left.");
            return;
        }

        if (event.getParticipants().size() == 1) {
            this.finishWithWinner(org.bukkit.Bukkit.getPlayer(event.getParticipants().keySet().iterator().next()));
        }
    }

    private void finishWithWinner(Player winner) {
        ActiveEvent event = this.activeEvent;
        if (event == null) {
            return;
        }

        if (winner != null) {
            this.broadcast("&b[Event] &a" + winner.getName() + " &fhas won " + event.getDefinition().getDisplayName() + "&f.");
        } else {
            this.broadcast("&b[Event] &fThe event ended without a winner.");
        }

        this.stopActiveEvent("");
    }

    private void prepareProfileForEvent(Player player) {
        Profile profile = this.profileService.getProfile(player.getUniqueId());
        if (profile == null) {
            return;
        }

        profile.setState(ProfileState.PLAYING_EVENT);
        profile.setMatch(null);
        profile.setFfaMatch(null);
        profile.setQueueProfile(null);
    }

    private void prepareRoundPlayer(Player player, Location teleportLocation, ActiveEvent event) {
        PlayerUtil.reset(player, true, true);
        if (teleportLocation != null) {
            player.teleport(teleportLocation);
        }

        switch (event.getDefinition().getType()) {
            case SUMO:
            case BRACKETS:
                this.applyConfiguredKit(player, event.getKit());
                break;
            case GULAG:
                if (event.getKit() != null) {
                    this.applyConfiguredKit(player, event.getKit());
                } else {
                    this.giveGulagLoadout(player);
                }
                break;
            default:
                break;
        }

        player.updateInventory();
    }

    private void prepareEliminationPlayer(Player player, Location teleportLocation, ActiveEvent event) {
        PlayerUtil.reset(player, true, true);
        if (teleportLocation != null) {
            player.teleport(teleportLocation);
        }

        switch (event.getDefinition().getType()) {
            case LMS:
            case SKYWARS:
                this.applyConfiguredKit(player, event.getKit());
                break;
            case KNOCKOUT:
                this.giveKnockoutLoadout(player);
                break;
            case SPLEEF:
                this.giveSpleefLoadout(player);
                break;
            default:
                break;
        }

        player.updateInventory();
    }

    private void prepareOitcPlayer(Player player, Location teleportLocation) {
        PlayerUtil.reset(player, true, true);
        if (teleportLocation != null) {
            player.teleport(teleportLocation);
        }
        this.giveOitcLoadout(player);
        player.updateInventory();
    }

    private void prepareParkourPlayer(Player player, Location teleportLocation) {
        PlayerUtil.reset(player, true, true);
        if (teleportLocation != null) {
            player.teleport(teleportLocation);
        }
        player.updateInventory();
    }

    private void prepareDropperPlayer(Player player, Location teleportLocation) {
        PlayerUtil.reset(player, true, true);
        if (teleportLocation != null) {
            player.teleport(teleportLocation);
        }
        player.updateInventory();
    }

    private void prepareUtilityPlayer(Player player, Location teleportLocation, ActiveEvent event) {
        PlayerUtil.reset(player, true, true);
        if (teleportLocation != null) {
            player.teleport(teleportLocation);
        }
        if (event.getDefinition().getType() == EventType.TNTTAG) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
        }
        player.updateInventory();
    }

    private void sendPlayerToWaitingArea(Player player, ActiveEvent event) {
        PlayerUtil.reset(player, true, true);
        Location waiting = this.getWaitingLocation(event);
        if (waiting != null) {
            player.teleport(waiting);
        } else {
            this.spawnService.teleportToSpawn(player);
        }
        this.giveLeaveItem(player);
    }

    private void giveLeaveItem(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);
        player.getInventory().setItem(8, new ItemBuilder(Material.INK_SACK)
                .name(LEAVE_ITEM_NAME)
                .durability(1)
                .lore("&cClick to leave &7(Right-Click)")
                .hideMeta()
                .build());
        player.updateInventory();
    }

    private void restorePlayerToLobby(Player player) {
        Profile profile = this.profileService.getProfile(player.getUniqueId());
        if (profile != null) {
            profile.setState(ProfileState.LOBBY);
        }

        PlayerUtil.reset(player, true, true);
        this.spawnService.teleportToSpawn(player);
        this.hotbarService.applyHotbarItems(player);
        this.visibilityService.updateVisibility(player);
    }

    private void sendEventParticipantsMessage(String message) {
        if (this.activeEvent == null) {
            return;
        }

        for (UUID uuid : this.activeEvent.getParticipants().keySet()) {
            Player participant = org.bukkit.Bukkit.getPlayer(uuid);
            if (participant != null) {
                participant.sendMessage(CC.translate(message));
            }
        }
    }

    private void cancelEventTasks(ActiveEvent event) {
        if (event == null) {
            return;
        }

        if (event.getCountdownTask() != null) {
            event.getCountdownTask().cancel();
            event.setCountdownTask(null);
        }

        this.cancelFightTasks(event);
        this.cancelPrimaryTask(event);
        this.cancelSecondaryTask(event);
        this.cancelTertiaryTask(event);
    }

    private void cancelFightTasks(ActiveEvent event) {
        if (event == null) {
            return;
        }

        if (event.getPreparationTask() != null) {
            event.getPreparationTask().cancel();
            event.setPreparationTask(null);
        }

        this.cancelSecondaryTask(event);
        this.cancelWaterCheck(event);
    }

    private void cancelWaterCheck(ActiveEvent event) {
        if (event.getWaterCheckTask() != null) {
            event.getWaterCheckTask().cancel();
            event.setWaterCheckTask(null);
        }
    }

    private void cancelPrimaryTask(ActiveEvent event) {
        if (event.getPrimaryTask() != null) {
            event.getPrimaryTask().cancel();
            event.setPrimaryTask(null);
        }
    }

    private void cancelSecondaryTask(ActiveEvent event) {
        if (event.getSecondaryTask() != null) {
            event.getSecondaryTask().cancel();
            event.setSecondaryTask(null);
        }
    }

    private void cancelTertiaryTask(ActiveEvent event) {
        if (event.getTertiaryTask() != null) {
            event.getTertiaryTask().cancel();
            event.setTertiaryTask(null);
        }
    }

    private void restoreModifiedBlocks(ActiveEvent event) {
        if (event == null) {
            return;
        }

        if (event.getDefinition().getType() == EventType.SPLEEF) {
            for (Location location : event.getModifiedBlocks()) {
                location.getBlock().setType(Material.SNOW_BLOCK);
            }
            event.getModifiedBlocks().clear();
        }

        Map<Location, Byte> removed = this.getCornersRemovedBlocks(event);
        if (!removed.isEmpty()) {
            this.restoreCornersBlocks(removed);
        }
    }

    private Arena getArena(EventDefinition definition) {
        if (definition.getArenaName() == null || definition.getArenaName().trim().isEmpty()) {
            return null;
        }
        return this.arenaService.getArenaByName(definition.getArenaName());
    }

    private String validateDefinition(EventDefinition definition, Arena arena) {
        if (arena.getType() != ArenaType.EVENT) {
            return "&cThe arena for this event needs to be of type EVENT.";
        }

        if (!arena.getEvents().contains(definition.getKey().toLowerCase())) {
            return "&cThe arena for this event is not linked to the event " + definition.getKey().toLowerCase() + ".";
        }

        switch (definition.getType()) {
            case SUMO:
            case GULAG:
            case BRACKETS:
                if (arena.getPos1() == null || arena.getPos2() == null) {
                    return "&cThe arena for this event needs to have pos1 and pos2 defined.";
                }
                return null;
            case LMS:
            case KNOCKOUT:
            case SKYWARS:
            case OITC:
            case STOPLIGHT:
            case DROPPER:
                return definition.getSpawnLocations().isEmpty() ? "&cThis event requires configured spawn-locations." : null;
            case PARKOUR:
                return definition.getLocation("start") == null && arena.getCenter() == null
                        ? "&cThe Parkour event requires locations.start or a center defined in the arena."
                        : null;
            case THIMBLE:
                return definition.getLocation("jump") == null || definition.getLocation("waiting") == null
                        ? "&cThe Thimble event requires locations.jump and locations.waiting."
                        : null;
            case FOUR_CORNERS:
                return arena.getMinimum() == null || arena.getMaximum() == null
                        ? "&cThe 4Corners event requires arena minimum and maximum limits."
                        : null;
            default:
                return null;
        }
    }

    private String getAdminPermission() {
        return this.plugin.getService(PluginConstant.class).getAdminPermissionPrefix();
    }

    private Kit resolveKit(EventDefinition definition) {
        if (definition.getKitName() != null && !definition.getKitName().trim().isEmpty()) {
            return this.kitService.getKit(definition.getKitName());
        }

        if (definition.getType() == EventType.SUMO) {
            return this.kitService.getKits().stream()
                    .filter(Kit::isEnabled)
                    .filter(kit -> kit.isSettingEnabled(KitSettingSumo.class))
                    .findFirst()
                    .orElse(null);
        }

        return null;
    }

    private boolean isOnLiquid(Player player) {
        Material feet = player.getLocation().getBlock().getType();
        Material below = player.getLocation().clone().subtract(0.0D, 1.0D, 0.0D).getBlock().getType();
        return feet == Material.WATER || feet == Material.STATIONARY_WATER || below == Material.WATER || below == Material.STATIONARY_WATER;
    }

    private boolean isBelowArena(Player player, Arena arena) {
        if (arena instanceof StandAloneArena) {
            return player.getLocation().getY() <= ((StandAloneArena) arena).getVoidLevel();
        }

        if (arena == null || arena.getPos1() == null || arena.getPos2() == null) {
            return false;
        }

        double minimumY = Math.min(arena.getPos1().getY(), arena.getPos2().getY()) - 6.0D;
        return player.getLocation().getY() <= minimumY;
    }

    private boolean isOn(Player player, Material material) {
        return player.getLocation().clone().subtract(0.0D, 1.0D, 0.0D).getBlock().getType() == material;
    }

    private boolean isSameBlock(Location first, Location second) {
        if (first == null || second == null || first.getWorld() == null || second.getWorld() == null) {
            return false;
        }
        return first.getWorld().getName().equals(second.getWorld().getName())
                && first.getBlockX() == second.getBlockX()
                && first.getBlockY() == second.getBlockY()
                && first.getBlockZ() == second.getBlockZ();
    }

    private boolean isBracketType(EventType type) {
        return type == EventType.SUMO || type == EventType.GULAG || type == EventType.BRACKETS;
    }

    private boolean isInventoryActionLocked(Player player, boolean pickup) {
        ActiveEvent active = this.activeEvent;
        if (player == null || active == null || !active.isParticipant(player.getUniqueId())) {
            return false;
        }

        switch (active.getDefinition().getType()) {
            case SKYWARS:
                return false;
            case BRACKETS:
            case GULAG:
            case LMS:
            case OITC:
                return pickup;
            default:
                return true;
        }
    }

    private Location getWaitingLocation(ActiveEvent event) {
        if (event == null) {
            return null;
        }

        Location waiting = event.getDefinition().getLocation("waiting");
        if (waiting != null) {
            return waiting;
        }

        if (event.getArena() != null && event.getArena().getCenter() != null) {
            return event.getArena().getCenter();
        }

        return this.spawnService.getLocation();
    }

    private void applyConfiguredKit(Player player, Kit kit) {
        if (kit == null) {
            return;
        }

        player.getInventory().setArmorContents(InventoryUtil.cloneItemStackArray(kit.getArmor()));
        player.getInventory().setContents(InventoryUtil.cloneItemStackArray(kit.getItems()));
        kit.applyPotionEffects(player);

        KnockbackAdapter knockbackAdapter = this.plugin.getService(KnockbackAdapter.class);
        if (knockbackAdapter != null && kit.getKnockbackProfile() != null && !kit.getKnockbackProfile().isEmpty()) {
            knockbackAdapter.getKnockbackImplementation().applyKnockback(player, kit.getKnockbackProfile());
        }
    }

    private void giveKnockoutLoadout(Player player) {
        player.getInventory().setItem(0, new ItemBuilder(Material.STICK)
                .name("&bKnockout Stick")
                .enchantment(Enchantment.KNOCKBACK, 1)
                .hideMeta()
                .build());
    }

    private void giveSpleefLoadout(Player player) {
        player.getInventory().setItem(0, new ItemBuilder(Material.DIAMOND_SPADE)
                .enchantment(Enchantment.DIG_SPEED, 10)
                .hideMeta()
                .build());
    }

    private void giveOitcLoadout(Player player) {
        player.getInventory().setItem(0, new ItemStack(Material.WOOD_SWORD));
        player.getInventory().setItem(1, new ItemStack(Material.BOW));
        player.getInventory().setItem(8, new ItemStack(Material.ARROW, 1));
    }

    private void giveGulagLoadout(Player player) {
        player.getInventory().setItem(0, new ItemBuilder(Material.IRON_HOE).name("&8[&7Glock-19&8]").build());
        player.getInventory().setItem(1, new ItemStack(Material.BOW));
        player.getInventory().setItem(8, new ItemStack(Material.ARROW, 1));
    }

    private void giveStoplightIndicator(Player player, boolean goState) {
        ItemStack wool = new ItemBuilder(Material.WOOL)
                .name(goState ? "&a&lGO GO GO!" : "&c&lSTOP!")
                .durability(goState ? 5 : 14)
                .build();
        for (int slot = 0; slot < 9; slot++) {
            player.getInventory().setItem(slot, wool);
        }
        player.updateInventory();
    }

    private void applyOitcReward(Player killer) {
        if (killer == null) {
            return;
        }

        if (killer.getInventory().getItem(8) == null || killer.getInventory().getItem(8).getType() == Material.AIR) {
            killer.getInventory().setItem(8, new ItemStack(Material.ARROW, 1));
        } else {
            ItemStack arrows = killer.getInventory().getItem(8);
            arrows.setAmount(Math.min(64, arrows.getAmount() + 1));
        }

        killer.setHealth(killer.getMaxHealth());
        killer.playSound(killer.getLocation(), Sound.NOTE_PLING, 1F, 1F);
        killer.updateInventory();
    }

    private void transferTag(Player attacker, Player victim) {
        ActiveEvent event = this.activeEvent;
        if (event == null) {
            return;
        }

        EventParticipant attackerData = event.getParticipant(attacker.getUniqueId());
        EventParticipant victimData = event.getParticipant(victim.getUniqueId());
        if (attackerData == null || victimData == null || !attackerData.isMarked() || victimData.isMarked()) {
            return;
        }

        attackerData.setMarked(false);
        victimData.setMarked(true);
        this.getTaggedPlayers(event).remove(attacker.getUniqueId());
        this.getTaggedPlayers(event).add(victim.getUniqueId());

        this.updateTntTagAppearance(attacker, false);
        this.updateTntTagAppearance(victim, true);
        this.sendEventParticipantsMessage("&b[Event] &c" + victim.getName() + " &fnow has the TNT.");
    }

    private void assignTaggedPlayers(ActiveEvent event) {
        List<UUID> available = new ArrayList<>(event.getParticipants().keySet());
        Collections.shuffle(available);

        int taggedCount = this.getTntTaggedCount(event.getParticipants().size());
        for (int index = 0; index < Math.min(taggedCount, available.size()); index++) {
            UUID uuid = available.get(index);
            EventParticipant participant = event.getParticipant(uuid);
            Player player = org.bukkit.Bukkit.getPlayer(uuid);
            if (participant != null) {
                participant.setMarked(true);
            }
            if (player != null) {
                this.getTaggedPlayers(event).add(uuid);
                this.updateTntTagAppearance(player, true);
            }
        }
    }

    private void updateTntTagAppearance(Player player, boolean tagged) {
        if (player == null) {
            return;
        }

        player.getInventory().setHelmet(tagged ? new ItemStack(Material.TNT, 1) : new ItemStack(Material.AIR, 1));
        player.getInventory().setItem(0, tagged ? new ItemStack(Material.TNT, 1) : new ItemStack(Material.AIR, 1));

        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, tagged ? 3 : 2));
        player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1F, 1F);
        player.updateInventory();
    }

    private int getTntTaggedCount(int playerCount) {
        if (playerCount >= 30) {
            return 6;
        }
        if (playerCount >= 20) {
            return 5;
        }
        if (playerCount >= 15) {
            return 4;
        }
        if (playerCount >= 5) {
            return 2;
        }
        return 1;
    }

    private void removeCornerColor(ActiveEvent event, byte woolData, Map<Location, Byte> removedBlocks) {
        if (event.getArena() == null || event.getArena().getMinimum() == null || event.getArena().getMaximum() == null) {
            return;
        }

        Location minimum = event.getArena().getMinimum();
        Location maximum = event.getArena().getMaximum();
        for (int x = Math.min(minimum.getBlockX(), maximum.getBlockX()); x <= Math.max(minimum.getBlockX(), maximum.getBlockX()); x++) {
            for (int y = Math.min(minimum.getBlockY(), maximum.getBlockY()); y <= Math.max(minimum.getBlockY(), maximum.getBlockY()); y++) {
                for (int z = Math.min(minimum.getBlockZ(), maximum.getBlockZ()); z <= Math.max(minimum.getBlockZ(), maximum.getBlockZ()); z++) {
                    Block block = minimum.getWorld().getBlockAt(x, y, z);
                    if (block.getType() == Material.WOOL && block.getData() == woolData) {
                        removedBlocks.put(block.getLocation(), woolData);
                        block.setType(Material.AIR);
                    }
                }
            }
        }

        this.getCornersRemovedBlocks(event).putAll(removedBlocks);
    }

    private void restoreCornersBlocks(Map<Location, Byte> removedBlocks) {
        removedBlocks.forEach((location, data) -> {
            Block block = location.getBlock();
            block.setType(Material.WOOL);
            block.setData(data);
        });
        removedBlocks.clear();
    }

    private void teleportParkourPlayer(Player player, EventParticipant participant) {
        if (player == null || participant == null) {
            return;
        }

        Location checkpoint = participant.getCheckpoint();
        if (checkpoint == null && this.activeEvent != null) {
            checkpoint = this.activeEvent.getDefinition().getLocation("start");
        }

        if (checkpoint != null) {
            player.teleport(checkpoint);
        }
    }

    private List<Player> getOnlineParticipants(ActiveEvent event) {
        return event.getParticipants().keySet().stream()
                .map(org.bukkit.Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Player pickRandomParticipant(ActiveEvent event) {
        List<Player> players = this.getOnlineParticipants(event);
        return players.isEmpty() ? null : players.get(ThreadLocalRandom.current().nextInt(players.size()));
    }

    private Location pickRandomLocation(List<Location> locations) {
        return locations == null || locations.isEmpty() ? null : locations.get(ThreadLocalRandom.current().nextInt(locations.size()));
    }

    private Player resolveDamager(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            return (Player) event.getDamager();
        }
        if (event.getDamager() instanceof Projectile && ((Projectile) event.getDamager()).getShooter() instanceof Player) {
            return (Player) ((Projectile) event.getDamager()).getShooter();
        }
        return null;
    }

    private void scheduleRespawn(Player player, Runnable afterRespawn) {
        org.bukkit.Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            if (player.isOnline()) {
                player.spigot().respawn();
            }
            org.bukkit.Bukkit.getScheduler().runTaskLater(this.plugin, afterRespawn, 1L);
        }, 1L);
    }

    private int randomBetween(int minimum, int maximum) {
        int safeMin = Math.min(minimum, maximum);
        int safeMax = Math.max(minimum, maximum);
        return ThreadLocalRandom.current().nextInt(safeMin, safeMax + 1);
    }

    @SuppressWarnings("unchecked")
    private Set<UUID> getStoplightMovers(ActiveEvent event) {
        return (Set<UUID>) event.getMetadata().computeIfAbsent(STOPLIGHT_MOVERS_KEY, key -> new HashSet<UUID>());
    }

    @SuppressWarnings("unchecked")
    private Set<UUID> getTaggedPlayers(ActiveEvent event) {
        return (Set<UUID>) event.getMetadata().computeIfAbsent(TNT_TAGGED_KEY, key -> new HashSet<UUID>());
    }

    @SuppressWarnings("unchecked")
    private Map<Location, Byte> getCornersRemovedBlocks(ActiveEvent event) {
        return (Map<Location, Byte>) event.getMetadata().computeIfAbsent(CORNERS_REMOVED_KEY, key -> new HashMap<Location, Byte>());
    }

    private boolean getBooleanMetadata(ActiveEvent event, String key, boolean defaultValue) {
        Object value = event.getMetadata().get(key);
        return value instanceof Boolean ? (Boolean) value : defaultValue;
    }

    private String describeWool(byte woolData) {
        switch (woolData) {
            case 14:
                return "Red";
            case 11:
                return "Blue";
            case 5:
                return "Green";
            case 4:
                return "Yellow";
            default:
                return "Unknown";
        }
    }

    private void broadcast(String message) {
        org.bukkit.Bukkit.broadcastMessage(CC.translate(message));
    }
}