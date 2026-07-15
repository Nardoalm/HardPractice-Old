package live.pvphard.practice.core.profile;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.adapter.core.CoreAdapter;
import live.pvphard.practice.core.profile.data.ProfileData;
import live.pvphard.practice.core.profile.data.types.ProfileFFAData;
import live.pvphard.practice.core.profile.data.types.ProfilePlayTimeData;
import live.pvphard.practice.core.profile.data.types.ProfileRankedKitData;
import live.pvphard.practice.core.profile.data.types.ProfileUnrankedKitData;
import live.pvphard.practice.core.profile.enums.GlobalCooldown;
import live.pvphard.practice.core.profile.enums.ProfileState;
import live.pvphard.practice.feature.abilities.Ability;
import live.pvphard.practice.feature.abilities.cooldown.AbilityCooldown;
import live.pvphard.practice.feature.division.Division;
import live.pvphard.practice.feature.division.DivisionService;
import live.pvphard.practice.feature.division.model.DivisionTier;
import live.pvphard.practice.feature.ffa.FFAMatch;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.feature.kit.KitService;
import live.pvphard.practice.feature.leaderboard.LeaderboardType;
import live.pvphard.practice.feature.match.Match;
import live.pvphard.practice.feature.party.Party;
import live.pvphard.practice.feature.party.PartyService;
import live.pvphard.practice.feature.queue.QueueProfile;
import live.pvphard.practice.feature.queue.QueueType;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import live.pvphard.practice.feature.tournament.model.Tournament;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Emmy
 * @project Hard
 * @date 19/05/2024 - 22:35
 */
@Getter
@Setter
public class Profile {
    private final UUID uuid;
    private String name;
    private long firstJoin;
    private boolean online;
    private Tournament tournament;

    private ProfileData profileData;
    private QueueProfile queueProfile;
    private ProfileState state;

    private LeaderboardType leaderboardType;
    private QueueType queueType;

    private final Map<Class<? extends Ability>, AbilityCooldown> abilityCooldowns;
    private final Map<GlobalCooldown, AbilityCooldown> globalCooldowns;

    private FFAMatch ffaMatch;
    private Match match;
    private Party party;

    private Kit lastPlayedKit;
    private boolean lastPlayedRanked;
    private boolean lastPlayedDuos;

    private ChatColor nameColor;

    public Profile(UUID uuid, String name) {
        this.uuid = uuid;
        this.firstJoin = System.currentTimeMillis();
        this.state = ProfileState.LOBBY;
        this.profileData = new ProfileData();
        this.name = name;
        this.leaderboardType = LeaderboardType.RANKED;
        this.queueType = QueueType.UNRANKED;
        this.nameColor = ChatColor.WHITE;

        this.abilityCooldowns = new HashMap<>();
        this.globalCooldowns = new EnumMap<>(GlobalCooldown.class);
    }

    public ChatColor getNameColor() {
        CoreAdapter adapter = HardPractice.getInstance().getService(CoreAdapter.class);
        if (adapter == null || adapter.getCore() == null) {
            return this.nameColor != null ? this.nameColor : ChatColor.WHITE;
        }

        Player p = HardPractice.getInstance().getServer().getPlayer(this.uuid);
        if (p == null) {
            return this.nameColor != null ? this.nameColor : ChatColor.WHITE;
        }

        ChatColor upToDateColor = adapter.getCore().getPlayerColor(p);
        if (upToDateColor != null && this.nameColor != upToDateColor) {
            this.nameColor = upToDateColor;
        }

        return this.nameColor != null ? this.nameColor : ChatColor.WHITE;
    }

    public String getFancyName() {
        return (this.nameColor != null ? this.nameColor : ChatColor.WHITE) + this.name;
    }

    public Party getParty() {
        PartyService partyService = HardPractice.getInstance().getService(PartyService.class);
        if (partyService == null) {
            return this.party;
        }

        Party activeParty = partyService.getPartyByMember(this.uuid);
        if (this.party != activeParty) {
            this.party = activeParty;
        }
        return this.party;
    }

    public boolean isBusy() {
        return this.state != ProfileState.LOBBY;
    }

    public boolean isInLobbyOrInQueue() {
        return this.state == ProfileState.LOBBY || this.state == ProfileState.WAITING;
    }

    public void load() {
        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        if (profileService != null && profileService.getDatabaseProfile() != null) {
            profileService.getDatabaseProfile().loadProfile(this);
        }
    }

    public void save() {
        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        if (profileService != null && profileService.getDatabaseProfile() != null) {
            profileService.getDatabaseProfile().saveProfile(this);
        }
    }

    public AbilityCooldown getCooldown(Class<? extends Ability> abilityClass) {
        return this.abilityCooldowns.computeIfAbsent(abilityClass, key -> new AbilityCooldown());
    }

    public AbilityCooldown getGlobalCooldown(GlobalCooldown type) {
        return this.globalCooldowns.computeIfAbsent(type, key -> new AbilityCooldown());
    }

