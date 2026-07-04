package live.pvphard.practice.feature.match.model;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.core.locale.LocaleService;
import live.pvphard.practice.core.locale.internal.impl.SettingsLocaleImpl;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Remi
 * @project Hard
 * @date 5/27/2024
 */
@Getter
@Setter
public class MatchGamePlayerData {
    private int lives;
    private int score;
    private int kills;
    private int deaths;

    private int longestCombo;
    private int combo;

    private int hits;
    private int criticalHits;
    private int blockedHits;

    private int missedPotions;
    private int thrownPotions;

    private int wTaps;

    private BaseRaiderRole role;

    public MatchGamePlayerData() {
        this.lives = HardPractice.getInstance().getService(LocaleService.class).getInt(SettingsLocaleImpl.GAME_LIVES_PER_MATCH);
        this.score = 0;
    }

    /**
     * Method to handle an attack.
     */
    public void handleAttack() {
        this.hits++;
        this.combo++;

        if (this.combo > this.longestCombo) {
            this.longestCombo = this.combo;
        }
    }

    /**
     * Method to reset the combo.
     */
    public void resetCombo() {
        this.combo = 0;
    }

    public void incrementScore() {
        this.score++;
    }

    public void incrementKills() {
        this.kills++;
    }

    public void incrementDeaths() {
        this.deaths++;
    }

    public void incrementMissedPotions() {
        this.missedPotions++;
    }

    public void incrementThrownPotions() {
        this.thrownPotions++;
    }

    public void incrementCriticalHits() {
        this.criticalHits++;
    }

    public void incrementBlockedHits() {
        this.blockedHits++;
    }

    public void incrementWTaps() {
        this.wTaps++;
    }
}