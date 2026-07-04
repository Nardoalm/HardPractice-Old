package live.pvphard.practice.bootstrap.lifecycle;

import live.pvphard.practice.bootstrap.HardContext;

/**
 * @author Remi
 * @project hard-practice
 * @date 2/07/2025
 */
public interface Service {
    /**
     * Called by the HardContext *after* the service instance has been created
     * and its server dependencies are available, but not necessarily fully initialized.
     *
     * @param context The application context, for access to the bootstrap instance or other services.
     */
    default void setup(HardContext context) {
        // Default implementation: no-op
    }

    /**
     * Called by the HardContext *after* all services have been created and setup.
     * Use this for logic that requires other services to be fully operational,
     * such as registering listeners or loading data from other services.
     *
     * @param context The application context.
     */
    default void initialize(HardContext context) {
        // Default implementation: no-op
    }

    /**
     * Called by the HardContext during bootstrap shutdown.
     * Should be used to release resources, save data, etc.
     *
     * @param context The application context.
     */
    default void shutdown(HardContext context) {
        // Default implementation: no-op
    }
}