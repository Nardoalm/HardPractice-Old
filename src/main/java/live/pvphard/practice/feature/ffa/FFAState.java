package live.pvphard.practice.feature.ffa;

import lombok.Getter;

/**
 * @author Emmy
 * @project Hard
 * @date 25/05/2024 - 14:25
 */
@Getter
public enum FFAState {
    SPAWN("Spawn", "O jogador está na safezone."),
    FIGHTING("Lutando", "O jogador está lutando fora da safezone.");

    private final String name;
    private final String description;

    /**
     * Constructor for the EnumFFAState enum.
     *
     * @param name        The name of the state.
     * @param description The description of the state.
     */
    FFAState(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
