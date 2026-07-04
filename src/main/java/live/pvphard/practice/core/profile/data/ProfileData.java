package live.pvphard.practice.core.profile.data;

import com.google.common.collect.Maps;
import live.pvphard.practice.HardPractice;
import live.pvphard.practice.core.profile.data.types.*;
import live.pvphard.practice.feature.kit.KitService;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.feature.kit.setting.types.mode.KitSettingRanked;
import live.pvphard.practice.feature.level.LevelService;
import live.pvphard.practice.feature.level.data.LevelData;
import live.pvphard.practice.feature.title.TitleService;
import live.pvphard.practice.feature.title.model.TitleRecord;
import live.pvphard.practice.feature.match.data.MatchData;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.common.text.LevelBadgeUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Emmy
 * @project Hard
 * @date 21/05/2024 - 22:03
 */
@Getter
@Setter
public class ProfileData {
    private Map<String, ProfileUnrankedKitData> unrankedKitData;
    private Map<String, ProfileRankedKitData> rankedKitData;
    private Map<String, ProfileFFAData> ffaData;

    private ProfileLayoutData layoutData;
    private ProfileSettingData settingData;
    private ProfileCosmeticData cosmeticData;
    private ProfilePlayTimeData playTimeData;

    private List<MatchData> previousMatches;

    private List<String> unlockedTitles;

    private String selectedTitle = "";
    private String globalLevel = "";

    private int elo = 1000;
    private int experience = 0;
    private int coins = 100;
    private int unrankedWins = 0;
    private int unrankedLosses = 0;
    private int rankedWins = 0;
    private int rankedLosses = 0;
    private int winStreak = 0;
    private int bestWinStreak = 0;

    private boolean rankedBanned = false;

    public ProfileData() {
        this.initializeMaps();
        this.feedDataClasses();
        this.initializeDataClasses();
        this.previousMatches = new ArrayList<>();
        this.unlockedTitles = new ArrayList<>();
    }

    private void initializeDataClasses() {
        this.settingData = new ProfileSettingData();
        this.cosmeticData = new ProfileCosmeticData();
        this.playTimeData = new ProfilePlayTimeData();
        this.layoutData = new ProfileLayoutData();
    }

    private void feedDataClasses() {
        KitService kitService = HardPractice.getInstance().getService(KitService.class);
        if (kitService != null && kitService.getKits() != null) {
            for (Kit kit : kitService.getKits()) {
                this.rankedKitData.put(kit.getName(), new ProfileRankedKitData());
                this.unrankedKitData.put(kit.getName(), new ProfileUnrankedKitData());
                this.ffaData.put(kit.getName(), new ProfileFFAData());
            }
        }
    }

    private void initializeMaps() {
        this.unrankedKitData = Maps.newHashMap();
        this.rankedKitData = Maps.newHashMap();
        this.ffaData = Maps.newHashMap();
    }

    private int calculateGlobalElo(Profile profile) {
        KitService kitService = HardPractice.getInstance().getService(KitService.class);
        if (kitService == null || kitService.getKits() == null) return 0;

        List<Kit> rankedKits = kitService.getKits().stream()
                .filter(kit -> kit.isSettingEnabled(KitSettingRanked.class))
                .collect(Collectors.toList());

        if (rankedKits.isEmpty()) return 0;

        int totalElo = rankedKits.stream()
                .mapToInt(kit -> {
                    ProfileRankedKitData kitData = profile.getProfileData().getRankedKitData().get(kit.getName());
                    return kitData != null ? kitData.getElo() : 0;
                })
                .sum();

        return totalElo / rankedKits.size();
    }

    public void determineTitles() {
        TitleService titleService = HardPractice.getInstance().getService(TitleService.class);
        if (titleService == null || titleService.getTitles() == null) return;

        for (TitleRecord title : titleService.getTitles().values()) {
            if (title.getKit() == null) continue;

            ProfileUnrankedKitData kitData = this.unrankedKitData.get(title.getKit().getName());

            if (kitData != null && kitData.getDivision() != null) {
                if (kitData.getDivision().equals(title.getRequiredDivision())) {
                    if (!this.unlockedTitles.contains(title.getKit().getName())) {
                        this.unlockedTitles.add(title.getKit().getName());
                    }
                }
            }
        }
    }

    public void determineLevel() {
        this.globalLevel = String.valueOf(LevelBadgeUtil.getLevel(this.experience));
    }

    public void updateElo(Profile profile) {
        LevelService levelService = HardPractice.getInstance().getService(LevelService.class);
        if (levelService == null) return;

        int previousElo = this.elo;
        LevelData prevLevelData = levelService.getLevel(previousElo);
        String previousLevel = (prevLevelData != null) ? prevLevelData.getName() : "N/D";

        this.elo = this.calculateGlobalElo(profile);

        LevelData newLevelData = levelService.getLevel(this.elo);
        String newLevel = (newLevelData != null) ? newLevelData.getName() : "N/D";

        if (!newLevel.equals(previousLevel) && !newLevel.equals("N/D")) {
            this.sendLevelUpMessage(profile, newLevel);
        }
    }

    private void sendLevelUpMessage(Profile profile, String newLevel) {
        Arrays.asList(
                "",
                "&b&lNEW LEVEL &f| &a&lCONGRATULATIONS!",
                " &fYou have reached &b" + newLevel + " &fin the global ranking system.",
                ""
        ).forEach(line -> Bukkit.getPlayer(profile.getUuid()).sendMessage(CC.translate(line)));
    }

    public int getTotalWins() {
        return this.rankedWins + this.unrankedWins;
    }

    public int getTotalLosses() {
        return this.rankedLosses + this.unrankedLosses;
    }

    public int getTotalFFAKills() {
        return this.ffaData.values().stream().mapToInt(ProfileFFAData::getKills).sum();
    }

    public int getTotalFFADeaths() {
        return this.ffaData.values().stream().mapToInt(ProfileFFAData::getDeaths).sum();
    }

    public void incrementUnrankedWins() {
        this.unrankedWins++;
    }

    public void incrementUnrankedLosses() {
        this.unrankedLosses++;
    }

    public void incrementRankedWins() {
        this.rankedWins++;
    }

    public void incrementRankedLosses() {
        this.rankedLosses++;
    }

    public void incrementCoins(int amount) {
        this.coins += amount;
    }

    public void addExperience(int amount) {
        if (amount <= 0) {
            return;
        }

        this.experience += amount;
        this.determineLevel();
    }

    public void incrementWinStreak() {
        this.winStreak++;
        if (this.winStreak > this.bestWinStreak) {
            this.bestWinStreak = this.winStreak;
        }
    }

    public void resetWinStreak() {
        this.winStreak = 0;
    }
}