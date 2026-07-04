package live.pvphard.practice.feature.tournament.validation;

import org.bukkit.entity.Player;
import live.pvphard.practice.bootstrap.lifecycle.Service;
import live.pvphard.practice.feature.tournament.model.Tournament;

/**
 * @author Remi
 * @project hard-practice
 * @date 8/08/2025
 */
public interface ParticipantValidator extends Service {
    /**
     * Validates if a player can join a tournament.
     *
     * @param player     The player attempting to join.
     * @param tournament The tournament to validate against.
     * @return True if player can join.
     */
    boolean canPlayerJoin(Player player, Tournament tournament);

    /**
     * Validates if a player's party has space in the tournament.
     *
     * @param player     The player whose party to check.
     * @param tournament The tournament to check space in.
     * @return True if there's space.
     */
    boolean hasSpaceForParty(Player player, Tournament tournament);
}