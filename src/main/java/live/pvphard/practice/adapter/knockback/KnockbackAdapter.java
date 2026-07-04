package live.pvphard.practice.adapter.knockback;

import live.pvphard.practice.bootstrap.lifecycle.Service;

public interface KnockbackAdapter extends Service {
    /**
     * Gets the active knockback implementation that was detected during startup.
     *
     * @return The IKnockback implementation for the current server type.
     */
    Knockback getKnockbackImplementation();
}