package live.pvphard.practice.feature.match.internal.types;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.adapter.core.kaoscore.KaosCoreBridge;
import live.pvphard.practice.common.InventoryUtil;
import live.pvphard.practice.common.ListenerUtil;
import live.pvphard.practice.common.PlayerDisplayUtil;
import live.pvphard.practice.common.PlayerUtil;
import live.pvphard.practice.common.elo.EloCalculator;
import live.pvphard.practice.common.elo.EloResult;
import live.pvphard.practice.common.elo.OldEloResult;
import live.pvphard.practice.common.logger.Logger;
import live.pvphard.practice.common.reflect.ReflectionService;
import live.pvphard.practice.common.reflect.internal.types.TitleReflectionServiceImpl;
import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.locale.LocaleService;
import live.pvphard.practice.core.locale.internal.impl.VisualsLocaleImpl;
import live.pvphard.practice.core.locale.internal.impl.message.GameMessagesLocaleImpl;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.data.types.ProfileRankedKitData;
import live.pvphard.practice.core.profile.data.types.ProfileUnrankedKitData;
import live.pvphard.practice.core.profile.progress.PlayerProgress;
import live.pvphard.practice.core.profile.progress.ProgressService;
import live.pvphard.practice.feature.arena.Arena;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.feature.kit.raiding.BaseRaidingService;
import live.pvphard.practice.feature.kit.setting.types.mechanic.KitSettingDropItemsImpl;
import live.pvphard.practice.feature.kit.setting.types.mode.KitSettingRaiding;
import live.pvphard.practice.feature.kit.setting.types.mode.KitSettingRespawnTimer;
import live.pvphard.practice.feature.layout.data.LayoutData;
import live.pvphard.practice.feature.match.Match;
import live.pvphard.practice.feature.match.MatchState;
import live.pvphard.practice.feature.match.internal.MatchExperienceCalculator;
import live.pvphard.practice.feature.match.model.BaseRaiderRole;
import live.pvphard.practice.feature.match.model.GameParticipant;
import live.pvphard.practice.feature.match.model.MatchGamePlayerData;
import live.pvphard.practice.feature.match.model.internal.MatchGamePlayer;
import live.pvphard.practice.feature.match.utility.MatchUtility;
import live.pvphard.practice.feature.queue.Queue;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Remi
 * @project Hard
 * @date 5/21/2024
 */
@Getter
@Setter
public class DefaultMatch extends Match {
    private static final ChatColor[] TEAM_COLOR_POOL = {
            ChatColor.BLACK,
            ChatColor.DARK_BLUE,
            ChatColor.DARK_GREEN,
            ChatColor.DARK_AQUA,
            ChatColor.DARK_RED,
            ChatColor.DARK_PURPLE,
            ChatColor.GOLD,
            ChatColor.BLUE,
            ChatColor.GREEN,
            ChatColor.AQUA,
            ChatColor.RED,
            ChatColor.LIGHT_PURPLE,
            ChatColor.YELLOW,
            ChatColor.WHITE
    };

    private final GameParticipant<MatchGamePlayer> participantA;
    private final GameParticipant<MatchGamePlayer> participantB;

    public final ChatColor teamAColor;
    public final ChatColor teamBColor;

    private GameParticipant<MatchGamePlayer> winner;
    private GameParticipant<MatchGamePlayer> loser;

    /**
     * Constructor for the MatchRegularImpl class.
     *
     * @param queue        The queue of the match.
     * @param kit          The kit of the match.
     * @param arena        The arena of the match.
     * @param ranked       Whether the match is ranked or not.
     * @param participantA The first participant.
     * @param participantB The second participant.
     */
    public DefaultMatch(Queue queue, Kit kit, Arena arena, boolean ranked, GameParticipant<MatchGamePlayer> participantA, GameParticipant<MatchGamePlayer> participantB) {
        super(queue, kit, arena, ranked);
        this.participantA = participantA;
        this.participantB = participantB;
        this.teamAColor = pickRandomTeamColor(null);
        this.teamBColor = pickRandomTeamColor(this.teamAColor);
    }

    @Override
    public void setupPlayer(Player player) {
        super.setupPlayer(player);
        this.applyColorKit(player);

        Location spawnLocation = this.getParticipantA().containsPlayer(player.getUniqueId()) ? getArena().getPos1() : getArena().getPos2();
        player.teleport(spawnLocation);

        if (this.getKit().isSettingEnabled(KitSettingRaiding.class)) {
            this.determineRolesAndGiveKit(player);
        }
    }

