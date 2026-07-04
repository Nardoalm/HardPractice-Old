package live.pvphard.practice.feature.cosmetic.model;

import java.util.List;

/**
 * @author Remi
 * @project Hard
 * @date 6/1/2024
 */
public interface CosmeticRepository<T extends Cosmetic> {
    List<T> getCosmetics();

    /**
     * Retrieves a cosmetic by its name.
     *
     * @param name The name of the cosmetic to retrieve.
     * @return The cosmetic with the specified name, or null if not found.
     */
    T getCosmetic(String name);

    /**
     * Add this method to the interface. It declares the category of the repository.
     *
     * @return The CosmeticType that this repository manages.
     */
    CosmeticType getRepositoryType();
}