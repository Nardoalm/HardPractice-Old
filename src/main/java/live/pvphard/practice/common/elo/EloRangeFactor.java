package live.pvphard.practice.common.elo;

import lombok.Getter;

/**
 * @author Remi
 * @project Hard
 * @date 6/2/2024
 */
@Getter
public class EloRangeFactor {
    private final int lowerBound;
    private final int upperBound;
    private final int factor;

    /**
     * Constructor for the EloRangeFactor class.
     *
     * @param lowerBound The lower bound of the range.
     * @param upperBound The upper bound of the range.
     * @param factor     The factor for the range.
     */
    public EloRangeFactor(int lowerBound, int upperBound, int factor) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.factor = factor;
    }

    /**
     * Method to check if the elo is in range.
     *
     * @param elo The elo to check.
     * @return If the elo is in range.
     */
    public boolean isInRange(int elo) {
        return elo >= this.lowerBound && elo <= this.upperBound;
    }
}