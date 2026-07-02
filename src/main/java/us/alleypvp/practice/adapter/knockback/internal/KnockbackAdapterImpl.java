package us.alleypvp.practice.adapter.knockback.internal;

import us.alleypvp.practice.AlleyPractice;
import us.alleypvp.practice.adapter.knockback.Knockback;
import us.alleypvp.practice.adapter.knockback.KnockbackAdapter;
import us.alleypvp.practice.adapter.knockback.KnockbackType;
import us.alleypvp.practice.bootstrap.KaosContext;
import us.alleypvp.practice.bootstrap.annotation.Service;
import lombok.Getter;

/**
 * @author Emmy
 * @project Alley
 * @since 26/04/2025
 */
@Getter
@Service(provides = KnockbackAdapter.class, priority = 50)
public class KnockbackAdapterImpl implements KnockbackAdapter {
    private final AlleyPractice plugin;
    private Knockback knockbackImplementation;

    /**
     * Constructor for DI. Receives the main bootstrap instance.
     */
    public KnockbackAdapterImpl(AlleyPractice plugin) {
        this.plugin = plugin;
    }

    @Override
    public void initialize(KaosContext context) {
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