    @Override
    public List<GameParticipant<MatchGamePlayer>> getParticipants() {
        return Arrays.asList(getParticipantA(), getParticipantB());
    }

    /**
     * Get the team color of a participant.
     *
     * @param participant The participant to get the team color of.
     * @return The team color of the participant.
     */
    public ChatColor getTeamColor(GameParticipant<MatchGamePlayer> participant) {
        return participant == this.getParticipantA() ? this.teamAColor : this.teamBColor;
    }

    /**
     * Applies the wool color to the player based on their team.
     *
     * @param player The player to apply the wool color to.
     */
    public void applyColorKit(Player player) {
        GameParticipant<MatchGamePlayer> participant = this.getParticipant(player);
        if (participant == null) {
            return;
        }

        final ChatColor participantColor = this.getTeamColor(participant);
        final InventoryUtil.TeamColor colorToApply = InventoryUtil.TeamColor.fromChatColor(participantColor);

        participant.getPlayers().stream()
                .map(MatchGamePlayer::getTeamPlayer)
                .filter(p -> p != null && p.isOnline())
                .forEach(teamPlayer -> InventoryUtil.applyTeamColorToInventory(teamPlayer, colorToApply));
    }

    private static ChatColor pickRandomTeamColor(ChatColor excludedColor) {
        ChatColor selected = TEAM_COLOR_POOL[ThreadLocalRandom.current().nextInt(TEAM_COLOR_POOL.length)];
        while (excludedColor != null && selected == excludedColor) {
            selected = TEAM_COLOR_POOL[ThreadLocalRandom.current().nextInt(TEAM_COLOR_POOL.length)];
        }
        return selected;
    }

    @Override
    protected boolean shouldHandleRegularRespawn(Player player) {
        return !this.getKit().isSettingEnabled(KitSettingRespawnTimer.class);
    }

    @Override
    public void handleRoundEnd() {
        final boolean teamADead = this.getParticipantA().isAllEliminated() || this.getParticipantA().isAllDead();
        final GameParticipant<MatchGamePlayer> winner = teamADead ? this.getParticipantB() : this.getParticipantA();
        final GameParticipant<MatchGamePlayer> loser = teamADead ? this.getParticipantA() : this.getParticipantB();

        this.winner = winner;
        this.loser = loser;

        broadcastMatchOutcome(winner, loser);
        processStatistics(winner, loser);

        if (!this.getSpectators().isEmpty()) {
            this.broadcastAndStopSpectating();
        }

        super.handleRoundEnd();
    }

    /**
     * Handles all player-facing messages at the end of a match, including titles and results.
     */
    private void broadcastMatchOutcome(GameParticipant<MatchGamePlayer> winner, GameParticipant<MatchGamePlayer> loser) {
        this.sendVictory(winner);
        this.sendDefeat(loser, winner);

        if (this.isTeamMatch()) {
            MatchUtility.sendConjoinedMatchResult(this, winner, loser);
        } else {
            MatchGamePlayer winnerPlayer = winner.getLeader();
            MatchGamePlayer loserPlayer = loser.getLeader();
            String winnerName = PlayerDisplayUtil.resolveTagColoredNick(winnerPlayer.getTeamPlayer(), winnerPlayer.getUsername());
            String loserName = PlayerDisplayUtil.resolveTagColoredNick(loserPlayer.getTeamPlayer(), loserPlayer.getUsername());
            MatchUtility.sendMatchResult(
                    this,
                    winnerName,
                    loserName,
                    winnerPlayer.getUuid(),
                    loserPlayer.getUuid()
            );
        }
    }

    /**
     * Processes all backend statistics if the match is configured to affect them.
     */
    private void processStatistics(GameParticipant<MatchGamePlayer> winner, GameParticipant<MatchGamePlayer> loser) {
        if (!this.isAffectStatistics()) {
            return;
        }

        handleMatchData(winner, loser); // 9 wins, increases to 10, as the player won
        rewardExperience(winner);

        if (!this.isRanked()) {
            this.sendProgressToWinner(winner.getLeader().getTeamPlayer());
        }
    }

