package live.pvphard.practice.feature.level.data;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

/**
 * @author Emmy
 * @project Hard
 * @since 22/04/2025
 */
@Getter
@Setter
public class LevelData {
    private final String name;
    private String displayName;

    private Material material;
    private int durability;

    private int minElo;
    private int maxElo;

    /**
     * Constructor for the LevelData class.
     *
     * @param name        The name of the level.
     * @param displayName The display name of the level.
     * @param material    The material associated with this level.
     * @param durability  The durability of the material.
     * @param minElo      The minimum Elo rating for this level.
     * @param maxElo      The maximum Elo rating for this level.
     */
    public LevelData(String name, String displayName, Material material, int durability, int minElo, int maxElo) {
        this.name = name;
        this.displayName = displayName;
        this.material = material;
        this.durability = durability;
        this.minElo = minElo;
        this.maxElo = maxElo;
    }
}