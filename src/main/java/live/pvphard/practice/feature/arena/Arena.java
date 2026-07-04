package live.pvphard.practice.feature.arena;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Emmy
 * @project Hard
 * @date 20/05/2024 - 16:42
 */
@Getter
@Setter
public abstract class Arena {
    private final String name;
    private String displayName;

    private boolean enabled;

    private ArenaType type;

    private Location pos1;
    private Location pos2;

    private Location center;

    private Location minimum;
    private Location maximum;

    private List<String> kits = new ArrayList<>();
    private List<String> events = new ArrayList<>();

    /**
     * Constructor for the Arena class.
     *
     * @param name    The name of the arena.
     * @param minimum The minimum location of the arena.
     * @param maximum The maximum location of the arena.
     */
    public Arena(String name, Location minimum, Location maximum) {
        this.name = name;
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public abstract void saveArena();

    public abstract void createArena();

    public abstract void deleteArena();
}