    private void rewardExperience(GameParticipant<MatchGamePlayer> winner) {
        Player winnerPlayer = winner != null ? winner.getLeader().getTeamPlayer() : null;
        if (winnerPlayer == null) {
            return;
        }

        Profile winnerProfile = HardPractice.getInstance().getService(ProfileService.class).getProfile(winnerPlayer.getUniqueId());
        if (winnerProfile == null || winnerProfile.getProfileData() == null) {
            return;
        }

        int streak = Math.max(0, winnerProfile.getProfileData().getWinStreak());
        int gainedXp = MatchExperienceCalculator.calculateWinXp(this.isRanked(), streak);

        winnerProfile.getProfileData().addExperience(gainedXp);
        winnerProfile.save();
        winnerPlayer.sendMessage(CC.translate("&b+" + gainedXp + " XP &7(streak: &f" + streak + "&7)"));
    }

    /**
     * Routes to the correct statistics handling method based on the match type.
     *
     * @param winner The winning participant.
     * @param loser  The losing participant.
     */
    private void handleMatchData(GameParticipant<MatchGamePlayer> winner, GameParticipant<MatchGamePlayer> loser) {
        if (this.isTeamMatch()) {
            return;
        }

        if (this.isRanked()) {
            updateRankedStats(winner, loser);
        } else {
            updateUnrankedStats(winner, loser);
        }
    }

    /**
     * Updates player profiles and Elo for a ranked match.
     */
    private void updateRankedStats(GameParticipant<MatchGamePlayer> winner, GameParticipant<MatchGamePlayer> loser) {
        OldEloResult result = this.getOldEloResult(winner, loser);
        EloResult eloResult = this.getEloResult(result.getOldWinnerElo(), result.getOldLoserElo());
        Player winnerPlayer = winner.getLeader().getTeamPlayer();
        Player loserPlayer = loser.getLeader().getTeamPlayer();
        int winnerDelta = Math.max(0, eloResult.getNewWinnerElo() - result.getOldWinnerElo());
        int loserDelta = Math.max(0, result.getOldLoserElo() - eloResult.getNewLoserElo());

        this.handleWinner(eloResult.getNewWinnerElo(), winner);
        this.handleLoser(eloResult.getNewLoserElo(), loser);
        this.updateClanStats(winnerPlayer, loserPlayer, true, winnerDelta, loserDelta);

        this.sendEloResult(
                PlayerDisplayUtil.resolveTagColoredNick(winnerPlayer, winner.getLeader().getUsername()),
                PlayerDisplayUtil.resolveTagColoredNick(loserPlayer, loser.getLeader().getUsername()),
                result.getOldWinnerElo(),
                result.getOldLoserElo(),
                eloResult.getNewWinnerElo(),
                eloResult.getNewLoserElo()
        );
    }

    /**
     * Updates player profiles with wins/losses for an unranked match.
     */
    private void updateUnrankedStats(GameParticipant<MatchGamePlayer> winner, GameParticipant<MatchGamePlayer> loser) {
        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        String kitName = this.getKit() != null ? this.getKit().getName() : null;
        if (kitName == null || kitName.trim().isEmpty()) {
            return;
        }

        Profile winnerProfile = profileService.getProfile(winner.getLeader().getUuid());
        if (winnerProfile == null || winnerProfile.getProfileData() == null || winnerProfile.getProfileData().getUnrankedKitData() == null) {
            return;
        }

        ProfileUnrankedKitData winnerKitData = winnerProfile.getProfileData()
                .getUnrankedKitData()
                .computeIfAbsent(kitName, key -> new ProfileUnrankedKitData());
        winnerKitData.incrementWins();
        winnerProfile.getProfileData().incrementUnrankedWins();
        winnerProfile.getProfileData().incrementWinStreak();
        winnerProfile.getProfileData().determineTitles();

        Profile loserProfile = profileService.getProfile(loser.getLeader().getUuid());
        if (loserProfile == null || loserProfile.getProfileData() == null || loserProfile.getProfileData().getUnrankedKitData() == null) {
            return;
        }

        ProfileUnrankedKitData loserKitData = loserProfile.getProfileData()
                .getUnrankedKitData()
                .computeIfAbsent(kitName, key -> new ProfileUnrankedKitData());
        loserKitData.incrementLosses();
        loserProfile.getProfileData().incrementUnrankedLosses();
        loserProfile.getProfileData().resetWinStreak();

        winnerProfile.save();
        loserProfile.save();

        this.updateClanStats(winner.getLeader().getTeamPlayer(), loser.getLeader().getTeamPlayer(), false, 0, 0);
    }

