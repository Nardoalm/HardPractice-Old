package live.pvphard.practice.common.placeholder;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.common.animation.internal.types.DotAnimation;
import live.pvphard.practice.common.text.LevelBadgeUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class KaosPlaceholderExpansion extends PlaceholderExpansion {

    private final HardPractice plugin;
    private final DotAnimation dotAnimation = new DotAnimation(); // Instância ÚNICA para não travar no "."

    public KaosPlaceholderExpansion(HardPractice plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "kaos"; // Isso define o prefixo %kaos_...%
    }

    @Override
    public @NotNull String getAuthor() {
        return "Emmy";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true; // Mantém registrado mesmo após /papi reload
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) return "";

        ProfileService profileService = plugin.getService(ProfileService.class);
        Profile profile = profileService != null ? profileService.getProfile(player.getUniqueId()) : null;

        // %kaos_level%
        if (params.equalsIgnoreCase("level")) {
            if (profile != null && profile.getProfileData() != null) {
                return LevelBadgeUtil.getDisplayBadge(player, profile.getProfileData().getExperience());
            }
            return "0";
        }

        // %kaos_dot_animation% ou %kaos_dot-animation%
        if (params.equalsIgnoreCase("dot-animation") || params.equalsIgnoreCase("dot_animation")) {
            return dotAnimation.getCurrentFrame();
        }

        return null;
    }
}
