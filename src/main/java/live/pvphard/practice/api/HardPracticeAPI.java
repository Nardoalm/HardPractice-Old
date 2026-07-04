package live.pvphard.practice.api;

import live.pvphard.practice.api.model.PracticeNametagContext;
import org.bukkit.entity.Player;

public interface HardPracticeAPI {
    boolean isInMatch(Player player);

    boolean isSpectating(Player player);

    boolean isQueueing(Player player);

    boolean isInCombat(Player player);

    boolean isInLobby(Player player);

    String getMatchId(Player player);

    String getMatchMode(Player player);

    String getArenaName(Player player);

    String getTeamId(Player player);

    String getTeamDisplayName(Player player);

    String getTeamColor(Player player);

    boolean shouldOverrideDefaultNametag(Player player);

    PracticeNametagContext getNametagContext(Player player);

    boolean isInEvent(Player player);

    boolean isInParty(Player player);

    String getPartyId(Player player);

    String getPartyRole(Player player);
}
