package live.pvphard.practice.adapter.placeholder;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.bootstrap.lifecycle.Service;

/**
 * @author Emmy
 * @project hard-practice
 * @since 17/07/2025
 */
public interface PlaceholderService extends Service {
    /**
     * Registers a papi expansion bootstrap with the Hard bootstrap.
     *
     * @param plugin The Hard bootstrap instance to register.
     */
    void registerExpansion(HardPractice plugin);
}