    /**
     * Sends the victory title to the winning participant.
     *
     * @param winner The winning participant.
     */
    private void sendVictory(GameParticipant<MatchGamePlayer> winner) {
        LocaleService localeService = this.plugin.getService(LocaleService.class);


        if (localeService.getBoolean(VisualsLocaleImpl.TITLE_MATCH_RESPAWNED_ENABLED_BOOLEAN)) {
            String winnerName = PlayerDisplayUtil.resolveTagColoredNick(
                    winner.getLeader().getTeamPlayer(),
                    winner.getLeader().getUsername()
            );
            String header = localeService.getString(VisualsLocaleImpl.TITLE_MATCH_VICTORY_HEADER).replace("{winner}", winnerName);
            String footer = localeService.getString(VisualsLocaleImpl.TITLE_MATCH_VICTORY_FOOTER).replace("{winner}", winnerName);

            int fadeIn = localeService.getInt(VisualsLocaleImpl.TITLE_MATCH_VICTORY_FADE_IN);
            int stay = localeService.getInt(VisualsLocaleImpl.TITLE_MATCH_VICTORY_STAY);
            int fadeOut = localeService.getInt(VisualsLocaleImpl.TITLE_MATCH_VICTORY_FADEOUT);

            winner.getPlayers().forEach(matchGamePlayer -> {
                Player player = this.plugin.getServer().getPlayer(matchGamePlayer.getUuid());
                if (player != null && player.isOnline()) {
                    this.plugin.getService(ReflectionService.class).getReflectionService(TitleReflectionServiceImpl.class).sendTitle(
                            player,
                            header,
                            footer,
                            fadeIn, stay, fadeOut
                    );
                }
            });
        }
    }

    /**
     * Sends the defeat title to the losing participant.
     *
     * @param loser The losing participant.
     */
    private void sendDefeat(GameParticipant<MatchGamePlayer> loser, GameParticipant<MatchGamePlayer> winner) {
        LocaleService localeService = this.plugin.getService(LocaleService.class);

        if (localeService.getBoolean(VisualsLocaleImpl.TITLE_MATCH_DEFEAT_ENABLED_BOOLEAN)) {
            String winnerName = PlayerDisplayUtil.resolveTagColoredNick(
                    winner.getLeader().getTeamPlayer(),
                    winner.getLeader().getUsername()
            );
            String header = localeService.getString(VisualsLocaleImpl.TITLE_MATCH_DEFEAT_HEADER).replace("{winner}", winnerName);
            String footer = localeService.getString(VisualsLocaleImpl.TITLE_MATCH_DEFEAT_FOOTER).replace("{winner}", winnerName);

            int fadeIn = localeService.getInt(VisualsLocaleImpl.TITLE_MATCH_DEFEAT_FADE_IN);
            int stay = localeService.getInt(VisualsLocaleImpl.TITLE_MATCH_DEFEAT_STAY);
            int fadeOut = localeService.getInt(VisualsLocaleImpl.TITLE_MATCH_DEFEAT_FADEOUT);

            loser.getPlayers().forEach(matchGamePlayer -> {
                Player player = this.plugin.getServer().getPlayer(matchGamePlayer.getUuid());
                if (player != null && player.isOnline()) {
                    this.plugin.getService(ReflectionService.class).getReflectionService(TitleReflectionServiceImpl.class).sendTitle(
                            player,
                            header,
                            footer,
                            fadeIn, stay, fadeOut
                    );
                }
            });
        }
    }

