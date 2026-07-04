package live.pvphard.practice.adapter.core.internal;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.adapter.core.Core;
import live.pvphard.practice.adapter.core.CoreType;
import live.pvphard.practice.common.text.CC;
import me.activated.core.api.tags.Tag;
import me.activated.core.plugin.AquaCoreAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author Emmy
 * @project Hard
 * @since 26/04/2025
 */
public class AquaCoreImpl implements Core {
    protected final HardPractice plugin;
    protected final AquaCoreAPI aquaCoreAPI;

    public AquaCoreImpl(AquaCoreAPI aquaCoreAPI, HardPractice plugin) {
        this.aquaCoreAPI = aquaCoreAPI;
        this.plugin = plugin;
    }

    @Override
    public CoreType getType() {
        return CoreType.AQUA;
    }

    @Override
    public ChatColor getPlayerColor(Player player) {
        if (player == null) return ChatColor.WHITE;
        ChatColor color = this.aquaCoreAPI.getPlayerNameColor(player.getUniqueId());
        return color != null ? color : ChatColor.WHITE;
    }

    @Override
    public String getRankPrefix(Player player) {
        if (player == null || this.aquaCoreAPI.getPlayerRank(player.getUniqueId()) == null) return "";
        String prefix = this.aquaCoreAPI.getPlayerRank(player.getUniqueId()).getPrefix();
        return prefix != null ? CC.translate(prefix) : "";
    }

    @Override
    public String getRankSuffix(Player player) {
        if (player == null || this.aquaCoreAPI.getPlayerRank(player.getUniqueId()) == null) return "";
        String suffix = this.aquaCoreAPI.getPlayerRank(player.getUniqueId()).getSuffix();
        return suffix != null ? CC.translate(suffix) : "";
    }

    @Override public String getRankName(Player player) { return "Member"; }
    @Override public ChatColor getRankColor(Player player) { return ChatColor.WHITE; }

    @Override
    public String getTagPrefix(Player player) {
        if (player == null) return "";
        Tag tag = this.aquaCoreAPI.getTag(player.getUniqueId());
        return (tag != null && tag.getPrefix() != null) ? tag.getPrefix() : "";
    }

    @Override
    public ChatColor getTagColor(Player player) {
        if (player == null) return ChatColor.RESET;
        Tag tag = this.aquaCoreAPI.getTag(player.getUniqueId());
        return (tag != null && tag.getColor() != null) ? tag.getColor() : ChatColor.RESET;
    }

    @Override
    public String getChatFormat(Player player, String eventMessage, String separator) {
        return Core.super.getChatFormat(player, eventMessage, separator);
    }
}
