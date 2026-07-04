package live.pvphard.practice.api.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public final class PracticeNametagContext {
    private static final PracticeNametagContext EMPTY = new PracticeNametagContext(false, "", "", "", 0);

    private final boolean overrideDefaultNametag;
    private final String prefix;
    private final String suffix;
    private final String nameColor;
    private final int priority;

    public PracticeNametagContext(boolean overrideDefaultNametag, String prefix, String suffix, String nameColor, int priority) {
        this.overrideDefaultNametag = overrideDefaultNametag;
        this.prefix = prefix == null ? "" : prefix;
        this.suffix = suffix == null ? "" : suffix;
        this.nameColor = nameColor == null ? "" : nameColor;
        this.priority = Math.max(0, priority);
    }

    public static PracticeNametagContext empty() {
        return EMPTY;
    }
}
