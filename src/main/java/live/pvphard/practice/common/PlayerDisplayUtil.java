package live.pvphard.practice.common;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.adapter.core.CoreAdapter;
import live.pvphard.practice.adapter.core.kaoscore.KaosCoreBridge;
import live.pvphard.practice.common.text.CC;
import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Utility methods for resolving player-facing identity and visibility placeholders.
 */
@UtilityClass
public class PlayerDisplayUtil {
    private static final String TAG_PREFIX_PLACEHOLDER = "%kaoscore_tag_prefix%";
    private static final String NICK_PLACEHOLDER = "%kaoscore_player-nick%";
    private static final String VANISH_PLACEHOLDER = "%kaoscore_player-isvanish%";
    private static final String CLAN_TAG_PLACEHOLDER = "%kaoscore_clan_tag%";

    public String resolveDisplayName(Player player) {
        String fallback = player != null ? player.getName() : "Unknown";
        return resolveDisplayName(player, fallback);
    }

    public String resolveDisplayName(Player player, String fallback) {
        String nick = resolveCurrentNick(player, fallback);
        String tagPrefix = resolveTagPrefix(player);
        return CC.translate(tagPrefix + nick).trim();
    }

    public String resolveTagColoredNick(Player player) {
        String fallback = player != null ? player.getName() : "Unknown";
        return resolveTagColoredNick(player, fallback);
    }

    public String resolveTagColoredNick(Player player, String fallback) {
        String nick = resolveCurrentNick(player, fallback);
        return (resolveTagColor(player) + nick).trim();
    }

    public String resolveCurrentNick(Player player) {
        String fallback = player != null ? player.getName() : "Unknown";
        return resolveCurrentNick(player, fallback);
    }

    public String resolveCurrentNick(Player player, String fallback) {
        String safeFallback = sanitizeFallback(fallback);
        if (player == null) {
            return safeFallback;
        }

        KaosCoreBridge kaosCoreBridge = getKaosCoreBridge();
        if (kaosCoreBridge != null) {
            String currentNick = kaosCoreBridge.resolveCurrentNick(player, safeFallback);
            if (currentNick != null && !currentNick.trim().isEmpty()) {
                return CC.translate(currentNick).trim();
            }
        }

        String resolved = PlaceholderUtil.setPapiSafe(player, NICK_PLACEHOLDER);
        if (resolved == null) {
            return safeFallback;
        }

        String cleaned = CC.translate(resolved).trim();
        if (cleaned.isEmpty() || cleaned.equalsIgnoreCase(NICK_PLACEHOLDER)) {
            return safeFallback;
        }

        return cleaned;
    }

    public String resolveTagPrefix(Player player) {
        if (player == null) {
            return "";
        }

        KaosCoreBridge kaosCoreBridge = getKaosCoreBridge();
        if (kaosCoreBridge != null) {
            String tagPrefix = kaosCoreBridge.getTagPrefix(player);
            if (tagPrefix != null && !tagPrefix.trim().isEmpty()) {
                return CC.translate(tagPrefix);
            }
        }

        String resolved = PlaceholderUtil.setPapiSafe(player, TAG_PREFIX_PLACEHOLDER);
        if (resolved == null) {
            return "";
        }

        String cleaned = CC.translate(resolved);
        if (cleaned.isEmpty() || cleaned.equalsIgnoreCase(TAG_PREFIX_PLACEHOLDER)) {
            return "";
        }

        return cleaned;
    }

    public boolean isVanished(Player player) {
        if (player == null) {
            return false;
        }

        KaosCoreBridge kaosCoreBridge = getKaosCoreBridge();
        if (kaosCoreBridge != null && kaosCoreBridge.isVanished(player)) {
            return true;
        }

        String resolved = PlaceholderUtil.setPapiSafe(player, VANISH_PLACEHOLDER);
        if (resolved == null) {
            return false;
        }

        String cleaned = ChatColor.stripColor(CC.translate(resolved)).trim();
        if (cleaned.isEmpty() || cleaned.equalsIgnoreCase(VANISH_PLACEHOLDER)) {
            return false;
        }

        return cleaned.equalsIgnoreCase("true")
                || cleaned.equalsIgnoreCase("yes")
                || cleaned.equalsIgnoreCase("on")
                || cleaned.equals("1");
    }

    public String resolveClanTag(Player player) {
        if (player == null) {
            return "";
        }

        KaosCoreBridge kaosCoreBridge = getKaosCoreBridge();
        if (kaosCoreBridge != null) {
            String clanTag = kaosCoreBridge.getClanTag(player);
            if (clanTag != null && !clanTag.trim().isEmpty()) {
                return CC.translate(clanTag);
            }
        }

        String resolved = PlaceholderUtil.setPapiSafe(player, CLAN_TAG_PLACEHOLDER);
        if (resolved == null) {
            return "";
        }

        String cleaned = CC.translate(resolved);
        if (cleaned.isEmpty() || cleaned.equalsIgnoreCase(CLAN_TAG_PLACEHOLDER)) {
            return "";
        }

        return cleaned;
    }

    private String sanitizeFallback(String fallback) {
        if (fallback == null || fallback.trim().isEmpty()) {
            return "Unknown";
        }

        return fallback;
    }

    private KaosCoreBridge getKaosCoreBridge() {
        HardPractice instance = HardPractice.getInstance();
        if (instance == null) {
            return null;
        }

        try {
            return instance.getService(KaosCoreBridge.class);
        } catch (Exception ignored) {
            return null;
        }
    }

    private ChatColor resolveTagColor(Player player) {
        if (player == null) {
            return ChatColor.WHITE;
        }

        HardPractice instance = HardPractice.getInstance();
        if (instance != null) {
            try {
                CoreAdapter coreAdapter = instance.getService(CoreAdapter.class);
                if (coreAdapter != null && coreAdapter.getCore() != null) {
                    ChatColor tagColor = coreAdapter.getCore().getTagColor(player);
                    if (tagColor != null && tagColor != ChatColor.RESET) {
                        return tagColor;
                    }
                }
            } catch (Exception ignored) {
            }
        }

        KaosCoreBridge kaosCoreBridge = getKaosCoreBridge();
        if (kaosCoreBridge != null) {
            ChatColor tagColor = kaosCoreBridge.getTagColor(player);
            if (tagColor != null && tagColor != ChatColor.RESET) {
                return tagColor;
            }
        }

        return ChatColor.WHITE;
    }
}
