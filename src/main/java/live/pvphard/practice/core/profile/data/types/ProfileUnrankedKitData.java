package live.pvphard.practice.core.profile.data.types;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.feature.division.Division;
import live.pvphard.practice.feature.division.DivisionService;
import live.pvphard.practice.feature.division.model.DivisionTier;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Emmy
 * @project Hard
 * @since 25/01/2025
 */
@Getter
@Setter
public class ProfileUnrankedKitData {
    private String division;
    private String tier;
    private int wins;
    private int losses;
    private int winstreak;
    private int bestWinstreak;

    public ProfileUnrankedKitData() {
        this.wins = 0;
        this.losses = 0;
        this.winstreak = 0;
        this.bestWinstreak = 0;
        // Não chamamos determineDivision no construtor para evitar loops antes dos dados carregarem
    }

    public void incrementWins() {
        this.winstreak++;
        if (this.winstreak > this.bestWinstreak) {
            this.bestWinstreak = this.winstreak;
        }
        this.wins++;
        this.determineDivision();
    }

    public void incrementLosses() {
        this.winstreak = 0;
        this.losses++;
    }

    public void determineDivision() {
        HardPractice plugin = HardPractice.getInstance();
        if (plugin == null) {
            return;
        }

        DivisionService divisionService = plugin.getService(DivisionService.class);
        if (divisionService == null || divisionService.getDivisions() == null) return;

        String bestDivision = null;
        String bestTier = null;
        int bestRequiredWins = -1;

        for (Division divisionObj : divisionService.getDivisions()) {
            if (divisionObj.getTiers() == null) continue;

            for (DivisionTier tierObj : divisionObj.getTiers()) {
                // Only consider tiers the player has actually reached, and
                // keep the one with the highest requiredWins threshold met.
                if (this.wins >= tierObj.getRequiredWins() && tierObj.getRequiredWins() > bestRequiredWins) {
                    bestRequiredWins = tierObj.getRequiredWins();
                    bestDivision = divisionObj.getName();
                    bestTier = tierObj.getName();
                }
            }
        }

        if (bestDivision != null) {
            this.division = bestDivision;
            this.tier = bestTier;
        }
    }

    /**
     * Gets the division.
     *
     * @return The division ou null se não encontrada.
     */
    public Division getDivision() {
        if (this.division == null) return null;

        HardPractice plugin = HardPractice.getInstance();
        if (plugin == null) {
            return null;
        }

        DivisionService divisionService = plugin.getService(DivisionService.class);
        return (divisionService != null) ? divisionService.getDivision(this.division) : null;
    }

    /**
     * Gets the division tier.
     *
     * @return The division tier ou null se a divisão for nula.
     */
    public DivisionTier getTier() {
        Division divisionObj = this.getDivision();

        // TRAVA DE SEGURANÇA: Se a divisão for nula ou não tiver tiers, retorna nulo em vez de crashar
        if (divisionObj == null || divisionObj.getTiers() == null || this.tier == null) {
            return null;
        }

        return divisionObj.getTiers().stream()
                .filter(t -> t != null && t.getName() != null && t.getName().equals(this.tier))
                .findFirst()
                .orElse(null);
    }
}
