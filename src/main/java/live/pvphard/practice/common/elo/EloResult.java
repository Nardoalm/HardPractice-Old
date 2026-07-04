package live.pvphard.practice.common.elo;

import lombok.Getter;

/**
 * @author Emmy
 * @project Hard
 * @date 09/11/2024 - 15:24
 */
@Getter
public class EloResult {
    private final int newWinnerElo;
    private final int newLoserElo;

    /**
     * Constructor for the EloResult class.
     *
     * @param newWinnerElo The new elo of the winner.
     * @param newLoserElo  The new elo of the loser.
     */
    public EloResult(int newWinnerElo, int newLoserElo) {
        this.newWinnerElo = newWinnerElo;
        this.newLoserElo = newLoserElo;
    }
}