    /**
     * Sends the elo result message.
     *
     * @param winnerName   The name of the winner.
     * @param loserName    The name of the loser.
     * @param oldEloWinner The old elo of the winner.
     * @param oldEloLoser  The old elo of the loser.
     * @param newEloWinner The new elo of the winner.
     * @param newEloLoser  The new elo of the loser.
     */
    public void sendEloResult(String winnerName, String loserName, int oldEloWinner, int oldEloLoser, int newEloWinner, int newEloLoser) {
        LocaleService localeService = this.plugin.getService(LocaleService.class);
        if (localeService.getBoolean(GameMessagesLocaleImpl.MATCH_ENDED_MATCH_RESULT_ELO_CHANGES_ENABLED_BOOLEAN)) {
            List<String> list = localeService.getStringList(GameMessagesLocaleImpl.MATCH_ENDED_MATCH_RESULT_ELO_CHANGES_FORMAT);

            list.replaceAll(string -> string
                    .replace("{winner}", winnerName)
                    .replace("{loser}", loserName)
                    .replace("{old-winner-elo}", String.valueOf(oldEloWinner))
                    .replace("{old-loser-elo}", String.valueOf(oldEloLoser))
                    .replace("{new-winner-elo}", String.valueOf(newEloWinner))
                    .replace("{new-loser-elo}", String.valueOf(newEloLoser))
                    .replace("{math-winner-elo}", String.valueOf(Math.abs(oldEloWinner - newEloWinner)))
                    .replace("{math-loser-elo}", String.valueOf(Math.abs(oldEloLoser - newEloLoser)))
            );

            list.forEach(this::notifyParticipants);
        }

    }

    /**
     * Sends the progress of the winner to the player using the ProgressService.
     * The method no longer needs Division or Tier passed in.
     *
     * @param winner The winning player.
     */
    public void sendProgressToWinner(Player winner) {
        if (winner == null) {
            return;
        }

        try {
            /*
             * TODO: Fix this retarded calculation its pmo
             *  "next thing i know half the core is f---ed" - Titanic Swim Team, Remi (13/07/2025 - 00:37)
             */

            Profile winnerProfile = HardPractice.getInstance().getService(ProfileService.class).getProfile(winner.getUniqueId());
            if (winnerProfile == null || this.getKit() == null) {
                return;
            }

            PlayerProgress progress = HardPractice.getInstance().getService(ProgressService.class).calculateProgress(winnerProfile, this.getKit().getName());
            if (progress == null) {
                return;
            }

            String progressLine;

            if (progress.isMaxRank() && progress.getCurrentWins() >= progress.getWinsForNextTier()) {
                progressLine = " &b&l● &fCONGRATULATIONS! You have reached the maximum rank!";
            } else {
                progressLine = String.format(" &b&l● &fUnlock &b%s &fwith %d more %s!",
                        progress.getNextRankName(),
                        progress.getWinsRequired(),
                        progress.getWinOrWins()
                );
            }
        } catch (Exception exception) {
            String kitName = this.getKit() != null ? this.getKit().getName() : "unknown";
            Logger.logException("Falha ao calcular progresso do vencedor na partida " + kitName, exception);
        }

//        Arrays.asList(
//                "&b&lProgress",
//                progressLine,
//                "  &7(" + progress.getProgressBar(12, "■") + "&7) " + progress.getProgressPercentage(),
//                " &b&l● &fDaily Streak: &b" + "N/A" + " &f(Best: " + "N/A" + ")",
//                " &b&l● &fWin Streak: &b" + "N/A" + " &f(Best: " + "N/A" + ")",
//                ""
//        ).forEach(line -> winner.sendMessage(CC.translate(line)));

//        LocaleService localeService = this.plugin.getService(LocaleService.class);
//        if (!localeService.getBoolean(GameMessagesLocaleImpl.MATCH_DIVISION_PROGRESS_ENABLED_BOOLEAN)) {
//            return;
//        }
//
//        Profile winnerProfile = HardPractice.getInstance().getService(ProfileService.class).getProfile(winner.getUniqueId());
//        PlayerProgress progress = HardPractice.getInstance().getService(ProgressService.class).calculateProgress(winnerProfile, this.getKit().getName());
//
//        List<String> message;
//
//        DivisionTier reachedTier = HardPractice.getInstance().getService(DivisionService.class).getDivisions().stream()
//                .flatMap(div -> div.getTiers().stream())
//                .filter(tier -> tier.getRequiredWins() == progress.getCurrentWins())
//                .findFirst()
//                .orElse(null);
//
//        if (reachedTier != null) {
//            message = localeService.getMessageList(GameMessagesLocaleImpl.MATCH_DIVISION_PROGRESS_REACHED_FORMAT)
//                    .stream()
//                    .map(line -> line.replace("{reached-new-division}", progress.getNextRankName() + " " + reachedTier.getName()))
//                    .collect(Collectors.toList());
//        } else {
//            message = localeService.getMessageList(GameMessagesLocaleImpl.MATCH_DIVISION_PROGRESS_ONGOING_FORMAT);
//        }
//
//        message.replaceAll(string -> string
//                .replace("{next-division}", Objects.requireNonNull(reachedTier).getName())
//                .replace("{wins-required}", String.valueOf(progress.getWinsRequired()))
//                .replace("{win-or-wins}", progress.getWinOrWins())
//                .replace("{progress-bar}", progress.getProgressBar(12, "■"))
//                .replace("{progress-percentage}", progress.getProgressPercentage())
//                .replace("{daily-streak}", "N/A")
//                .replace("{best-daily-streak}", "N/A")
//                .replace("{win-streak}", String.valueOf(winnerProfile.getProfileData().getUnrankedKitData().get(this.getKit().getName()).getWinstreak()))
//                .replace("{best-win-streak}", "N/A")
//        );
//
//        message.forEach(line -> winner.sendMessage(CC.translate(line)));
    }


