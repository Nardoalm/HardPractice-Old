package live.pvphard.practice.feature.match;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.adapter.core.kaoscore.KaosCoreBridge;
import live.pvphard.practice.adapter.knockback.KnockbackAdapter;
import live.pvphard.practice.common.InventoryUtil;
import live.pvphard.practice.common.ListenerUtil;
import live.pvphard.practice.common.PlayerDisplayUtil;
import live.pvphard.practice.common.PlayerUtil;
import live.pvphard.practice.common.SoundUtil;
import live.pvphard.practice.common.logger.Logger;
import live.pvphard.practice.common.reflect.ReflectionService;
import live.pvphard.practice.common.reflect.internal.types.ActionBarReflectionServiceImpl;
import live.pvphard.practice.common.reflect.internal.types.DeathReflectionServiceImpl;
import live.pvphard.practice.common.reflect.internal.types.TitleReflectionServiceImpl;
import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.common.time.TimeUtil;
import live.pvphard.practice.core.locale.LocaleService;
import live.pvphard.practice.core.locale.internal.impl.message.GameMessagesLocaleImpl;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.enums.ProfileState;
import live.pvphard.practice.feature.arena.Arena;
import live.pvphard.practice.feature.arena.ArenaService;
import live.pvphard.practice.feature.arena.internal.types.StandAloneArena;
import live.pvphard.practice.feature.combat.CombatService;
import live.pvphard.practice.feature.cosmetic.CosmeticService;
import live.pvphard.practice.feature.cosmetic.internal.repository.BaseCosmeticRepository;
import live.pvphard.practice.feature.cosmetic.internal.repository.KillEffectRepository;
import live.pvphard.practice.feature.cosmetic.internal.repository.SoundEffectRepository;
import live.pvphard.practice.feature.cosmetic.internal.repository.impl.killeffect.BaseKillEffect;
import live.pvphard.practice.feature.cosmetic.internal.repository.impl.killmessage.KillMessagePack;
import live.pvphard.practice.feature.cosmetic.internal.repository.impl.soundeffect.BaseSoundEffect;
import live.pvphard.practice.feature.cosmetic.model.CosmeticType;
import live.pvphard.practice.feature.hotbar.HotbarService;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.feature.kit.setting.types.mechanic.KitSettingCampProtectionImpl;
import live.pvphard.practice.feature.kit.setting.types.mode.*;
import live.pvphard.practice.feature.kit.setting.types.visual.KitSettingHealthBar;
import live.pvphard.practice.feature.layout.LayoutService;
import live.pvphard.practice.feature.layout.data.LayoutData;
import live.pvphard.practice.feature.match.data.MatchData;
import live.pvphard.practice.feature.match.data.types.MatchDataSolo;
import live.pvphard.practice.feature.match.internal.types.RoundsMatch;
import live.pvphard.practice.feature.match.model.GameParticipant;
import live.pvphard.practice.feature.match.model.GamePlayer;
import live.pvphard.practice.feature.match.model.MatchGamePlayerData;
import live.pvphard.practice.feature.match.model.internal.MatchGamePlayer;
import live.pvphard.practice.feature.match.snapshot.Snapshot;
import live.pvphard.practice.feature.match.snapshot.SnapshotService;
import live.pvphard.practice.feature.match.task.MatchTask;
import live.pvphard.practice.feature.match.task.mode.PlatformDecayTask;
import live.pvphard.practice.feature.match.task.other.MatchCampProtectionTask;
import live.pvphard.practice.feature.match.task.other.MatchRespawnTask;
import live.pvphard.practice.feature.queue.Queue;
import live.pvphard.practice.feature.spawn.SpawnService;
import live.pvphard.practice.feature.tournament.model.Tournament;
import live.pvphard.practice.feature.visibility.VisibilityService;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@Setter
public abstract class Match {
    protected final HardPractice plugin = HardPractice.getInstance();

    private final Queue queue;
    private final Kit kit;
    private final Arena arena;
    private final boolean ranked;
    private final String matchId = UUID.randomUUID().toString();
    private Tournament tournament;

    private final Map<BlockState, Location> brokenBlocks = new ConcurrentHashMap<>();
    private final Map<BlockState, Location> placedBlocks = new ConcurrentHashMap<>();
    private final List<UUID> spectators = new CopyOnWriteArrayList<>();
    private final List<Snapshot> snapshots = new ArrayList<>();

    private boolean teamMatch;
    private boolean affectStatistics = true;

    private MatchTask runnable;
    private MatchState state;
    private long startTime;
    private long endTime;

    public Match(Queue queue, Kit kit, Arena arena, boolean ranked) {
        this.queue = Objects.requireNonNull(queue, "Queue cannot be null");
        this.kit = Objects.requireNonNull(kit, "Kit cannot be null");
        this.arena = Objects.requireNonNull(arena, "Arena cannot be null");
        this.ranked = ranked;
    }

    public abstract List<GameParticipant<MatchGamePlayer>> getParticipants();

    public abstract void handleDisconnect(Player player);

    public abstract void handleRespawn(Player player);

    public abstract boolean canStartRound();

    public abstract boolean canEndRound();

    public abstract boolean canEndMatch();

    public abstract void handleDeathItemDrop(Player player, PlayerDeathEvent event);

    public void startMatch() {
        this.sendPlayerVersusPlayerMessage();

        this.state = MatchState.STARTING;

        this.getParticipants().forEach(this::initializeParticipant);

        this.handleMatchTasks();

        this.startTime = System.currentTimeMillis();
    }

    public void endMatch() {
        this.cleanupHealthDisplay();

        deleteArenaCopyIfStandalone();

        this.getParticipants().forEach(this::finalizeParticipant);

        this.cleanupTasks();

        this.plugin.getService(MatchService.class).removeMatch(this);
    }

