package live.pvphard.practice.visual.scoreboard.internal;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.adapter.core.CoreAdapter;
import live.pvphard.practice.common.PlayerDisplayUtil;
import live.pvphard.practice.core.config.ConfigService;
import live.pvphard.practice.common.text.LevelBadgeUtil;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.enums.ProfileState;
import live.pvphard.practice.visual.scoreboard.Scoreboard;
import live.pvphard.practice.common.text.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LobbyScoreboardImpl implements Scoreboard {

    @Override
    public List<String> getLines(Profile profile) {
        if (profile == null || profile.getProfileData() == null) {
            return Collections.singletonList("&cLoading data...");
        }

        Player player = Bukkit.getPlayer(profile.getUuid());
        if (player == null || !player.isOnline()) {
            return Collections.emptyList();
        }

        ConfigService configService = HardPractice.getInstance().getService(ConfigService.class);
        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        CoreAdapter coreAdapter = HardPractice.getInstance().getService(CoreAdapter.class);
        if (configService == null || configService.getScoreboardConfig() == null || profileService == null) {
            return Collections.emptyList();
        }

        List<String> scoreboardLines = new ArrayList<>();
        List<String> template = (profile.getParty() != null)
                ? configService.getScoreboardConfig().getStringList("scoreboard.lines.party")
                : configService.getScoreboardConfig().getStringList("scoreboard.lines.lobby");
        int experience = profile.getProfileData().getExperience();
        String levelProgressBar = LevelBadgeUtil.getProgressBar(experience, 12);
        String levelProgressDetails = LevelBadgeUtil.getProgressDetails(experience);

        String levelName = LevelBadgeUtil.getDisplayBadge(player, experience);

        String rankStr = "";
        if (coreAdapter != null && coreAdapter.getCore() != null) {
            rankStr = coreAdapter.getCore().getRankColor(player) + coreAdapter.getCore().getRankName(player);
        }

        for (String line : template) {
            String processedLine = CC.translate(line)
                    .replace("{online}", String.valueOf(Bukkit.getOnlinePlayers().size()))
                    .replace("{wins}", String.valueOf(profile.getProfileData().getTotalWins()))
                    .replace("{level}", levelName)
                    .replace("{nivel}", levelName)
                    .replace("{nível}", levelName)
                    .replace("{level_progress_bar}", levelProgressBar)
                    .replace("{level_progress_details}", levelProgressDetails)
                    .replace("{rank}", rankStr)
                    .replace("{playing}", String.valueOf(safeCountState(profileService, ProfileState.PLAYING)))
                    .replace("{in-queue}", String.valueOf(safeCountState(profileService, ProfileState.WAITING)));

            if (profile.getParty() != null) {
                String leaderName = "Unknown";
                Profile leaderProfile = profileService.getProfile(profile.getParty().getLeader().getUniqueId());
                if (leaderProfile != null) {
                    Player leaderPlayer = Bukkit.getPlayer(leaderProfile.getUuid());
                    String leaderColor = leaderProfile.getNameColor() != null ? leaderProfile.getNameColor().toString() : "";
                    leaderName = leaderColor + PlayerDisplayUtil.resolveCurrentNick(leaderPlayer, leaderProfile.getName());
                }

                processedLine = CC.translate(processedLine)
                        .replace("{party-size}", String.valueOf(profile.getParty().getMembers().size()))
                        .replace("{party-privacy}", profile.getParty().isPrivate() ? "Private" : "Public")
                        .replace("{party-leader}", leaderName);
            }

            scoreboardLines.add(processedLine);
        }

        return scoreboardLines;
    }

    @Override
    public List<String> getLines(Profile profile, Player player) {
        return Collections.emptyList();
    }
}