    /**
     * Method to get the old elo result.
     *
     * @return The old elo result.
     */
    public @NotNull OldEloResult getOldEloResult(GameParticipant<MatchGamePlayer> winner, GameParticipant<MatchGamePlayer> loser) {
        int oldWinnerElo = winner.getLeader().getElo();
        int oldLoserElo = loser.getLeader().getElo();
        return new OldEloResult(oldWinnerElo, oldLoserElo);
    }

    /**
     * Method to get the elo result.
     *
     * @param oldWinnerElo The old elo of the winner.
     * @param oldLoserElo  The old elo of the loser.
     * @return The elo result.
     */
    public @NotNull EloResult getEloResult(int oldWinnerElo, int oldLoserElo) {
        EloCalculator eloCalculator = HardPractice.getInstance().getService(EloCalculator.class);
        int newWinnerElo = eloCalculator.determineNewElo(oldWinnerElo, oldLoserElo, true);
        int newLoserElo = eloCalculator.determineNewElo(oldLoserElo, oldWinnerElo, false);
        return new EloResult(newWinnerElo, newLoserElo);
    }

    /**
     * Method to handle the winner.
     *
     * @param elo    The new elo of the winner.
     * @param winner The winner of the match.
     */
    public void handleWinner(int elo, GameParticipant<MatchGamePlayer> winner) {
        String kitName = this.getKit() != null ? this.getKit().getName() : null;
        if (kitName == null || kitName.trim().isEmpty()) {
            return;
        }

        Profile winnerProfile = HardPractice.getInstance().getService(ProfileService.class).getProfile(winner.getLeader().getUuid());
        if (winnerProfile == null || winnerProfile.getProfileData() == null || winnerProfile.getProfileData().getRankedKitData() == null) {
            return;
        }

        ProfileRankedKitData winnerKitData = winnerProfile.getProfileData()
                .getRankedKitData()
                .computeIfAbsent(kitName, key -> new ProfileRankedKitData());
        winnerKitData.setElo(elo);
        winnerKitData.incrementWins();
        winnerProfile.getProfileData().incrementRankedWins();
        winnerProfile.getProfileData().incrementWinStreak();
        winnerProfile.getProfileData().updateElo(winnerProfile);
        winnerProfile.save();
    }

    /**
     * Method to handle the loser.
     *
     * @param elo   The new elo of the loser.
     * @param loser The loser of the match.
     */
    public void handleLoser(int elo, GameParticipant<MatchGamePlayer> loser) {
        String kitName = this.getKit() != null ? this.getKit().getName() : null;
        if (kitName == null || kitName.trim().isEmpty()) {
            return;
        }

        Profile loserProfile = HardPractice.getInstance().getService(ProfileService.class).getProfile(loser.getLeader().getUuid());
        if (loserProfile == null || loserProfile.getProfileData() == null || loserProfile.getProfileData().getRankedKitData() == null) {
            return;
        }

        ProfileRankedKitData loserKitData = loserProfile.getProfileData()
                .getRankedKitData()
                .computeIfAbsent(kitName, key -> new ProfileRankedKitData());
        loserKitData.setElo(elo);
        loserKitData.incrementLosses();
        loserProfile.getProfileData().incrementRankedLosses();
        loserProfile.getProfileData().resetWinStreak();
        loserProfile.getProfileData().updateElo(loserProfile);
        loserProfile.save();
    }

