package live.pvphard.practice.feature.match.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Remi
 * @project Hard
 * @date 5/21/2024
 */
public class TeamGameParticipant<T extends GamePlayer> extends GameParticipant<T> {
    private final List<T> players;

    /**
     * Constructor for the TeamGameParticipant class.
     *
     * @param t The player.
     */
    public TeamGameParticipant(T t) {
        super(t);

        this.players = new ArrayList<>();
        this.players.add(t);
    }

    /**
     * Method to retrieve the players in the team participant who are not disconnected.
     *
     * @return the list of players in the team participant.
     */
    @Override
    public List<T> getPlayers() {
        return this.players.stream()
                .filter(player -> !player.isDisconnected())
                .collect(Collectors.toList());
    }

    /**
     * Method to retrieve the players in the team participant who are not disconnected.
     *
     * @return the list of players in the team participant.
     */
    @Override
    public List<T> getAllPlayers() {
        return this.players;
    }

    /**
     * Gets the size of all players added to the list of players in the team participant.
     *
     * @return the size of the player list.
     */
    @Override
    public int getPlayerSize() {
        return this.players.size();
    }

    /**
     * Adds a player to the team participant.
     *
     * @param t the player to add.
     */
    @Override
    public void addPlayer(T t) {
        if (t == null || this.players.contains(t)) {
            return;
        }

        this.players.add(t);
    }

    /**
     * Removes a player from the team participant.
     *
     * @param player The player to remove.
     */
    @Override
    public void removePlayer(T player) {
        this.players.remove(player);
    }

    /**
     * Gets the amount of players that are alive in the team participant.
     *
     * @return the amount of alive players in the team participant.
     */
    @Override
    public int getAlivePlayerSize() {
        int i = 0;

        for (GamePlayer gamePlayer : this.players) {
            if (!gamePlayer.isDead() && !gamePlayer.isDisconnected()) {
                i++;
            }
        }

        return i;
    }

    /**
     * Checks if all players in the team participant are dead or disconnected.
     *
     * @return true if all players are dead or disconnected, false otherwise.
     */
    @Override
    public boolean isAllDead() {
        int i = 0;

        for (GamePlayer gamePlayer : this.players) {
            if (gamePlayer.isDead() || gamePlayer.isDisconnected()) {
                i++;
            }
        }

        return this.players.size() == i;
    }

    @Override
    public boolean isAllEliminated() {
        int i = 0;

        for (GamePlayer gamePlayer : this.players) {
            if (gamePlayer.isEliminated()) {
                i++;
            }
        }

        return this.players.size() == i;
    }

    /**
     * Method to determine whether the provided UUID is contained within the team participant's player list.
     *
     * @param uuid The UUID of the player.
     * @return true if the participant contains the player, false otherwise.
     */
    @Override
    public boolean containsPlayer(UUID uuid) {
        for (GamePlayer gamePlayer : this.players) {
            if (gamePlayer.getUuid().equals(uuid)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the conjoined names of all players in the team participant.
     *
     * @return a string containing the conjoined names of the players.
     */
    @Override
    public String getConjoinedNames() {
        StringBuilder builder = new StringBuilder();

        int size = this.players.size();
        if (size == 1) {
            return this.players.get(0).getUsername();
        }

        for (int i = 0; i < size; i++) {
            builder.append(this.players.get(i).getUsername());

            if (i == size - 2) {
                builder.append(" and ");
            } else if (i < size - 2) {
                builder.append(", ");
            }
        }

        return builder.toString();
    }
}