package live.pvphard.practice.adapter.core.internal;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.common.constants.PluginConstant;
import live.pvphard.practice.adapter.core.Core;
import live.pvphard.practice.adapter.core.CoreType;
import live.pvphard.practice.common.text.CC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author Emmy
 * @project Hard
 * @since 26/04/2025
 */
public class DefaultCoreImpl implements Core {
    protected final HardPractice plugin;
    protected final String adminPermission;

    /**
     * Constructor for the DefaultCoreImpl class.
     *
     * @param plugin The Hard bootstrap instance.
     */
    public DefaultCoreImpl(HardPractice plugin) {
        this.plugin = plugin;
        this.adminPermission = HardPractice.getInstance().getService(PluginConstant.class).getAdminPermissionPrefix();
    }

    @Override
    public CoreType getType() {
        return CoreType.DEFAULT;
    }

    @Override
    public ChatColor getPlayerColor(Player player) {
        if (player == null) {
            return ChatColor.WHITE;
        }

        if (player.isOp()) {
            return ChatColor.DARK_RED;
        } else if (player.hasPermission(this.adminPermission)) {
            return ChatColor.RED;
        }

        return ChatColor.GREEN;
    }

    @Override
    public String getRankPrefix(Player player) {
        if (player == null) {
            return CC.translate("&a");
        }

        if (player.isOp()) {
            return CC.translate("&7[&4&oOwner&7] &4");
        } else if (player.hasPermission(this.adminPermission)) {
            return CC.translate("&7[&c&oAdmin&7] &c");
        }

        return CC.translate("&a");
    }

    @Override
    public String getRankName(Player player) {
        if (player == null) {
            return "Default";
        }

        if (player.isOp()) {
            return "Owner";
        } else if (player.hasPermission(this.adminPermission)) {
            return "Admin";
        }

        return "Default";
    }

    @Override
    public String getRankSuffix(Player player) {
        return "";
    }

    @Override
    public ChatColor getRankColor(Player player) {
        if (player == null) {
            return ChatColor.WHITE;
        }

        if (player.isOp()) {
            return ChatColor.DARK_RED;
        } else if (player.hasPermission(this.adminPermission)) {
            return ChatColor.RED;
        }

        return ChatColor.GREEN;
    }

    @Override
    public String getTagPrefix(Player player) {
        return "";
    }

    @Override
    public ChatColor getTagColor(Player player) {
        return ChatColor.WHITE;
    }
}