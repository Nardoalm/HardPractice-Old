package live.pvphard.practice.common.constants;

import live.pvphard.practice.bootstrap.lifecycle.Service;
import org.bukkit.ChatColor;
import org.reflections.Reflections;

import java.util.List;

/**
 * @author Remi
 * @project hard-practice
 * @date 2/07/2025
 */
public interface PluginConstant extends Service {
    String getName();

    String getVersion();

    String getDescription();

    List<String> getAuthors();

    String getSpigotVersion();

    ChatColor getMainColor();

    String getPackageDirectory();

    String getAdminPermissionPrefix();

    String getPermissionLackMessage();

    /**
     * Gets the Reflections instance for classpath scanning.
     *
     * @return The Reflections object.
     */
    Reflections getReflections();
}