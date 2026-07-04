package live.pvphard.practice.adapter.placeholder.internal;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.adapter.placeholder.PlaceholderService;
import live.pvphard.practice.bootstrap.HardContext;
import live.pvphard.practice.bootstrap.annotation.Service;
import live.pvphard.practice.common.logger.Logger;

/**
 * @author Emmy
 * @project hard-practice
 * @since 17/07/2025
 */
@Service(provides = PlaceholderService.class, priority = 430)
public class PlaceholderServiceImpl implements PlaceholderService {

    @Override
    public void initialize(HardContext context) {
        this.registerExpansion(context.getPlugin());
    }

    @Override
    public void registerExpansion(HardPractice plugin) {
        if (plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") == null) {
            Logger.info("PlaceholderAPI is not installed! Hard Placeholder Expansion will not be registered.");
            return;
        }
        Logger.logTime(KaosPlaceholderExpansion.class.getSimpleName(), () -> {
            KaosPlaceholderExpansion expansion = new KaosPlaceholderExpansion(plugin);
            expansion.register();
        });
    }
}