    private void updateClanStats(Player winner, Player loser, boolean ranked, int winnerEloDelta, int loserEloDelta) {
        KaosCoreBridge kaosCoreBridge = HardPractice.getInstance().getService(KaosCoreBridge.class);
        if (kaosCoreBridge == null) {
            return;
        }

        kaosCoreBridge.applyClanMatchResult(winner, loser, ranked, winnerEloDelta, loserEloDelta);
    }

    @Override
    public boolean canStartRound() {
        return false;
    }

    @Override
    public boolean canEndRound() {
        return (this.getParticipantA().isAllDead() || this.getParticipantB().isAllDead())
                || (this.getParticipantA().getAllPlayers().stream().allMatch(MatchGamePlayer::isDisconnected)
                || this.getParticipantB().getAllPlayers().stream().allMatch(MatchGamePlayer::isDisconnected));
    }

    @Override
    public boolean canEndMatch() {
        return true;
    }

    @Override
    public void handleRespawn(Player player) {
        player.spigot().respawn();
        PlayerUtil.reset(player, false, true);
    }

    @Override
    public void handleDeathItemDrop(Player player, PlayerDeathEvent event) {
        if (this.getKit().isSettingEnabled(KitSettingDropItemsImpl.class)) {
            ListenerUtil.clearDroppedItemsOnDeath(event, player);
        } else {
            event.getDrops().clear();
        }
    }

    @Override
    public void handleDisconnect(Player player) {
        if (!(this.getState() == MatchState.STARTING || this.getState() == MatchState.RUNNING)) {
            return;
        }

        MatchGamePlayer gamePlayer = this.getFromAllGamePlayers(player);
        if (gamePlayer == null || gamePlayer.isDisconnected()) {
            return;
        }

        Profile profile = this.plugin.getService(ProfileService.class).getProfile(player.getUniqueId());
        if (profile != null) {
            this.sendMessage(profile.getFancyName() + " &cdisconnected.");
        } else {
            this.sendMessage("&c" + player.getName() + " &cdisconnected.");
        }

        gamePlayer.setDisconnected(true);
        gamePlayer.setEliminated(true);
        if (!gamePlayer.isDead()) {
            this.handleDeath(player, EntityDamageEvent.DamageCause.CUSTOM);
        }

        if (player.isOnline()) {
            this.finalizePlayer(player);
        }
    }

    /**
     * Gives the base raiding kit to the player based on their team.
     *
     * @param player The player to give the kit to.
     */
    public void determineRolesAndGiveKit(Player player) {
        if (this.getParticipantA() == null || this.getParticipantB() == null) {
            return;
        }

        Kit parentKit = this.getKit();
        if (parentKit == null) {
            Logger.error("&cNao foi possivel determinar o kit pai para a partida de raiding.");
            return;
        }

        BaseRaiderRole role = getParticipantA().containsPlayer(player.getUniqueId())
                ? BaseRaiderRole.TRAPPER
                : BaseRaiderRole.RAIDER;

        Kit kitToGive = HardPractice.getInstance().getService(BaseRaidingService.class).getRaidingKitByRole(parentKit, role);
        if (kitToGive == null) {
            Logger.info("&cNenhum kit encontrado para o papel: " + role.name() + " vinculado ao kit pai.");
            return;
        }

        MatchGamePlayerData data = this.getGamePlayer(player).getData();
        data.setRole(role);

        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService != null ? profileService.getProfile(player.getUniqueId()) : null;
        LayoutData layout = null;
        if (profile != null
                && profile.getProfileData() != null
                && profile.getProfileData().getLayoutData() != null
                && profile.getProfileData().getLayoutData().getLayouts() != null) {
            List<LayoutData> layouts = profile.getProfileData().getLayoutData().getLayouts().get(kitToGive.getName());
            if (layouts != null && !layouts.isEmpty()) {
                layout = layouts.stream().filter(java.util.Objects::nonNull).findFirst().orElse(null);
            }
        }

        //TODO: after implementing multiple layouts, we need to give the books here, if multiple layouts are present in profile.

        if (layout != null && layout.getItems() != null && layout.getItems().length > 0) {
            player.getInventory().setContents(InventoryUtil.cloneItemStackArray(layout.getItems()));
        } else {
            player.getInventory().setContents(InventoryUtil.cloneItemStackArray(kitToGive.getItems()));
        }

        player.getInventory().setArmorContents(InventoryUtil.cloneItemStackArray(kitToGive.getArmor()));
        player.updateInventory();
    }
}