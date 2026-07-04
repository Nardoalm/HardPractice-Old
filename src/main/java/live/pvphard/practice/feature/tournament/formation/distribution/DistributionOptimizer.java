package live.pvphard.practice.feature.tournament.formation.distribution;

import java.util.List;

public interface DistributionOptimizer {
    List<Integer> findOptimalDistribution(List<List<Integer>> possibleDistributions);
}