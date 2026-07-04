package live.pvphard.practice.core.profile.enums;

import lombok.Getter;

@Getter
public enum ProfileState {
    LOBBY("Lobby", "The player is in the lobby"),
    WAITING("Waiting", "The player is waiting in the queue for an opponent"),
    PLAYING("Playing", "The player is in a match"),
    FIGHTING_BOT("Fighting Bot", "The player is fighting against a bot"),
    SPECTATING("Spectating", "The player is spectating a match"),
    EDITING("Editing", "The player is editing a kit"),
    PLAYING_TOURNAMENT("Tournament", "The player is in a tournament"),
    PLAYING_EVENT("Event", "The player is in an event"),
    FFA("FFA", "The player is in the FFA lobby"),
    TOURNAMENT_LOBBY("Tournament", "The player is in tournament lobby"),

    ;

    private final String name;
    private final String description;

    /**
     * Constructor for the ProfileState enum.
     *
     * @param name        The name of the profile state.
     * @param description The description of the profile state.
     */
    ProfileState(String name, String description) {
        this.name = name;
        this.description = description;
    }
}