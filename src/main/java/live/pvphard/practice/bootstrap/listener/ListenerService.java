package live.pvphard.practice.bootstrap.listener;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.bootstrap.lifecycle.Service;

/**
 * @author Emmy
 * @project hard-practice
 * @since 16/07/2025
 */
public interface ListenerService extends Service {

    /**
     * Registers all listeners for the service.
     * This method should be called during the service initialization phase
     */
    void registerListeners(HardPractice plugin);
}