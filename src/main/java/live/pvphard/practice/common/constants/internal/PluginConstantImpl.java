package live.pvphard.practice.common.constants.internal;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.bootstrap.HardContext;
import live.pvphard.practice.bootstrap.annotation.Service;
import live.pvphard.practice.common.constants.PluginConstant;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginDescriptionFile;
import org.reflections.Reflections;

import java.util.List;

/**
 * This class holds constants related to a bootstrap.
 * It allows for easy access to specific bootstrap constants and configurations.
 *
 * @author Emmy
 * @project Hard
 * @since 03/04/2025
 */
@Getter
@Service(provides = PluginConstant.class, priority = 0)
public class PluginConstantImpl implements PluginConstant {
    private final HardPractice plugin;

    private String name;
    private String version;
    private String description;
    private List<String> authors;
    private String spigotVersion;
    private ChatColor mainColor;
    private String packageDirectory;
    private String adminPermissionPrefix;
    private String permissionLackMessage;
    private Reflections reflections;

    /**
     * Constructor for the PluginConstant class.
     *
     * @param plugin The Hard bootstrap instance.
     */
    public PluginConstantImpl(HardPractice plugin) {
        this.plugin = plugin;
    }

    @Override
    public void initialize(HardContext context) {
        PluginDescriptionFile pluginDescription = plugin.getDescription();

        this.name = pluginDescription.getName();
        this.version = pluginDescription.getVersion();
        this.authors = pluginDescription.getAuthors();
        this.description = pluginDescription.getDescription();
        this.spigotVersion = this.getBukkitVersionExact();

        this.mainColor = ChatColor.GOLD;
        this.packageDirectory = "live.pvphard.practice";

        this.adminPermissionPrefix = this.name + ".admin";
        this.permissionLackMessage = ChatColor.RED + ".";

        this.reflections = new Reflections(this.packageDirectory);
    }

    /**
     * Gets the exact Bukkit version of the server (e.g., "1.8.8").
     * @return The exact Bukkit version string.
     */
    private String getBukkitVersionExact() {
        String serverVersion = this.plugin.getServer().getVersion();
        int mcIndex = serverVersion.indexOf("MC: ");
        if (mcIndex != -1) {
            int endIndex = serverVersion.indexOf(")", mcIndex);
            if (endIndex != -1) {
                return serverVersion.substring(mcIndex + 4, endIndex);
            }
        }
        return "Unknown";
    }
}
