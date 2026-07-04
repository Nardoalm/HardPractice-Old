package live.pvphard.practice.adapter.core;

import live.pvphard.practice.bootstrap.lifecycle.Service;

public interface CoreAdapter extends Service {

    /**
     * Gets the active core implementation that was detected during startup.
     *
     * @return The Core implementation for the currently enabled core bootstrap.
     */
    Core getCore();
}