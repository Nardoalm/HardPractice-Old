package live.pvphard.practice.feature.leaderboard.internal;

import com.mongodb.client.MongoCollection;
import live.pvphard.practice.feature.kit.KitService;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.bootstrap.HardContext;
import live.pvphard.practice.bootstrap.annotation.Service;
import live.pvphard.practice.core.database.MongoService;
import live.pvphard.practice.feature.leaderboard.LeaderboardService;
import live.pvphard.practice.feature.leaderboard.data.LeaderboardPlayerData;
import live.pvphard.practice.feature.leaderboard.LeaderboardType;
import live.pvphard.practice.feature.leaderboard.model.LeaderboardRecord;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.data.ProfileData;
import live.pvphard.practice.core.profile.data.types.ProfileFFAData;
import live.pvphard.practice.core.profile.data.types.ProfileRankedKitData;
import live.pvphard.practice.core.profile.data.types.ProfileUnrankedKitData;
import live.pvphard.practice.common.logger.Logger;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Getter
@Service(provides = LeaderboardService.class, priority = 280)
public class LeaderboardServiceImpl implements LeaderboardService {
    private final MongoService mongoService;
    private final KitService kitService;
    private final ProfileService profileService;
    private final ExecutorService executorService;

    private final Map<Kit, List<LeaderboardRecord>> leaderboardCache = new ConcurrentHashMap<>();
    private final Map<String, Integer> onlinePlayerCache = new ConcurrentHashMap<>();

    public LeaderboardServiceImpl(MongoService mongoService, KitService kitService, ProfileService profileService) {
        this.mongoService = mongoService;
        this.kitService = kitService;
        this.profileService = profileService;
        this.executorService = Executors.newFixedThreadPool(4);
    }

    @Override
    public void initialize(HardContext context) {
        this.forceRecalculateAll();
    }

