package us.alleypvp.practice.adapter.placeholder.internal;

import us.alleypvp.practice.AlleyPractice;
import us.alleypvp.practice.common.constants.PluginConstant;
import us.alleypvp.practice.core.profile.ProfileService;
import us.alleypvp.practice.core.profile.Profile;
import us.alleypvp.practice.core.profile.data.ProfileData;
import us.alleypvp.practice.core.profile.enums.ProfileState;
import us.alleypvp.practice.common.text.CC;
import us.alleypvp.practice.common.animation.internal.types.DotAnimation;
import us.alleypvp.practice.common.text.LevelBadgeUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class KaosPlaceholderExpansion extends PlaceholderExpansion {

    protected final AlleyPractice plugin;
    protected final String notAvailableString;
    private final DotAnimation dotAnimation = new DotAnimation();

    public KaosPlaceholderExpansion(AlleyPractice plugin) {
        this.plugin = plugin;
        this.notAvailableString = CC.translate("&cN/D");
    }

    @Override
    public @NotNull String getIdentifier() {
        return "kaos";
    }

    @Override
    public @NotNull String getAuthor() {
        return this.plugin.getDescription().getAuthors().isEmpty() ? "Emmy - ysubz" : this.plugin.getDescription().getAuthors().get(0);
    }

    @Override
    public @NotNull String getVersion() {
        PluginConstant constant = this.plugin.getService(PluginConstant.class);
        return constant != null ? constant.getVersion() : "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) {
            return "";
        }

        String normalizedParams = params.toLowerCase().replace('_', '-');

        if (normalizedParams.equals("dot-animation")) {
            return dotAnimation.getCurrentFrame();
        }

        ProfileService profileService = AlleyPractice.getInstance().getService(ProfileService.class);

        if (normalizedParams.equals("playing-count")) {
            if (profileService == null) {
                return "0 playing now!";
            }
            long activePlayersCount = profileService.getProfiles().values().stream()
                    .filter(java.util.Objects::nonNull)
                    .filter(p -> p.getState() == ProfileState.PLAYING)
                    .count();
            return activePlayersCount + " playing now!";
        }

        Profile profile = (profileService != null) ? profileService.getProfile(player.getUniqueId()) : null;

        if (profile == null || profile.getProfileData() == null) {
            return this.notAvailableString;
        }

        ProfileData profileData = profile.getProfileData();

        if (normalizedParams.startsWith("division-")) {
            String kitName = params.length() > 9 ? params.substring(9) : "";
            if (kitName.trim().isEmpty()) {
                return this.notAvailableString;
            }
            String resolvedKitName = profileData.getUnrankedKitData().keySet().stream()
                    .filter(key -> key.equalsIgnoreCase(kitName))
                    .findFirst()
                    .orElse(null);

            if (resolvedKitName != null) {
                if (profileData.getUnrankedKitData().get(resolvedKitName).getDivision() == null) {
                    return this.notAvailableString;
                }
                String division = profileData.getUnrankedKitData().get(resolvedKitName).getDivision().getName();
                return division != null ? CC.translate(division) : this.notAvailableString;
            }
            return this.notAvailableString;
        }

        switch (normalizedParams) {
            case "player-global-elo":
                return String.valueOf(profileData.getElo());

            case "player-unranked-wins":
                return String.valueOf(profileData.getTotalWins());

            case "player-unranked-losses":
                return String.valueOf(profileData.getTotalLosses());

            case "player-ranked-wins":
                return String.valueOf(profileData.getRankedWins());

            case "player-ranked-losses":
                return String.valueOf(profileData.getRankedLosses());

            case "player-level":
            case "level":
            case "nivel":
            case "nível":
                return LevelBadgeUtil.getDisplayBadge(player, profileData.getExperience());

            case "player-level-number":
            case "level-number":
            case "nivel-numero":
                return String.valueOf(LevelBadgeUtil.getDisplayLevel(player, profileData.getExperience()));

            case "player-experience":
            case "xp":
                return String.valueOf(profileData.getExperience());

            case "level-progress":
            case "player-level-progress":
                return LevelBadgeUtil.getProgressDetails(profileData.getExperience());

            case "player-coins":
                return String.valueOf(profileData.getCoins());

            default:
                return null;
        }
    }
}