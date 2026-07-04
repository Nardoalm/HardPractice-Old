package live.pvphard.practice.adapter.core;

import lombok.Getter;

/**
 * @author Emmy
 * @project Hard
 * @since 26/04/2025
 */
@Getter
public enum CoreType {
    DEFAULT("Default", "Revere Inc.", "1.0"),
    KAOSCORE("KaosCore", "ysubz", "14.1"),
    PHOENIX("Phoenix", "Refine Development", "1.0"),
    AQUA("AquaCore", "Activated_, FaceSlap_", "1.0"),
    HELIUM("Helium", "Plasma Services", "1.0"),

    ;

    private final String pluginName;
    private final String pluginAuthor;
    private final String pluginVersion;

    /**
     * Constructor for the EnumCoreType enum.
     *
     * @param pluginName   The name of the bootstrap.
     * @param pluginAuthor The author of the bootstrap.
     * @param pluginVersion The author of the bootstrap.
     */
    CoreType(String pluginName, String pluginAuthor, String pluginVersion) {
        this.pluginVersion = pluginVersion;
        this.pluginName = pluginName;
        this.pluginAuthor = pluginAuthor;
    }
}
