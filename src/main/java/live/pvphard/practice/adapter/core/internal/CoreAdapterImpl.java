package live.pvphard.practice.adapter.core.internal;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.adapter.core.Core;
import live.pvphard.practice.adapter.core.CoreAdapter;
import live.pvphard.practice.adapter.core.CoreType;
import live.pvphard.practice.adapter.core.kaoscore.KaosCoreBridge;
import live.pvphard.practice.bootstrap.HardContext;
import live.pvphard.practice.bootstrap.annotation.Service;
import lombok.Getter;
import me.activated.core.plugin.AquaCoreAPI;
import services.plasma.helium.api.HeliumAPI;
import xyz.refinedev.phoenix.SharedAPI;

/**
 * @author Emmy
 * @project Hard
 * @since 26/04/2025
 */
@Getter
@Service(provides = CoreAdapter.class, priority = 60)
public class CoreAdapterImpl implements CoreAdapter {

    private final HardPractice plugin;
    private final KaosCoreBridge kaosCoreBridge;
    private Core core;

    /**
     * Constructor for DI. Receives the main bootstrap instance.
     */
    public CoreAdapterImpl(HardPractice plugin, KaosCoreBridge kaosCoreBridge) {
        this.plugin = plugin;
        this.kaosCoreBridge = kaosCoreBridge;
    }

    @Override
    public void initialize(HardContext context) {
        this.core = this.determineCore();
    }

    @Override
    public Core getCore() {
        if (this.core == null) {
            throw new IllegalStateException("CoreAdapter has not been initialized yet.");
        }
        return this.core;
    }

    /**
     * Determines the server implementation to use based on the enabled plugins.
     *
     * @return The selected server implementation.
     */
    private Core determineCore() {
        Core selectedCore = new DefaultCoreImpl(this.plugin);

        for (CoreType coreType : CoreType.values()) {
            if (this.plugin.getServer().getPluginManager().isPluginEnabled(coreType.getPluginName())) {
                switch (coreType) {
                    case KAOSCORE:
                        selectedCore = new KaosCoreImpl(this.kaosCoreBridge);
                        break;
                    case PHOENIX:
                        selectedCore = new PhoenixCoreImpl(SharedAPI.getInstance());
                        break;
                    case AQUA:
                        selectedCore = new AquaCoreImpl(AquaCoreAPI.INSTANCE, this.plugin);
                        break;
                    case HELIUM:
                        selectedCore = new HeliumCoreImpl(HeliumAPI.INSTANCE);
                        break;
                }
                break;
            }
        }
        return selectedCore;
    }
}