    private void deleteArenaCopyIfStandalone() {
        if (!(this.arena instanceof StandAloneArena)) {
            return;
        }

        ArenaService arenaService = this.plugin.getService(ArenaService.class);
        arenaService.deleteTemporaryArena((StandAloneArena) this.arena);
    }

    private void initializeParticipant(GameParticipant<MatchGamePlayer> gameParticipant) {
        VisibilityService visibilityService = this.plugin.getService(VisibilityService.class);
        KnockbackAdapter knockbackAdapter = this.plugin.getService(KnockbackAdapter.class);
        KaosCoreBridge kaosCoreBridge = this.plugin.getService(KaosCoreBridge.class);

        int participantIndex = this.getParticipants().indexOf(gameParticipant);
        Location spawnLocation = (participantIndex == 0 || participantIndex == -1)
                ? this.getArena().getPos1()
                : this.getArena().getPos2();

        gameParticipant.getPlayers().forEach(gamePlayer -> {
            Player player = this.plugin.getServer().getPlayer(gamePlayer.getUuid());
            if (player == null) {
                return;
            }

            Runnable setupTask = () -> {
                if (!player.isOnline()) return;

                if (spawnLocation != null) {
                    ListenerUtil.teleportAndClearSpawn(player, spawnLocation);
                }

                if (kaosCoreBridge != null) {
                    kaosCoreBridge.removeVanish(player);
                }

                this.updatePlayerProfileForMatch(player);
                this.setupPlayer(player);

                visibilityService.updateVisibility(player);
                knockbackAdapter.getKnockbackImplementation().applyKnockback(player, getKit().getKnockbackProfile());

                this.registerHealthObjectiveForPlayer(player);
                this.registerCampProtectionTask(player);
            };

            if (spawnLocation != null && this.arena instanceof StandAloneArena && !((StandAloneArena) this.arena).isManagedBySlimeWorldManager()) {
                // Delay teleport and setup by 15 ticks (750ms) to allow schematic to paste
                Bukkit.getScheduler().runTaskLater(this.plugin, setupTask, 15L);
            } else {
                setupTask.run();
            }
        });
    }

    private void finalizeParticipant(GameParticipant<MatchGamePlayer> gameParticipant) {
        gameParticipant.getPlayers().stream()
                .filter(gamePlayer -> !gamePlayer.isDisconnected())
                .forEach(gamePlayer -> {
                    Player player = this.plugin.getServer().getPlayer(gamePlayer.getUuid());
                    if (player != null) {
                        finalizePlayer(player);
                    }
                });
    }

    public void finalizePlayer(Player player) {
        VisibilityService visibilityService = this.plugin.getService(VisibilityService.class);
        this.updatePlayerProfileForLobby(player);
        this.resetPlayerState(player);
        visibilityService.updateVisibility(player);
        this.teleportPlayerToSpawn(player);
    }

    private void registerHealthObjectiveForPlayer(Player player) {
        if (!this.getKit().isSettingEnabled(KitSettingHealthBar.class)) {
            return;
        }

        Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard.equals(this.plugin.getServer().getScoreboardManager().getMainScoreboard())) {
            scoreboard = this.plugin.getServer().getScoreboardManager().getNewScoreboard();
            player.setScoreboard(scoreboard);
        }

        Objective objective = scoreboard.getObjective("showhealth");
        if (objective == null) {
            objective = scoreboard.registerNewObjective("showhealth", "health");
        }

        objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        objective.setDisplayName(ChatColor.RED + "❤");
    }

    private void registerCampProtectionTask(Player player) {
        if (!this.getKit().isSettingEnabled(KitSettingCampProtectionImpl.class)) {
            return;
        }

        MatchCampProtectionTask campProtectionTask = new MatchCampProtectionTask(player);
        campProtectionTask.runTaskTimer(this.plugin, 0L, 20L);
    }

    private void cleanupHealthDisplay() {
        List<Player> playersToCleanup = new ArrayList<>();

        if (getParticipants() != null) {
            getParticipants().stream()
                    .filter(Objects::nonNull)
                    .flatMap(participant -> participant.getPlayers().stream())
                    .filter(Objects::nonNull)
                    .map(gamePlayer -> this.plugin.getServer().getPlayer(gamePlayer.getUuid()))
                    .filter(Objects::nonNull)
                    .forEach(playersToCleanup::add);
        }

        if (getSpectators() != null) {
            getSpectators().stream()
                    .filter(Objects::nonNull)
                    .map(this.plugin.getServer()::getPlayer)
                    .filter(Objects::nonNull)
                    .forEach(playersToCleanup::add);
        }

        playersToCleanup.forEach(player -> {
            Scoreboard scoreboard = player.getScoreboard();
            if (scoreboard != null) {
                Objective objective = scoreboard.getObjective("showhealth");
                if (objective != null) {
                    objective.unregister();
                }
            }
        });
    }

    private void cleanupTasks() {
        this.runnable.cancel();
    }

    public void setupPlayer(Player player) {
        MatchGamePlayer gamePlayer = getGamePlayer(player);
        if (gamePlayer == null) {
            return;
        }

        gamePlayer.setDead(false);
        PlayerUtil.reset(player, true, true);
        this.giveLoadout(player, this.kit);
    }

    public void giveLoadout(Player player, Kit kit) {
        LayoutService layoutService = this.plugin.getService(LayoutService.class);
        ProfileService profileService = this.plugin.getService(ProfileService.class);

        player.getInventory().setArmorContents(InventoryUtil.cloneItemStackArray(kit.getArmor()));

        Profile profile = profileService != null ? profileService.getProfile(player.getUniqueId()) : null;
        ItemStack[] itemsToGive = InventoryUtil.cloneItemStackArray(kit.getItems());

        if (profile == null
                || profile.getProfileData() == null
                || profile.getProfileData().getLayoutData() == null
                || profile.getProfileData().getLayoutData().getLayouts() == null) {
            player.getInventory().setContents(itemsToGive);
        } else {
            List<LayoutData> kitLayouts = profile.getProfileData().getLayoutData().getLayouts().get(kit.getName());
            if (kitLayouts == null || kitLayouts.isEmpty()) {
                player.getInventory().setContents(itemsToGive);
            } else if (kitLayouts.size() == 1) {
                LayoutData singleLayout = kitLayouts.get(0);
                if (singleLayout != null && InventoryUtil.hasAnyItem(singleLayout.getItems())) {
                    itemsToGive = InventoryUtil.cloneItemStackArray(singleLayout.getItems());
                }
                player.getInventory().setContents(itemsToGive);
            } else {
                if (layoutService != null) {
                    layoutService.giveBooks(player, kit.getName());
                } else {
                    player.getInventory().setContents(itemsToGive);
                }
            }
        }

        player.updateInventory();

        this.kit.applyPotionEffects(player);
    }

    public void handleDeath(Player player, EntityDamageEvent.DamageCause cause) {
        MatchGamePlayer gamePlayer = this.getFromAllGamePlayers(player);
        if (gamePlayer == null) {
            return;
        }

        boolean disconnectDeathDuringStarting = this.state == MatchState.STARTING && gamePlayer.isDisconnected();
        if (this.state != MatchState.RUNNING && !disconnectDeathDuringStarting) {
            return;
        }

        CombatService combatService = this.plugin.getService(CombatService.class);
        ProfileService profileService = this.plugin.getService(ProfileService.class);

        GameParticipant<MatchGamePlayer> participant = this.getParticipant(player);
        if (participant == null) {
            return;
        }

        if (participant.isAllEliminated() && !gamePlayer.isDisconnected()) {
            return;
        }

        this.handleParticipant(player, gamePlayer);

        Player killer = combatService.getLastAttacker(player);
        Profile victimProfile = profileService.getProfile(player.getUniqueId());
        Profile killerProfile = (killer != null) ? profileService.getProfile(killer.getUniqueId()) : null;

        if (!gamePlayer.isDisconnected()) {
            this.handleDeathMessages(player, killer, victimProfile, killerProfile, cause);
        }

        this.createSnapshot(player);

        Location deathLocation = player.getLocation().clone();
        player.setVelocity(new Vector(0, 0.8, 0));

        if (this.canEndRound()) {
            this.state = MatchState.ENDING_ROUND;
            this.handleRoundEnd();

            if (this.canEndMatch()) {
                if (player != null && player.isOnline() && !gamePlayer.isDisconnected()) {
                    this.playFinalDeathAnimation(player);
                }

                if (killer != null && player != null && player.isOnline()) {
                    this.handleDeathEffects(player, killer);
                }

                if (player != null && player.isOnline() && !gamePlayer.isDisconnected()) {
                    this.handleFinalDeathVisibility(player, gamePlayer);
                }

                this.state = MatchState.ENDING_MATCH;
            }
            this.runnable.setStage(4);
            return;
        }

        if (handleSpectator(player, victimProfile, participant)) {
            if (killer != null) {
                this.handleDeathEffects(player, killer);
            }
            this.setupSpectatorProfile(player);
            this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> this.addSpectator(player), 1L);
            return;
        }

        if (gamePlayer.isEliminated()) {
            return;
        }

        if (this.shouldHandleRegularRespawn(player)) {
            this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> this.handleRespawn(player), 1L);
        }

        if (!this.shouldHandleRegularRespawn(player)) {
            this.startRespawnProcess(player, deathLocation);
        }
    }

    private boolean handleSpectator(Player player, Profile profile, GameParticipant<MatchGamePlayer> participant) {
        if (profile == null || profile.getMatch() == null || participant == null) {
            return false;
        }

        Kit matchKit = profile.getMatch().getKit();

        MatchGamePlayer gamePlayer = this.getFromAllGamePlayers(player);
        if (gamePlayer == null) {
            return false;
        }

        if (gamePlayer.isDisconnected()) {
            return false;
        }

        return this.shouldBecomeSpectatorForEliminationKit(participant, matchKit, player) || shouldBecomeSpectatorForNonRoundKit(participant, matchKit);
    }

    private boolean shouldBecomeSpectatorForEliminationKit(GameParticipant<MatchGamePlayer> participant, Kit matchKit, Player player) {
        if (!participant.isAllEliminated() && this.hasEliminationBasedKit(matchKit)) {
            MatchGamePlayer gamePlayer = this.getGamePlayer(player);
            return gamePlayer.isEliminated();
        }
        return false;
    }

    private boolean shouldBecomeSpectatorForNonRoundKit(GameParticipant<MatchGamePlayer> participant, Kit matchKit) {
        return !participant.isAllDead() && !this.isRoundBasedKit(matchKit) && !this.hasEliminationBasedKit(matchKit);
    }

    private boolean hasEliminationBasedKit(Kit kit) {
        return kit.isSettingEnabled(KitSettingBed.class) || kit.isSettingEnabled(KitSettingCheckpoint.class) || kit.isSettingEnabled(KitSettingLives.class);
    }

    private boolean isRoundBasedKit(Kit kit) {
        return kit.isSettingEnabled(KitSettingStickFight.class) || kit.isSettingEnabled(KitSettingRounds.class);
    }

    private void handleDeathMessages(Player victim, Player killer, Profile victimProfile, Profile killerProfile, EntityDamageEvent.DamageCause cause) {
        if (this.getKit().isSettingEnabled(KitSettingBoxing.class)) {
            return;
        }

        if (killer == null || killerProfile == null) {
            this.handleDefaultDeathMessages(victim, null, victimProfile);
            return;
        }

        String selectedPackName = killerProfile.getProfileData().getCosmeticData().getSelected(CosmeticType.KILL_MESSAGE);

        if (selectedPackName == null || selectedPackName.equalsIgnoreCase("None")) {
            this.handleDefaultDeathMessages(victim, killer, victimProfile);
            return;
        }

        CosmeticService cosmeticRepository = this.plugin.getService(CosmeticService.class);
        BaseCosmeticRepository<?> repository = cosmeticRepository.getRepository(CosmeticType.KILL_MESSAGE);
        KillMessagePack pack = (KillMessagePack) repository.getCosmetic(selectedPackName);

        if (pack == null) {
            this.handleDefaultDeathMessages(victim, killer, victimProfile);
            return;
        }

        String messageTemplate = pack.getRandomMessage(cause);

        if (messageTemplate != null) {
            String victimColor = String.valueOf(victimProfile.getNameColor());
            String killerColor = String.valueOf(killerProfile.getNameColor());
            String victimName = PlayerDisplayUtil.resolveCurrentNick(victim, victim.getName());
            String killerName = PlayerDisplayUtil.resolveCurrentNick(killer, killer.getName());

            String finalMessage = messageTemplate.replace("{victim}", victimColor + victimName + "&f");
            finalMessage = finalMessage.replace("{killer}", killerColor + killerName + "&f");
            finalMessage = finalMessage.replace("{victim-name-color}", victimColor);
            finalMessage = finalMessage.replace("{victim-color}", victimColor);
            finalMessage = finalMessage.replace("{killer-name-color}", killerColor);
            finalMessage = finalMessage.replace("{killer-color}", killerColor);

            this.notifyAll(this.plugin.getService(LocaleService.class).getString(GameMessagesLocaleImpl.MATCH_DEATH_MESSAGE_CUSTOM).replace("{message}", CC.translate(finalMessage)));
            this.processKillerStatActions(killer);
        } else {
            this.handleDefaultDeathMessages(victim, killer, victimProfile);
        }
    }

    private void handleDefaultDeathMessages(Player victim, Player killer, Profile victimProfile) {
        if (killer == null) {
            String victimName = PlayerDisplayUtil.resolveCurrentNick(victim, victimProfile.getName());
            this.notifyAll(CC.translate(this.plugin.getService(LocaleService.class).getString(GameMessagesLocaleImpl.MATCH_DEATH_MESSAGE_GENERIC)
                    .replace("{player}", victimName)
                    .replace("{name-color}", String.valueOf(victimProfile.getNameColor())))
            );
        } else {
            this.processKillerActions(victim, killer, victimProfile);
        }
    }

    private void processKillerActions(Player victim, Player killer, Profile victimProfile) {
        this.processKillerStatActions(killer);

        ProfileService profileService = this.plugin.getService(ProfileService.class);
        ReflectionService reflectionService = this.plugin.getService(ReflectionService.class);

        Profile killerProfile = profileService.getProfile(killer.getUniqueId());
        String victimName = PlayerDisplayUtil.resolveCurrentNick(victim, victim.getName());
        String killerName = PlayerDisplayUtil.resolveCurrentNick(killer, killer.getName());

        reflectionService.getReflectionService(ActionBarReflectionServiceImpl.class).sendDeathMessage(killer, victim);

        this.notifyAll(this.plugin.getService(LocaleService.class).getString(GameMessagesLocaleImpl.MATCH_DEATH_MESSAGE_GENERIC_KILLER)
                .replace("{victim}", victimProfile.getNameColor() + victimName + "&f")
                .replace("{killer}", killerProfile.getNameColor() + killerName + "&f")
                .replace("{name-color}", String.valueOf(victimProfile.getNameColor()))
                .replace("{victim-name-color}", String.valueOf(victimProfile.getNameColor()))
                .replace("{victim-color}", String.valueOf(victimProfile.getNameColor()))
                .replace("{killer-name-color}", String.valueOf(killerProfile.getNameColor()))
                .replace("{killer-color}", String.valueOf(killerProfile.getNameColor()))
        );
    }

    private void processKillerStatActions(Player killer) {
        GameParticipant<MatchGamePlayer> killerParticipant = getParticipant(killer);
        if (killerParticipant != null) {
            killerParticipant.getLeader().getData().incrementKills();
        }
    }

    private void handleDeathEffects(Player player, Player killer) {
        ProfileService profileService = this.plugin.getService(ProfileService.class);
        Profile profile = profileService.getProfile(killer.getUniqueId());

        String selectedKillEffectName = profile.getProfileData().getCosmeticData().getSelectedKillEffect();
        String selectedSoundEffectName = profile.getProfileData().getCosmeticData().getSelectedSoundEffect();

        this.applyCosmetic(CosmeticType.KILL_EFFECT, selectedKillEffectName, player);
        this.applyCosmetic(CosmeticType.SOUND_EFFECT, selectedSoundEffectName, killer);
    }

    private void playFinalDeathAnimation(Player victim) {
        ReflectionService reflectionService = this.plugin.getService(ReflectionService.class);
        if (reflectionService == null || victim == null) {
            return;
        }

        DeathReflectionServiceImpl deathReflection = reflectionService.getReflectionService(DeathReflectionServiceImpl.class);
        if (deathReflection == null) {
            return;
        }

        Set<Player> viewers = new LinkedHashSet<>();
        this.getParticipants().forEach(participant -> participant.getAllPlayers().forEach(matchGamePlayer -> {
            Player viewer = this.plugin.getServer().getPlayer(matchGamePlayer.getUuid());
            if (viewer != null && viewer.isOnline() && !viewer.getUniqueId().equals(victim.getUniqueId())) {
                viewers.add(viewer);
            }
        }));
        this.getSpectators().stream()
                .map(uuid -> this.plugin.getServer().getPlayer(uuid))
                .filter(Objects::nonNull)
                .filter(Player::isOnline)
                .filter(viewer -> !viewer.getUniqueId().equals(victim.getUniqueId()))
                .forEach(viewers::add);

        if (!viewers.isEmpty()) {
            deathReflection.animateDeath(victim, viewers);
        }
    }

    private void applyCosmetic(CosmeticType cosmeticType, String cosmeticName, Player targetPlayer) {
        if (cosmeticName == null || cosmeticName.equalsIgnoreCase("None")) {
            return;
        }

        CosmeticService cosmeticService = this.plugin.getService(CosmeticService.class);
        if (cosmeticService == null) {
            return;
        }

        switch (cosmeticType) {
            case KILL_EFFECT:
                KillEffectRepository killEffectRepository = cosmeticService.getRepository(CosmeticType.KILL_EFFECT, KillEffectRepository.class);
                if (killEffectRepository == null) {
                    return;
                }

                BaseKillEffect killEffect = killEffectRepository.getCosmetic(cosmeticName);
                if (killEffect == null) {
                    return;
                }

                killEffect.execute(targetPlayer);
                break;

            case SOUND_EFFECT:
                SoundEffectRepository soundEffectRepository = cosmeticService.getRepository(CosmeticType.SOUND_EFFECT, SoundEffectRepository.class);
                if (soundEffectRepository == null) {
                    return;
                }

                BaseSoundEffect soundEffect = soundEffectRepository.getCosmetic(cosmeticName);
                if (soundEffect == null) {
                    return;
                }

                soundEffect.execute(targetPlayer);
                break;

            default:
                Logger.warn("Cosmetic type " + cosmeticType.name() + " is not supported for explicit execution.");
                break;
        }
    }

    public void handleRoundStart() {
        if (this instanceof RoundsMatch && ((RoundsMatch) this).getCurrentRound() > 0) {
            return;
        }
        this.startTime = System.currentTimeMillis();
    }

    public void handleRoundEnd() {
        this.endTime = System.currentTimeMillis();

        this.getParticipants().forEach(participant -> participant.getAllPlayers().forEach(gamePlayer -> {
            Player player = this.plugin.getServer().getOnlinePlayers().stream()
                    .filter(p -> p.getUniqueId().equals(gamePlayer.getUuid()))
                    .findFirst()
                    .orElse(null);

            if (player != null && player.isOnline()) {
                player.getInventory().clear();
                player.getInventory().setArmorContents(null);
                player.updateInventory();

                player.setVelocity(new Vector(0, 0.8, 0));

                this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
                    if (player.isOnline()) {
                        player.setAllowFlight(true);
                        player.setFlying(true);
                    }
                }, 1L);
            }
        }));

        this.handleMatchHistoryData();

        this.getParticipants().forEach(
                participant -> participant.getAllPlayers().forEach(gamePlayer -> {
                    Player player = this.plugin.getServer().getPlayer(gamePlayer.getUuid());
                    if (player != null) {
                        this.createSnapshot(player);
                    }
                })
        );

        SnapshotService snapshotRepository = this.plugin.getService(SnapshotService.class);
        this.snapshots.forEach(snapshotRepository::addSnapshot);
    }

    private void handleMatchHistoryData() {
        if (this.isTeamMatch()) return;

        this.getParticipants().forEach(gameParticipant -> gameParticipant.getAllPlayers().forEach(gamePlayer -> {
            Player player = this.plugin.getServer().getPlayer(gamePlayer.getUuid());
            if (player == null) return;

            Profile profile = this.plugin.getService(ProfileService.class).getProfile(player.getUniqueId());

            UUID winnerID;
            UUID loserID;

            if (gamePlayer.isDead()) {
                winnerID = this.getOpponent(player).getLeader().getUuid();
                loserID = gamePlayer.getUuid();
            } else {
                winnerID = gamePlayer.getUuid();
                loserID = this.getOpponent(player).getLeader().getUuid();
            }

            String arenaName;
            if (this.arena instanceof StandAloneArena) {
                arenaName = ((StandAloneArena) this.arena).getOriginalArenaName();
            } else {
                arenaName = this.arena.getName();
            }

            MatchData matchData = new MatchDataSolo(
                    this.getKit().getName(),
                    arenaName,
                    winnerID,
                    loserID
            );

            if (this.isRanked()) {
                matchData.setRanked(true);
            }

            profile.getProfileData().getPreviousMatches().add(matchData);
        }));
    }

    public void createSnapshot(Player player) {
        if (this.snapshots.stream().anyMatch(snapshot -> snapshot.getUuid().equals(player.getUniqueId()))) {
            return;
        }

        MatchGamePlayer gamePlayer = this.getGamePlayer(player);
        if (gamePlayer == null || gamePlayer.isDisconnected()) {
            return;
        }

        Snapshot snapshot = new Snapshot(player, !gamePlayer.isDead());
        snapshot.setOpponent(this.getOpponent(player).getLeader().getUuid());
        snapshot.setLongestCombo(gamePlayer.getData().getLongestCombo());
        snapshot.setTotalHits(gamePlayer.getData().getHits());
        snapshot.setThrownPotions(gamePlayer.getData().getThrownPotions());
        snapshot.setMissedPotions(gamePlayer.getData().getMissedPotions());
        snapshot.setCriticalHits(gamePlayer.getData().getCriticalHits());
        snapshot.setBlockedHits(gamePlayer.getData().getBlockedHits());
        snapshot.setWTaps(gamePlayer.getData().getWTaps());

        this.snapshots.add(snapshot);
    }

    public void addSpectator(Player player) {
        if (this.getGamePlayer(player) == null) {
            if (this.getState() == MatchState.ENDING_MATCH) {
                player.sendMessage(CC.translate("&cThis match has already ended."));
                return;
            }

            this.setupSpectatorProfile(player);
            this.spectators.add(player.getUniqueId());
        }

        VisibilityService visibilityService = this.plugin.getService(VisibilityService.class);
        HotbarService hotbarService = this.plugin.getService(HotbarService.class);

        visibilityService.updateVisibility(player);
        hotbarService.applyHotbarItems(player);

        if (this.arena.getCenter() == null) {
            player.sendMessage(CC.translate("&cArena is not configured for spectators."));
            return;
        }

        player.setAllowFlight(true);
        player.setFlying(true);

        ListenerUtil.teleportAndClearSpawn(player, this.arena.getCenter());

        ProfileService profileService = this.plugin.getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());
        this.notifyAll("&b" + profile.getFancyName() + " &eis now spectating the match.");
    }

    public void removeSpectator(Player player, boolean notify) {
        ProfileService profileService = this.plugin.getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());
        profile.setState(ProfileState.LOBBY);
        profile.setMatch(null);

        VisibilityService visibilityService = this.plugin.getService(VisibilityService.class);

        visibilityService.updateVisibility(player);

        player.setAllowFlight(false);
        player.setFlying(false);

        this.resetPlayerState(player);
        this.teleportPlayerToSpawn(player);
        this.spectators.remove(player.getUniqueId());

        if (notify) {
            this.notifyAll("&b" + profile.getFancyName() + " &tfis no longer spectating the match.");
        }
    }

    public void startRespawnProcess(Player player, Location deathLocation) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);

        player.addPotionEffects(Arrays.asList(
                new PotionEffect(PotionEffectType.BLINDNESS, 45, 3),
                new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 3)
        ));

        MatchGamePlayer gamePlayer = this.getGamePlayer(player);
        if (gamePlayer != null) {
            gamePlayer.setDead(false);
        }

        Location hoverLocation = deathLocation.clone().add(0, 2, 0);
        ListenerUtil.teleportAndClearSpawn(player, hoverLocation);

        player.setAllowFlight(true);
        player.setFlying(true);
        player.setVelocity(new Vector(0, 0.4, 0));

        org.bukkit.Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
            if (!onlinePlayer.getUniqueId().equals(player.getUniqueId())) {
                onlinePlayer.hidePlayer(player);
            }
        });

        new MatchRespawnTask(player, this, 3).runTaskTimer(this.plugin, 0L, 20L);
    }

    protected boolean shouldHandleRegularRespawn(Player player) {
        return true;
    }

    public void handleParticipant(Player player, MatchGamePlayer gamePlayer) {
        gamePlayer.setDead(true);
    }

    public void notifyParticipants(String message) {
        this.getParticipants().forEach(gameParticipant -> gameParticipant.getPlayers().forEach(uuid -> {
            Player player = this.plugin.getServer().getPlayer(uuid.getUuid());
            if (player != null) {
                player.sendMessage(CC.translate(message));
            }
        }));
    }

    public void notifySpectators(String message) {
        this.spectators.stream()
                .map(uuid -> this.plugin.getServer().getPlayer(uuid))
                .filter(Objects::nonNull)
                .forEach(player -> player.sendMessage(CC.translate(message)));
    }

    public void notifyAll(String message) {
        this.notifyParticipants(message);
        this.notifySpectators(message);
    }

    public void broadcastAndStopSpectating() {
        List<String> firstThreeSpectatorNames = new ArrayList<>();
        this.spectators.stream()
                .map(uuid -> this.plugin.getServer().getPlayer(uuid))
                .filter(Objects::nonNull)
                .limit(3)
                .forEach(player -> firstThreeSpectatorNames.add(PlayerDisplayUtil.resolveCurrentNick(player, player.getName())));

        List<Integer> remainingSpectators = new ArrayList<>();
        this.spectators.stream()
                .map(uuid -> this.plugin.getServer().getPlayer(uuid))
                .filter(Objects::nonNull)
                .skip(3)
                .forEach(player -> remainingSpectators.add(player.getEntityId()));

        this.notifyAll(this.plugin.getService(LocaleService.class).getString(GameMessagesLocaleImpl.MATCH_ENDED_SPECTATORS_LIST)
                .replace("{spectators}", String.join(", ", firstThreeSpectatorNames))
                .replace("{more_count}", String.valueOf(remainingSpectators.size())));

        this.spectators.forEach(uuid -> {
            Player player = this.plugin.getServer().getPlayer(uuid);
            if (player != null) {
                this.removeSpectator(player, false);
            }
        });
    }

    public String getDuration() {
        if (this.state == MatchState.STARTING) {
            return TimeUtil.getFormattedElapsedTime(this.getElapsedTime());
        } else if (this.state == MatchState.ENDING_MATCH) {
            return TimeUtil.getFormattedElapsedTime(this.endTime - this.startTime);
        } else {
            return TimeUtil.getFormattedElapsedTime(this.getElapsedTime());
        }
    }

    public long getElapsedTime() {
        return System.currentTimeMillis() - this.startTime;
    }

    public MatchGamePlayer getGamePlayer(Player player) {
        return this.getParticipants().stream()
                .map(GameParticipant::getPlayers)
                .flatMap(List::stream)
                .filter(gamePlayer -> gamePlayer.getUuid().equals(player.getUniqueId()))
                .findFirst()
                .orElse(null);
    }

    public MatchGamePlayer getFromAllGamePlayers(Player player) {
        return this.getParticipants().stream()
                .map(GameParticipant::getAllPlayers)
                .flatMap(List::stream)
                .filter(gamePlayer -> gamePlayer.getUuid().equals(player.getUniqueId()))
                .findFirst()
                .orElse(null);
    }

    public GameParticipant<MatchGamePlayer> getParticipant(Player player) {
        return this.getParticipants().stream()
                .filter(gameParticipant -> gameParticipant.containsPlayer(player.getUniqueId()))
                .findFirst()
                .orElse(null);
    }

    public GameParticipant<MatchGamePlayer> getOpponent(Player player) {
        GameParticipant<MatchGamePlayer> participant = this.getParticipant(player);
        if (participant == null) {
            return null;
        }

        return this.getParticipants().stream()
                .filter(p -> !p.equals(participant))
                .findFirst()
                .orElse(null);
    }

    public void playSound(Sound sound) {
        this.getParticipants().forEach(gameParticipant -> gameParticipant.getPlayers().forEach(uuid -> {
            Player player = this.plugin.getServer().getPlayer(uuid.getUuid());
            if (player != null) {
                SoundUtil.playCustomSound(player, sound, 1.0F, 1.0F);
            }
        }));

        this.getSpectators().forEach(uuid -> {
            Player player = this.plugin.getServer().getPlayer(uuid);
            if (player != null) {
                SoundUtil.playCustomSound(player, sound, 1.0F, 1.0F);
            }
        });
    }

    public void playSound(GameParticipant<MatchGamePlayer> participant, Sound sound) {
        participant.getPlayers().forEach(uuid -> {
            Player player = this.plugin.getServer().getPlayer(uuid.getUuid());
            if (player != null) {
                SoundUtil.playCustomSound(player, sound, 1.0F, 1.0F);
            }
        });
    }

    public void sendTitle(String title, String subtitle) {
        ReflectionService reflectionService = this.plugin.getService(ReflectionService.class);
        this.getParticipants().forEach(gameParticipant -> gameParticipant.getPlayers().forEach(uuid -> {
            Player player = this.plugin.getServer().getPlayer(uuid.getUuid());
            if (player != null) {
                reflectionService.getReflectionService(TitleReflectionServiceImpl.class).sendTitle(
                        player,
                        title,
                        subtitle
                );
            }
        }));

        this.getSpectators().forEach(uuid -> {
            Player player = this.plugin.getServer().getPlayer(uuid);
            if (player != null) {
                reflectionService.getReflectionService(TitleReflectionServiceImpl.class).sendTitle(
                        player,
                        title,
                        subtitle
                );
            }
        });
    }

    public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut, boolean spectators) {
        ReflectionService reflectionService = this.plugin.getService(ReflectionService.class);
        this.getParticipants().forEach(gameParticipant -> gameParticipant.getPlayers().forEach(uuid -> {
            Player player = this.plugin.getServer().getPlayer(uuid.getUuid());
            if (player != null) {
                reflectionService.getReflectionService(TitleReflectionServiceImpl.class).sendTitle(
                        player,
                        title,
                        subtitle,
                        fadeIn, stay, fadeOut
                );
            }
        }));

        if (spectators) {
            this.getSpectators().forEach(uuid -> {
                Player player = this.plugin.getServer().getPlayer(uuid);
                if (player != null) {
                    reflectionService.getReflectionService(TitleReflectionServiceImpl.class).sendTitle(
                            player,
                            title,
                            subtitle,
                            fadeIn, stay, fadeOut
                    );
                }
            });
        }
    }

    public void sendMessage(List<String> messages) {
        messages.forEach(this::sendMessage);
    }

    public void sendMessage(String message) {
        this.getParticipants().forEach(gameParticipant -> gameParticipant.getPlayers().forEach(uuid -> {
            Player player = this.plugin.getServer().getPlayer(uuid.getUuid());
            if (player != null) {
                player.sendMessage(CC.translate(message));
            }
        }));

        this.getSpectators().forEach(uuid -> {
            Player player = this.plugin.getServer().getPlayer(uuid);
            if (player != null) {
                player.sendMessage(CC.translate(message));
            }
        });
    }

    public boolean isInSameTeam(Player attacker, Player victim) {
        if (attacker == null || victim == null || attacker.getUniqueId().equals(victim.getUniqueId())) {
            return false;
        }

        if (!this.isTeamMatch()) {
            return false;
        }

        GameParticipant<MatchGamePlayer> attackerParticipant = this.getParticipant(attacker);
        GameParticipant<MatchGamePlayer> victimParticipant = this.getParticipant(victim);

        if (attackerParticipant == null || victimParticipant == null) {
            return false;
        }

        return attackerParticipant == victimParticipant;
    }

    public void denyPlayerMovement(List<GameParticipant<MatchGamePlayer>> participants) {
        if (participants.size() == 2) {
            GameParticipant<?> participantA = participants.get(0);
            GameParticipant<?> participantB = participants.get(1);

            Location locationA = this.arena.getPos1();
            Location locationB = this.arena.getPos2();

            for (GamePlayer gamePlayer : participantA.getPlayers()) {
                Player participantPlayer = gamePlayer.getTeamPlayer();
                if (participantPlayer != null) {
                    this.teleportBackIfMoved(participantPlayer, locationA);
                }
            }

            for (GamePlayer gamePlayer : participantB.getPlayers()) {
                Player participantPlayer = gamePlayer.getTeamPlayer();
                if (participantPlayer != null) {
                    this.teleportBackIfMoved(participantPlayer, locationB);
                }
            }
        }
    }

    protected void teleportBackIfMoved(Player player, Location location) {
        Location playerLocation = player.getLocation();

        double deltaX = Math.abs(playerLocation.getX() - location.getX());
        double deltaZ = Math.abs(playerLocation.getZ() - location.getZ());

        if (deltaX > 0.1 || deltaZ > 0.1) {
            player.teleport(new Location(location.getWorld(), location.getX(), playerLocation.getY(), location.getZ(), playerLocation.getYaw(), playerLocation.getPitch()));
        }
    }

    private void teleportPlayerToSpawn(Player player) {
        if (player == null) return;

        HotbarService hotbarService = this.plugin.getService(HotbarService.class);
        SpawnService spawnService = this.plugin.getService(SpawnService.class);

        spawnService.teleportToSpawn(player);
        hotbarService.applyHotbarItems(player);
    }

    private void resetPlayerState(Player player) {
        player.setFireTicks(0);
        player.updateInventory();
        PlayerUtil.reset(player, false, true);
    }

    public void addBlockToPlacedBlocksMap(BlockState blockState, Location location) {
        this.placedBlocks.put(blockState, location);
    }

    public void removeBlockFromPlacedBlocksMap(BlockState blockState, Location location) {
        this.placedBlocks.remove(blockState, location);
    }

    public void addBlockToBrokenBlocksMap(BlockState blockState, Location location) {
        if (this.placedBlocks.containsValue(location)) {
            this.placedBlocks.values().remove(location);
        } else {
            this.brokenBlocks.put(blockState, location);
        }
    }

    @SuppressWarnings("deprecation")
    public void resetBlockChanges() {
        if (this.getKit().isSettingEnabled(KitSettingRaiding.class)) {
            Arena arena = this.getArena();
            Location pos1 = arena.getPos1();
            Location pos2 = arena.getPos2();

            for (int x = pos1.getBlockX(); x <= pos2.getBlockX(); x++) {
                for (int z = pos1.getBlockZ(); z <= pos2.getBlockZ(); z++) {
                    for (int y = pos1.getBlockY(); y <= pos2.getBlockY(); y++) {
                        Location location = new Location(pos1.getWorld(), x, y, z);
                        Block block = location.getBlock();
                        if (ListenerUtil.isInteractiveBlock(block.getType())) {
                            BlockState originalState = block.getState();
                            if (originalState.getType() == Material.AIR) {
                                continue;
                            }
                            this.brokenBlocks.put(originalState, location);
                            block.setType(Material.AIR);
                        }
                    }
                }
            }
        }

        this.removePlacedBlocks();

        for (Map.Entry<BlockState, Location> entry : this.brokenBlocks.entrySet()) {
            Location location = entry.getValue();
            BlockState originalState = entry.getKey();

            Block block = location.getBlock();
            block.setType(originalState.getType());
            block.setData(originalState.getRawData());
        }

        this.brokenBlocks.clear();
    }

    public void removePlacedBlocks() {
        for (Map.Entry<BlockState, Location> entry : this.placedBlocks.entrySet()) {
            Location location = entry.getValue();
            location.getBlock().setType(Material.AIR);
        }

        this.placedBlocks.clear();
    }

    private void sendPlayerVersusPlayerMessage() {
        LocaleService localeService = this.plugin.getService(LocaleService.class);

        GameParticipant<MatchGamePlayer> participantA = this.getParticipants().get(0);
        GameParticipant<MatchGamePlayer> participantB = this.getParticipants().get(1);

        if (this.isTeamMatch()) {
            if (localeService.getBoolean(GameMessagesLocaleImpl.MATCH_PLAYER_VS_PLAYER_TEAM_ENABLED_BOOLEAN)) {
                int teamSizeA = participantA.getPlayerSize();
                int teamSizeB = participantB.getPlayerSize();

                List<String> message = localeService.getStringList(GameMessagesLocaleImpl.MATCH_PLAYER_VS_PLAYER_TEAM_FORMAT);
                for (String line : message) {
                    String formatted = line
                            .replace("{teamA-leader}", participantA.getLeader().getUsername())
                            .replace("{teamA-size}", String.valueOf(teamSizeA))
                            .replace("{teamB-leader}", participantB.getLeader().getUsername())
                            .replace("{teamB-size}", String.valueOf(teamSizeB));
                    this.sendMessage(formatted);
                }
            }
        } else {
            if (localeService.getBoolean(GameMessagesLocaleImpl.MATCH_PLAYER_VS_PLAYER_SOLO_ENABLED_BOOLEAN)) {
                List<String> message = localeService.getStringList(GameMessagesLocaleImpl.MATCH_PLAYER_VS_PLAYER_SOLO_FORMAT);
                for (String line : message) {
                    String formatted = line
                            .replace("{playerA}", participantA.getLeader().getUsername())
                            .replace("{playerB}", participantB.getLeader().getUsername());
                    this.sendMessage(formatted);
                }
            }
        }
    }

    private void handleMatchTasks() {
        this.runnable = new MatchTask(this);
        this.runnable.runTaskTimer(this.plugin, 0L, 20L);

        if (this.getKit().isSettingEnabled(KitSettingPlatformDecay.class) && this.getArena() instanceof StandAloneArena) {
            PlatformDecayTask.start(this);
        }
    }

    private void updatePlayerProfileForMatch(Player player) {
        ProfileService profileService = this.plugin.getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());
        profile.setState(ProfileState.PLAYING);
        profile.setMatch(this);
    }

    private void updatePlayerProfileForLobby(Player player) {
        ProfileService profileService = this.plugin.getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());

        profile.setState(ProfileState.LOBBY);
        profile.setMatch(null);
    }

    private void setupSpectatorProfile(Player player) {
        ProfileService profileService = this.plugin.getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());
        profile.setState(ProfileState.SPECTATING);
        profile.setMatch(this);

        PlayerUtil.reset(player, false, true);
    }

    private void handleFinalDeathVisibility(Player player, MatchGamePlayer gamePlayer) {
        gamePlayer.setEliminated(true);
        this.setupSpectatorProfile(player);

        VisibilityService visibilityService = this.plugin.getService(VisibilityService.class);
        if (visibilityService != null) {
            visibilityService.updateVisibility(player);
        }
    }

    public void calculateCoinReward(Player player) {
        ProfileService profileService = this.plugin.getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());

        MatchGamePlayerData data = this.getGamePlayer(player).getData();
        int kills = data.getKills();
        int deaths = data.getDeaths();
        int missedPotions = data.getMissedPotions();

        double score = 0;

        score += kills * 15;
        score -= deaths * 10;

        int excessPotions = Math.max(missedPotions - 20, 0);
        score -= excessPotions * 1.5;

        int performanceScore = (int) Math.max(0, Math.min(100, score));
        profile.getProfileData().incrementCoins(performanceScore);
    }

    public void sendRewardMessage(Player player) {
        ProfileService profileService = this.plugin.getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());
        int coins = profile.getProfileData().getCoins();

        player.sendMessage(CC.translate(" &7(&a+&b" + coins + "&f&7)"));
    }
}