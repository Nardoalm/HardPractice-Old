package live.pvphard.practice.feature.match.internal;

import live.pvphard.practice.bootstrap.HardContext;
import live.pvphard.practice.bootstrap.annotation.Service;
import live.pvphard.practice.common.logger.Logger;
import live.pvphard.practice.core.config.ConfigService;
import live.pvphard.practice.core.locale.LocaleService;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.feature.arena.Arena;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.feature.kit.setting.KitSetting;
import live.pvphard.practice.feature.kit.setting.types.mode.*;
import live.pvphard.practice.feature.match.internal.types.*;
import live.pvphard.practice.feature.match.Match;
import live.pvphard.practice.feature.match.MatchService;
import live.pvphard.practice.feature.match.model.GameParticipant;
import live.pvphard.practice.feature.match.model.internal.MatchGamePlayer;
import live.pvphard.practice.feature.queue.Queue;
import live.pvphard.practice.feature.queue.QueueService;
import lombok.Getter;
import live.pvphard.practice.feature.tournament.model.Tournament;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@Service(provides = MatchService.class, priority = 220)
public class MatchServiceImpl implements MatchService {
    @FunctionalInterface
    private interface MatchFactory {
        Match create(Queue queue, Kit kit, Arena arena, boolean isRanked, GameParticipant<MatchGamePlayer> pA, GameParticipant<MatchGamePlayer> pB);
    }

    private final ProfileService profileService;
    private final QueueService queueService;
    private final ConfigService configService;
    private final LocaleService localeService;

    private final List<Match> matches = new CopyOnWriteArrayList<>();
    private final Map<Class<? extends KitSetting>, MatchFactory> matchFactoryRegistry = new LinkedHashMap<>();

    public MatchServiceImpl(ProfileService profileService, QueueService queueService, ConfigService configService, LocaleService localeService) {
        this.profileService = profileService;
        this.queueService = queueService;
        this.configService = configService;
        this.localeService = localeService;
    }

    @Override
    public void initialize(HardContext context) {
        this.registerMatchFactories();
    }

    @Override
    public void shutdown(HardContext context) {
        if (this.matches.isEmpty()) {
            return;
        }
        Logger.info("Ending " + this.matches.size() + " active matches due to server shutdown...");
        new ArrayList<>(this.matches).forEach(Match::endMatch);
        this.matches.clear();
    }

    @Override
    public void addMatch(Match match) {
        if (match != null) {
            this.matches.add(match);
        }
    }

    @Override
    public void removeMatch(Match match) {
        this.matches.remove(match);
    }

    @Override
    public void createTournamentMatch(Tournament tournament, Kit kit, Arena arena, GameParticipant<MatchGamePlayer> participantA, GameParticipant<MatchGamePlayer> participantB) {
        MatchFactory factory = null;
        for (Map.Entry<Class<? extends KitSetting>, MatchFactory> entry : matchFactoryRegistry.entrySet()) {
            if (kit.isSettingEnabled(entry.getKey())) {
                factory = entry.getValue();
                break;
            }
        }

        Match match;
        if (factory != null) {
            match = factory.create(null, kit, arena, false, participantA, participantB);
        } else {
            match = new DefaultMatch(null, kit, arena, false, participantA, participantB);
        }

        match.setTournament(tournament);
        match.setAffectStatistics(false);
        match.setTeamMatch(tournament.getTeamSize() > 1);

        tournament.addActiveMatch(match);

        this.addMatch(match);
        match.startMatch();
    }

    private void registerMatchFactories() {
        matchFactoryRegistry.put(KitSettingBed.class, BedMatch::new);
        matchFactoryRegistry.put(KitSettingLives.class, LivesMatch::new);
        matchFactoryRegistry.put(KitSettingCheckpoint.class, CheckpointMatch::new);
        matchFactoryRegistry.put(KitSettingHideAndSeek.class, HideAndSeekMatch::new);
        matchFactoryRegistry.put(KitSettingStickFight.class, (q, k, ar, r, pA, pB) -> new RoundsMatch(q, k, ar, r, pA, pB, 5));
        matchFactoryRegistry.put(KitSettingRounds.class, (q, k, ar, r, pA, pB) -> new RoundsMatch(q, k, ar, r, pA, pB, 3));
    }

    @Override
    public void createAndStartMatch(Kit kit, Arena arena, GameParticipant<MatchGamePlayer> participantA, GameParticipant<MatchGamePlayer> participantB, boolean teamMatch, boolean affectStatistics, boolean isRanked) {
        Profile profileA = this.profileService.getProfile(participantA.getPlayers().get(0).getUuid());
        Profile profileB = this.profileService.getProfile(participantB.getPlayers().get(0).getUuid());
        if (profileA.getMatch() != null || profileB.getMatch() != null) {
            return;
        }

        Queue matchingQueue = this.queueService.getQueues().stream()
                .filter(queue -> queue.getKit().equals(kit))
                .findFirst()
                .orElse(null);

        MatchFactory factory = null;
        for (Map.Entry<Class<? extends KitSetting>, MatchFactory> entry : matchFactoryRegistry.entrySet()) {
            if (kit.isSettingEnabled(entry.getKey())) {
                factory = entry.getValue();
                break;
            }
        }

        Match match;
        if (factory != null) {
            match = factory.create(matchingQueue, kit, arena, isRanked, participantA, participantB);
        } else {
            match = new DefaultMatch(matchingQueue, kit, arena, isRanked, participantA, participantB);
        }

        match.setTeamMatch(teamMatch);
        match.setAffectStatistics(affectStatistics);

        this.addMatch(match);
        match.startMatch();
    }
}