package live.pvphard.practice.feature.party;

import live.pvphard.practice.feature.arena.Arena;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.bootstrap.lifecycle.Service;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

/**
 * @author Remi
 * @project hard-practice
 * @date 2/07/2025
 */
public interface PartyService extends Service {
    /**
     * Gets a list of all currently active parties.
     *
     * @return An unmodifiable list of active parties.
     */
    List<Party> getParties();

    /**
     * Creates a new party with the given player as the leader.
     *
     * @param player The player creating the party.
     */
    void createParty(Player player);

    /**
     * Disbands a party, kicking all members. This can only be done by the leader.
     *
     * @param leader The leader of the party to disband.
     */
    void disbandParty(Player leader);

    /**
     * Allows a player to leave the party they are currently in.
     * If they are the leader, the party is disbanded.
     *
     * @param player The player leaving the party.
     */
    void leaveParty(Player player);

    /**
     * Kicks a member from the party. Can only be initiated by the party leader.
     *
     * @param leader The party leader.
     * @param member The player to kick.
     */
    void kickMember(Player leader, Player member);

    /**
     * Bans a player from the party, preventing them from rejoining.
     *
     * @param leader The party leader.
     * @param target The player to ban.
     */
    void banMember(Player leader, Player target);

    /**
     * Unbans a player from the party.
     *
     * @param leader The party leader.
     * @param target The player to unban.
     */
    void unbanMember(Player leader, Player target);

    /**
     * Allows a player to join an existing party.
     *
     * @param player The player joining.
     * @param leader The leader of the party to join.
     */
    void joinParty(Player player, Player leader);

    /**
     * Sends a party invitation from a sender to a target player.
     *
     * @param party  The party instance.
     * @param sender The player sending the invite.
     * @param target The player receiving the invite.
     */
    boolean sendInvite(Party party, Player sender, Player target);

    /**
     * Gets the party that a player is the leader of.
     *
     * @param player The potential leader.
     * @return The Party object, or null if they are not a leader.
     */
    Party getPartyByLeader(Player player);

    /**
     * Gets the party that a player is a member of.
     *
     * @param uuid The UUID of the potential member.
     * @return The Party object, or null if they are not in a party.
     */
    Party getPartyByMember(UUID uuid);

    /**
     * Gets the party a player is in, regardless of their role (leader or member).
     *
     * @param player The player.
     * @return The Party object, or null if they are not in a party.
     */
    Party getParty(Player player);

    /**
     * Starts a 2v2 party match.
     *
     * @param kit   The kit for the match.
     * @param arena The arena for the match.
     * @param party The party starting the match.
     */
    void startMatch(Kit kit, Arena arena, Party party);

    /**
     * Announces a party to the entire server, inviting players to join.
     *
     * @param party The party to announce.
     */
    void announceParty(Party party);

    List<PartyRequest> getPartyRequests();

    PartyRequest getRequest(Player player);

    void removeRequest(PartyRequest request);

    /**
     * Gets the chat format
     *
     * @return The chat format
     */
    String getChatFormat();

    /**
     * Formats a party chat message using the configured format.
     *
     * @param player  The player sending the message.
     * @param message The raw message content.
     * @return The formatted party chat message.
     */
    String formatPartyChatMessage(Player player, String message);
}
