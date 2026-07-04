package live.pvphard.practice.feature.match.internal.types;

import live.pvphard.practice.feature.arena.Arena;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.feature.queue.Queue;
import live.pvphard.practice.feature.match.model.internal.MatchGamePlayer;
import live.pvphard.practice.feature.match.model.GameParticipant;
import live.pvphard.practice.common.PlayerUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @author Remi
 * @project hard-practice
 * @date 25/06/2025
 */
public class CheckpointMatch extends DefaultMatch {
    private final GameParticipant<MatchGamePlayer> participantA;
    private final GameParticipant<MatchGamePlayer> participantB;

    /**
     * Constructor for the MatchRegularImpl class.
     *
     * @param queue        The queue of the match.
     * @param kit          The kit of the match.
     * @param arena        The arena of the match.
     * @param ranked       Whether the match is ranked or not.
     * @param participantA The first participant.
     * @param participantB The second participant.
     */
    public CheckpointMatch(Queue queue, Kit kit, Arena arena, boolean ranked, GameParticipant<MatchGamePlayer> participantA, GameParticipant<MatchGamePlayer> participantB) {
        super(queue, kit, arena, ranked, participantA, participantB);
        this.participantA = participantA;
        this.participantB = participantB;
    }

    @Override
    public boolean canEndRound() {
        return ((this.participantA.isLostCheckpoint() && this.participantA.isAllDead()) || (this.participantB.isLostCheckpoint() && this.participantB.isAllDead()))
                || (this.participantA.getAllPlayers().stream().allMatch(MatchGamePlayer::isDisconnected)
                || this.participantB.getAllPlayers().stream().allMatch(MatchGamePlayer::isDisconnected));
    }

    @Override
    public void handleRespawn(Player player) {
        PlayerUtil.reset(player, true, true);

        MatchGamePlayer gamePlayer = this.getGamePlayer(player);

        Location checkpoint = gamePlayer.getCheckpoint();
        if (checkpoint == null) {
            checkpoint = this.participantA.containsPlayer(player.getUniqueId()) ? getArena().getPos1() : getArena().getPos2();
        }

        player.teleport(checkpoint);

        this.giveLoadout(player, this.getKit());
        this.applyColorKit(player);
    }
}