    @Override
    public void shutdown(HardContext context) {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            Logger.info("The leaderboard executor service has been shut down.");
        }
    }

    @Override
    public void forceRecalculateAll() {
        MongoCollection<Document> profileCollection = this.mongoService.getMongoDatabase().getCollection("profiles");

        CompletableFuture.allOf(this.kitService.getKits().stream()
                .map(kit -> CompletableFuture.runAsync(() -> calculateLeaderboardForKit(kit, profileCollection), executorService)).toArray(CompletableFuture[]::new)).join();
    }

    private void calculateLeaderboardForKit(Kit kit, MongoCollection<Document> profileCollection) {
        List<LeaderboardRecord> records = Collections.synchronizedList(new ArrayList<>());

        for (LeaderboardType type : LeaderboardType.values()) {
            List<LeaderboardPlayerData> playerDataList = fetchOptimizedLeaderboard(profileCollection, kit, type);
            records.add(new LeaderboardRecord(type, playerDataList));
        }

        this.leaderboardCache.put(kit, records);
    }

    private List<LeaderboardPlayerData> fetchOptimizedLeaderboard(MongoCollection<Document> profileCollection, Kit kit, LeaderboardType type) {
        List<LeaderboardPlayerData> playerDataList = new ArrayList<>();

        List<Document> pipeline = buildAggregationPipeline(kit, type);

        for (Document doc : profileCollection.aggregate(pipeline)) {
            try {
                String name = doc.getString("name");
                UUID uuid = UUID.fromString(doc.getString("uuid"));
                int value = doc.getInteger("value", 0);

                if (value > 0 || type == LeaderboardType.RANKED) {
                    playerDataList.add(new LeaderboardPlayerData(name, uuid, kit, value));
                }
            } catch (Exception ignored) {
            }
        }

        return playerDataList;
    }

    private List<Document> buildAggregationPipeline(Kit kit, LeaderboardType type) {
        List<Document> pipeline = new ArrayList<>();

        Document projectStage = new Document("$project", new Document()
                .append("uuid", 1)
                .append("name", 1)
                .append("value", buildValueExtraction(kit, type)));

        pipeline.add(projectStage);

        if (type != LeaderboardType.RANKED) {
            pipeline.add(new Document("$match", new Document("value", new Document("$gt", 0))));
        }

        pipeline.add(new Document("$sort", new Document("value", -1)));
        pipeline.add(new Document("$limit", 100));

        return pipeline;
    }

    private Document buildValueExtraction(Kit kit, LeaderboardType type) {
        String kitName = kit.getName();

        switch (type) {
            case RANKED:
                return new Document("$ifNull", Arrays.asList(
                        new Document("$getField", new Document()
                                .append("field", "elo")
                                .append("input", new Document("$getField", new Document()
                                        .append("field", kitName)
                                        .append("input", "$profileData.rankedKitData")))),
                        1000
                ));
            case UNRANKED:
                return new Document("$ifNull", Arrays.asList(
                        new Document("$getField", new Document()
                                .append("field", "wins")
                                .append("input", new Document("$getField", new Document()
                                        .append("field", kitName)
                                        .append("input", "$profileData.unrankedKitData")))),
                        0
                ));
            case FFA:
                return new Document("$ifNull", Arrays.asList(
                        new Document("$getField", new Document()
                                .append("field", "kills")
                                .append("input", new Document("$getField", new Document()
                                        .append("field", kitName)
                                        .append("input", "$profileData.ffaData")))),
                        0
                ));
            case WIN_STREAK:
                return new Document("$ifNull", Arrays.asList(
                        new Document("$getField", new Document()
                                .append("field", "winstreak")
                                .append("input", new Document("$getField", new Document()
                                        .append("field", kitName)
                                        .append("input", "$profileData.unrankedKitData")))),
                        0
                ));
            default:
                return new Document("$literal", 0);
        }
    }

    @Override
    public List<LeaderboardPlayerData> getLeaderboardEntries(Kit kit, LeaderboardType type) {
        if (kit == null) {
            MongoCollection<Document> profileCollection = this.mongoService.getMongoDatabase().getCollection("profiles");
            return fetchGlobalLeaderboard(profileCollection, type);
        }

        return this.refreshOnlinePlayersOptimized(kit, type);
    }

    private List<LeaderboardPlayerData> fetchGlobalLeaderboard(MongoCollection<Document> profileCollection, LeaderboardType type) {
        List<LeaderboardPlayerData> playerDataList = new ArrayList<>();
        List<Document> pipeline = new ArrayList<>();

        String fieldPath = "";
        String subField = "";
        int defaultValue = (type == LeaderboardType.RANKED) ? 1000 : 0;

        switch (type) {
            case RANKED:
                fieldPath = "profileData.rankedKitData";
                subField = "elo";
                break;
            case UNRANKED:
                fieldPath = "profileData.unrankedKitData";
                subField = "wins";
                break;
            case FFA:
                fieldPath = "profileData.ffaData";
                subField = "kills";
                break;
            case WIN_STREAK:
                fieldPath = "profileData.unrankedKitData";
                subField = "winstreak";
                break;
        }

        pipeline.add(new Document("$project", new Document()
                .append("uuid", 1)
                .append("name", 1)
                .append("kitsArray", new Document("$objectToArray", new Document("$ifNull", Arrays.asList("$" + fieldPath, new Document()))))
        ));

        pipeline.add(new Document("$unwind", new Document()
                .append("path", "$kitsArray")
                .append("preserveNullAndEmptyArrays", true)
        ));

        Document groupExpression = type == LeaderboardType.RANKED
                ? new Document("$max", new Document("$ifNull", Arrays.asList("$kitsArray.v." + subField, 1000)))
                : new Document("$sum", new Document("$ifNull", Arrays.asList("$kitsArray.v." + subField, 0)));

        pipeline.add(new Document("$group", new Document()
                .append("_id", "$uuid")
                .append("name", new Document("$first", "$name"))
                .append("value", groupExpression)
        ));

        if (type == LeaderboardType.RANKED) {
            pipeline.add(new Document("$project", new Document()
                    .append("uuid", "$_id")
                    .append("name", 1)
                    .append("value", new Document("$cond", new Document()
                            .append("if", new Document("$eq", Arrays.asList("$value", 0)))
                            .append("then", 1000)
                            .append("else", "$value")
                    ))
            ));
        } else {
            pipeline.add(new Document("$project", new Document()
                    .append("uuid", "$_id")
                    .append("name", 1)
                    .append("value", 1)
            ));
            pipeline.add(new Document("$match", new Document("value", new Document("$gt", 0))));
        }

        pipeline.add(new Document("$sort", new Document("value", -1)));
        pipeline.add(new Document("$limit", 10));

        for (Document doc : profileCollection.aggregate(pipeline)) {
            try {
                String name = doc.getString("name");
                if (name == null || name.isEmpty()) continue;

                Object uuidObj = doc.get("uuid");
                UUID uuid = uuidObj instanceof String ? UUID.fromString((String) uuidObj) : (UUID) uuidObj;
                int value = doc.getInteger("value", defaultValue);

                playerDataList.add(new LeaderboardPlayerData(name, uuid, null, value));
            } catch (Exception ignored) {}
        }
        return playerDataList;
    }

    private List<LeaderboardPlayerData> refreshOnlinePlayersOptimized(Kit kit, LeaderboardType type) {
        if (kit == null) return Collections.emptyList();

        List<LeaderboardRecord> records = this.leaderboardCache.get(kit);
        if (records == null) return Collections.emptyList();

        LeaderboardRecord record = records.stream()
                .filter(r -> r.getType() == type)
                .findFirst()
                .orElse(null);

        if (record == null) return Collections.emptyList();

        List<LeaderboardPlayerData> cachedLeaderboard = record.getParticipants();
        List<LeaderboardPlayerData> temporaryLeaderboard = new ArrayList<>();

        for (LeaderboardPlayerData data : cachedLeaderboard) {
            temporaryLeaderboard.add(new LeaderboardPlayerData(data.getName(), data.getUuid(), data.getKit(), data.getValue()));
        }

        Map<UUID, Integer> onlinePlayerUpdates = new HashMap<>();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            Profile profile = this.profileService.getProfile(onlinePlayer.getUniqueId());
            if (profile != null) {
                int newValue = getValueForType(profile, kit, type);
                onlinePlayerUpdates.put(profile.getUuid(), newValue);
            }
        }

        for (LeaderboardPlayerData playerData : temporaryLeaderboard) {
            Integer newValue = onlinePlayerUpdates.get(playerData.getUuid());
            if (newValue != null) {
                playerData.setValue(newValue);
            }
        }

        Set<UUID> temporaryUuids = temporaryLeaderboard.stream()
                .map(LeaderboardPlayerData::getUuid)
                .collect(Collectors.toSet());

        for (Map.Entry<UUID, Integer> entry : onlinePlayerUpdates.entrySet()) {
            if (!temporaryUuids.contains(entry.getKey())) {
                Player player = Bukkit.getPlayer(entry.getKey());
                if (player != null) {
                    temporaryLeaderboard.add(new LeaderboardPlayerData(player.getName(), entry.getKey(), kit, entry.getValue()));
                }
            }
        }

        temporaryLeaderboard.sort(Comparator.comparingInt(LeaderboardPlayerData::getValue).reversed());
        return temporaryLeaderboard;
    }

    private int getValueForType(Profile profile, Kit kit, LeaderboardType type) {
        ProfileData data = profile.getProfileData();
        switch (type) {
            case RANKED:
                return data.getRankedKitData().getOrDefault(kit.getName(), new ProfileRankedKitData()).getElo();
            case UNRANKED:
                return data.getUnrankedKitData().getOrDefault(kit.getName(), new ProfileUnrankedKitData()).getWins();
            case FFA:
                return data.getFfaData().getOrDefault(kit.getName(), new ProfileFFAData()).getKills();
            case WIN_STREAK:
                return data.getUnrankedKitData().getOrDefault(kit.getName(), new ProfileUnrankedKitData()).getWinstreak();
            default:
                return 0;
        }
    }
}