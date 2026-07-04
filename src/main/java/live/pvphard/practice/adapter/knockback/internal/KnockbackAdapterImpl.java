package live.pvphard.practice.adapter.knockback.internal;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.adapter.knockback.Knockback;
import live.pvphard.practice.adapter.knockback.KnockbackAdapter;
import live.pvphard.practice.adapter.knockback.KnockbackType;
import live.pvphard.practice.bootstrap.HardContext;
import live.pvphard.practice.bootstrap.annotation.Service;
import lombok.Getter;

/**
 * @author Emmy
 * @project Hard
 * @since 26/04/2025
 */
@Getter
@Service(provides = KnockbackAdapter.class, priority = 50)
public class KnockbackAdapterImpl implements KnockbackAdapter {
    private final HardPractice plugin;
    private Knockback knockbackImplementation;

    /**
     * Constructor for DI. Receives the main bootstrap instance.
     */
    public KnockbackAdapterImpl(HardPractice plugin) {
        this.plugin = plugin;
    }

    @Override
    public void initialize(HardContext context) {
        this.knockbackImplementation = this.determineKnockback();
    }

    @Override
    public Knockback getKnockbackImplementation() {
        if (this.knockbackImplementation == null) {
            throw new IllegalStateException("KnockbackAdapter has not been initialized yet.");
        }
        return this.knockbackImplementation;
    }

    /**
     * Determines the knockback implementation to use based on the server's name.
     *
     * @return The selected knockback implementation.
     */
    private Knockback determineKnockback() {
        Knockback selectedImplementation = new DefaultKnockbackImpl();

        for (KnockbackType kbType : KnockbackType.values()) {
            if (this.plugin.getServer().getName().equalsIgnoreCase(kbType.getSpigotName())) {
                break;
            }
        }

        return selectedImplementation;
    }
}