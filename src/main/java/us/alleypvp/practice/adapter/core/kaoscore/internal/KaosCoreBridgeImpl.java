package us.alleypvp.practice.adapter.core.kaoscore.internal;

import us.alleypvp.practice.AlleyPractice;
import us.alleypvp.practice.adapter.core.kaoscore.KaosCoreBridge;
import us.alleypvp.practice.bootstrap.annotation.Service;
import com.knowplugins.ysubz.kaoscore.api.KaosCoreAPI;
import com.knowplugins.ysubz.kaoscore.clan.Clan;
import com.knowplugins.ysubz.kaoscore.utils.tag.Tag;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

@Service(provides = KaosCoreBridge.class, priority = 55)
public class KaosCoreBridgeImpl implements KaosCoreBridge {
    private final AlleyPractice plugin;
    private KaosCoreAPI api;

    public KaosCoreBridgeImpl(AlleyPractice plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isAvailable() {
        return this.resolveApi() != null;
    }

    @Override
    public boolean isDisguised(Player player) {
        KaosCoreAPI resolvedApi = this.resolveApi();
        if (resolvedApi == null || player == null) {
            return false;
        }

        try {
            return resolvedApi.isDisguised(player);
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public String resolveCurrentNick(Player player, String fallback) {
        String safeFallback = sanitizeFallback(fallback, player);
        KaosCoreAPI resolvedApi = this.resolveApi();
        if (resolvedApi == null || player == null) {
            return safeFallback;
        }

        try {
            if (resolvedApi.isDisguised(player)) {
                String disguiseNick = resolvedApi.getDisguiseNick(player);
                if (isUsable(disguiseNick)) {
                    return disguiseNick.trim();
                }
            }

            String originalNick = resolvedApi.getOriginalNick(player);
            if (isUsable(originalNick)) {
                return originalNick.trim();
            }
        } catch (Exception ignored) {
            return safeFallback;
        }

        return safeFallback;
    }

    @Override
    public boolean isVanished(Player player) {
        KaosCoreAPI resolvedApi = this.resolveApi();
        if (resolvedApi == null || player == null) {
            return false;
        }

        try {
            return resolvedApi.isVanished(player);
        } catch (Exception ignored) {
            return false;
        }
    }

    //  todo: pegar o rank

    @Override
    public void removeVanish(Player player) {
        KaosCoreAPI resolvedApi = this.resolveApi();
        if (resolvedApi == null || player == null) {
            return;
        }

        try {
            if (resolvedApi.isVanished(player)) {
                resolvedApi.removeVanish(player);
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public String getTagPrefix(Player player) {
        Tag tag = this.resolveTag(player);
        return tag != null && tag.getPrefix() != null ? tag.getPrefix() : "";
    }

    @Override
    public String getTagName(Player player) {
        Tag tag = this.resolveTag(player);
        return tag != null && tag.getName() != null ? tag.getName() : "";
    }

    @Override
    public String getTagSuffix(Player player) {
        Tag tag = this.resolveTag(player);
        return tag != null && tag.getSuffix() != null ? tag.getSuffix() : "";
    }

    @Override
    public ChatColor getTagColor(Player player) {
        Tag tag = this.resolveTag(player);
        if (tag == null) {
            return ChatColor.WHITE;
        }

        return parseColor(tag.getColor());
    }

    @Override
    public int getTagPosition(Player player) {
        Tag tag = this.resolveTag(player);
        if (tag == null) {
            return 9999;
        }

        return Math.max(0, Math.min(9999, tag.getPosition()));
    }

    @Override
    public String getClanTag(Player player) {
        KaosCoreAPI resolvedApi = this.resolveApi();
        Clan clan = safeGetClan(resolvedApi, player);
        if (clan == null) {
            return "";
        }

        String formattedTag = clan.getFormattedTag();
        if (isUsable(formattedTag)) {
            return formattedTag.trim();
        }

        String tag = clan.getTag();
        return isUsable(tag) ? tag.trim() : "";
    }

    @Override
    public void applyClanMatchResult(Player winner, Player loser, boolean ranked, int winnerEloDelta, int loserEloDelta) {
        KaosCoreAPI resolvedApi = this.resolveApi();
        if (resolvedApi == null) {
            return;
        }

        Clan winnerClan = safeGetClan(resolvedApi, winner);
        Clan loserClan = safeGetClan(resolvedApi, loser);

        if (sameClan(winnerClan, loserClan)) {
            return;
        }

        updateWinnerClan(resolvedApi, winnerClan, ranked, Math.max(0, winnerEloDelta));
        updateLoserClan(resolvedApi, loserClan, ranked, Math.max(0, loserEloDelta));
    }

    private KaosCoreAPI resolveApi() {
        if (this.api != null) {
            return this.api;
        }

        if (!this.plugin.getServer().getPluginManager().isPluginEnabled("KaosCore")) {
            return null;
        }

        RegisteredServiceProvider<KaosCoreAPI> registration = Bukkit.getServicesManager().getRegistration(KaosCoreAPI.class);
        if (registration == null) {
            return null;
        }

        this.api = registration.getProvider();
        return this.api;
    }

    private Tag resolveTag(Player player) {
        KaosCoreAPI resolvedApi = this.resolveApi();
        if (resolvedApi == null || player == null) {
            return null;
        }

        try {
            return resolvedApi.getTag(player);
        } catch (Exception ignored) {
            return null;
        }
    }

    private Clan safeGetClan(KaosCoreAPI resolvedApi, Player player) {
        if (resolvedApi == null || player == null) {
            return null;
        }

        try {
            return resolvedApi.getClan(player);
        } catch (Exception ignored) {
            return null;
        }
    }

    private void updateWinnerClan(KaosCoreAPI resolvedApi, Clan winnerClan, boolean ranked, int winnerEloDelta) {
        if (resolvedApi == null || winnerClan == null) {
            return;
        }

        int updatedElo = ranked ? winnerClan.getElo() + winnerEloDelta : winnerClan.getElo();
        int updatedWins = winnerClan.getWins() + 1;
        resolvedApi.updateClanStats(winnerClan, updatedElo, updatedWins, winnerClan.getLosses());
    }

    private void updateLoserClan(KaosCoreAPI resolvedApi, Clan loserClan, boolean ranked, int loserEloDelta) {
        if (resolvedApi == null || loserClan == null) {
            return;
        }

        int updatedElo = ranked ? Math.max(0, loserClan.getElo() - loserEloDelta) : loserClan.getElo();
        int updatedLosses = loserClan.getLosses() + 1;
        resolvedApi.updateClanStats(loserClan, updatedElo, loserClan.getWins(), updatedLosses);
    }

    private boolean sameClan(Clan first, Clan second) {
        if (first == null || second == null) {
            return false;
        }

        String firstName = first.getName();
        String secondName = second.getName();
        return firstName != null && secondName != null && firstName.equalsIgnoreCase(secondName);
    }

    private ChatColor parseColor(String rawColor) {
        if (!isUsable(rawColor)) {
            return ChatColor.WHITE;
        }

        String translated = ChatColor.translateAlternateColorCodes('&', rawColor);
        for (int index = translated.length() - 1; index >= 0; index--) {
            if (translated.charAt(index) == ChatColor.COLOR_CHAR && index + 1 < translated.length()) {
                ChatColor parsed = ChatColor.getByChar(translated.charAt(index + 1));
                if (parsed != null) {
                    return parsed;
                }
            }
        }

        return ChatColor.WHITE;
    }

    private String sanitizeFallback(String fallback, Player player) {
        if (isUsable(fallback)) {
            return fallback.trim();
        }

        if (player != null && isUsable(player.getName())) {
            return player.getName();
        }

        return "Unknown";
    }

    private boolean isUsable(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
