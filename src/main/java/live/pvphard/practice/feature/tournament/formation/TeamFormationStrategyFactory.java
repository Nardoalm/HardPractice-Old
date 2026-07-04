package live.pvphard.practice.feature.tournament.formation;

import live.pvphard.practice.feature.tournament.formation.strategy.BalancedTeamFormationStrategy;
import live.pvphard.practice.feature.tournament.formation.strategy.LegacyTeamFormationStrategy;

public class TeamFormationStrategyFactory {
    public enum StrategyType {
        BALANCED, LEGACY
    }

    public static TeamFormationStrategy createStrategy(StrategyType type) {
        switch (type) {
            case BALANCED:
                return new BalancedTeamFormationStrategy();
            case LEGACY:
                return new LegacyTeamFormationStrategy();
            default:
                throw new IllegalArgumentException("Unknown strategy type: " + type);
        }
    }
}