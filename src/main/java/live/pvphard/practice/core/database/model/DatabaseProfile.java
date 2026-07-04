package live.pvphard.practice.core.database.model;

import live.pvphard.practice.core.profile.Profile;

/**
 * @author Remi
 * @project Hard
 * @date 5/22/2024
 */
public interface DatabaseProfile {
    /**
     * Gets the type of the database profile.
     *
     * @return The type as a string.
     */
    DatabaseType getType();

    /**
     * Saves a profile to the database.
     *
     * @param profile The profile to save.
     */
    void saveProfile(Profile profile);

    /**
     * Loads a profile from the database.
     *
     * @param profile The profile to load.
     */
    void loadProfile(Profile profile);

    /**
     * Archives a profile in the database.
     *
     * @param profile The profile to archive.
     */
    void archiveProfile(Profile profile);
}
