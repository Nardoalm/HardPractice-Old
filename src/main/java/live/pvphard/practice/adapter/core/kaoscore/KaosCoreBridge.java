package live.pvphard.practice.adapter.core.kaoscore;

import live.pvphard.practice.bootstrap.lifecycle.Service;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Centraliza o acesso opcional ao KaosCore para evitar dependência espalhada.
 */
public interface KaosCoreBridge extends Service {
    boolean isAvailable();

    boolean isDisguised(Player player);

    String resolveCurrentNick(Player player, String fallback);

    boolean isVanished(Player player);

    void removeVanish(Player player);

    String getTagPrefix(Player player);

    String getTagName(Player player);

    String getTagSuffix(Player player);

    ChatColor getTagColor(Player player);

    int getTagPosition(Player player);

    String getClanTag(Player player);

    void applyClanMatchResult(Player winner, Player loser, boolean ranked, int winnerEloDelta, int loserEloDelta);
}