    public List<Kit> getSortedKits() {
        KitService kitService = HardPractice.getInstance().getService(KitService.class);
        if (kitService == null || kitService.getKits() == null || this.profileData == null) {
            return Collections.emptyList();
        }

        return kitService.getKits().stream().filter(kit -> {
            ProfileRankedKitData rankedData = this.profileData.getRankedKitData().get(kit.getName());
            ProfileUnrankedKitData unrankedData = this.profileData.getUnrankedKitData().get(kit.getName());
            ProfileFFAData ffaData = this.profileData.getFfaData().get(kit.getName());

            return (rankedData != null && (rankedData.getWins() > 0 || rankedData.getLosses() > 0)) ||
                    (unrankedData != null && (unrankedData.getWins() > 0 || unrankedData.getLosses() > 0)) ||
                    (ffaData != null && (ffaData.getKills() > 0 || ffaData.getDeaths() > 0));
        }).sorted(Comparator.comparingInt((Kit kit) -> {
            ProfileRankedKitData ranked = this.profileData.getRankedKitData().get(kit.getName());
            return ranked != null ? ranked.getElo() : 0;
        }).reversed().thenComparingInt(kit -> {
            ProfileRankedKitData ranked = this.profileData.getRankedKitData().get(kit.getName());
            return ranked != null ? ranked.getWins() : 0;
        }).reversed().thenComparingInt(kit -> {
            ProfileFFAData ffa = this.profileData.getFfaData().get(kit.getName());
            return ffa != null ? ffa.getKills() : 0;
        }).reversed()).collect(Collectors.toList());
    }

    public boolean hasParticipatedInRanked() {
        if (this.profileData == null || this.profileData.getRankedKitData() == null) return false;
        return this.profileData.getRankedKitData().values().stream()
                .anyMatch(data -> data != null && (data.getWins() > 0 || data.getLosses() > 0 || data.getElo() != 1000));
    }

    public boolean hasParticipatedInTournament() {
        return false; //TODO: Implement tournament system
    }

    public boolean hasParticipatedInFFA() {
        if (this.profileData == null || this.profileData.getFfaData() == null) return false;
        return this.profileData.getFfaData().values().stream()
                .anyMatch(data -> data != null && (data.getKills() > 0 || data.getDeaths() > 0));
    }

    public String getNextDivisionAndTier(String kitName) {
        if (this.profileData == null || this.profileData.getUnrankedKitData() == null) return "Unranked";

        ProfileUnrankedKitData kitData = this.profileData.getUnrankedKitData().get(kitName);
        if (kitData == null) return "Unranked";

        Division division = kitData.getDivision();
        DivisionTier tier = kitData.getTier();

        if (division == null || tier == null) return "Unranked";

        List<DivisionTier> tiers = division.getTiers();
        if (tiers == null || tiers.isEmpty()) return "Unranked";

        int tierIndex = tiers.indexOf(tier);
        if (tierIndex != -1 && tierIndex < tiers.size() - 1) {
            DivisionTier nextTier = tiers.get(tierIndex + 1);
            return division.getName() + " " + nextTier.getName();
        }

        DivisionService divisionService = HardPractice.getInstance().getService(DivisionService.class);
        if (divisionService == null || divisionService.getDivisions() == null) return "Max";

        List<Division> divisions = divisionService.getDivisions();
        int divisionIndex = divisions.indexOf(division);

        if (divisionIndex != -1 && divisionIndex < divisions.size() - 1) {
            Division nextDivision = divisions.get(divisionIndex + 1);
            if (nextDivision != null && nextDivision.getTiers() != null && !nextDivision.getTiers().isEmpty()) {
                return nextDivision.getName() + " " + nextDivision.getTiers().get(0).getName();
            }
        }

        return division.getName() + " " + tier.getName();
    }

    public Division getNextDivision(String kitName) {
        if (this.profileData == null || this.profileData.getUnrankedKitData() == null) return null;

        ProfileUnrankedKitData kitData = this.profileData.getUnrankedKitData().get(kitName);
        if (kitData == null) return null;

        Division division = kitData.getDivision();
        DivisionService divisionService = HardPractice.getInstance().getService(DivisionService.class);

        if (divisionService == null || divisionService.getDivisions() == null) return null;

        // Se o jogador não tem divisão ainda, retorna a primeira disponível
        if (division == null) {
            if (!divisionService.getDivisions().isEmpty()) {
                return divisionService.getDivisions().get(0);
            }
            return null;
        }

        List<Division> divisions = divisionService.getDivisions();
        int divisionIndex = divisions.indexOf(division);

        if (divisionIndex != -1 && divisionIndex < divisions.size() - 1) {
            return divisions.get(divisionIndex + 1);
        }

        return null;
    }

    public void updatePlayTime() {
        if (this.profileData == null) return;
        ProfilePlayTimeData playTimeData = this.profileData.getPlayTimeData();
        if (playTimeData != null) {
            long now = System.currentTimeMillis();
            playTimeData.setTotal(playTimeData.getTotal() + (now - playTimeData.getLastLogin()));
            playTimeData.setLastLogin(now);
        }
    }
}
