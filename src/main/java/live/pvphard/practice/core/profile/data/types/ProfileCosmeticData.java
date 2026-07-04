package live.pvphard.practice.core.profile.data.types;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.feature.cosmetic.model.BaseCosmetic;
import live.pvphard.practice.feature.cosmetic.model.CosmeticType;
import lombok.Getter;
import lombok.Setter;

import java.util.EnumMap;
import java.util.Map;

/**
 * @author Remi
 * @project Hard
 * @date 6/1/2024
 */
@Getter
@Setter
public class ProfileCosmeticData {
    protected final HardPractice plugin = HardPractice.getInstance();
    private Map<CosmeticType, String> selectedCosmetics;

    public ProfileCosmeticData() {
        this.selectedCosmetics = new EnumMap<>(CosmeticType.class);

        for (CosmeticType type : CosmeticType.values()) {
            this.selectedCosmetics.put(type, "None");
        }
    }

    /**
     * Sets the active cosmetic for the correct category using its type.
     *
     * @param cosmetic The cosmetic object to select.
     */
    public void setSelected(BaseCosmetic cosmetic) {
        if (cosmetic == null) return;
        this.selectedCosmetics.put(cosmetic.getType(), cosmetic.getName());
    }

    /**
     * Gets the name of the selected cosmetic for a given type.
     *
     * @param type The CosmeticType category to check.
     * @return The name of the selected cosmetic, or "None" if not found.
     */
    public String getSelected(CosmeticType type) {
        return this.selectedCosmetics.getOrDefault(type, "None");
    }

    /**
     * Checks if a specific cosmetic is currently selected.
     *
     * @param cosmetic The cosmetic to check.
     * @return true if it is the currently selected cosmetic for its type.
     */
    public boolean isSelected(BaseCosmetic cosmetic) {
        if (cosmetic == null) return false;
        String selectedName = getSelected(cosmetic.getType());
        return cosmetic.getName().equals(selectedName);
    }

    public String getSelectedKillEffect() {
        return getSelected(CosmeticType.KILL_EFFECT);
    }

    public String getSelectedSoundEffect() {
        return getSelected(CosmeticType.SOUND_EFFECT);
    }

    public String getSelectedProjectileTrail() {
        return getSelected(CosmeticType.PROJECTILE_TRAIL);
    }

    public String getSelectedKillMessage() {
        return getSelected(CosmeticType.KILL_MESSAGE);
    }

    public String getSelectedSuit() {
        return getSelected(CosmeticType.SUIT);
    }

    public String getSelectedCloak() {
        return getSelected(CosmeticType.CLOAK);
    }
}