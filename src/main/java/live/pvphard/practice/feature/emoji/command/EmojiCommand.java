package live.pvphard.practice.feature.emoji.command;

import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.common.text.ClickableUtil;
import live.pvphard.practice.feature.emoji.EmojiType;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * @author Emmy
 * @project Hard
 * @date 22/01/2025 - 21:15
 */
public class EmojiCommand extends BaseCommand {
    @CommandData(
            name = "emoji",
            aliases = "emojis",
            permission = "kaos.donator.chat.symbol",
            usage = "emoji",
            description = "Help guide for emojis"
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        Arrays.asList(
                "",
                "&b&lEmoji Help",
                " &7To use an emoji, simply type the identifier in chat.",
                " &7For example, if you want to use the &f" + EmojiType.HEART.getFormat() + " &7emoji, type &f" + EmojiType.HEART.getIdentifier() + "&7 in chat.",
                ""
        ).forEach(line -> player.sendMessage(CC.translate(line)));

        player.spigot().sendMessage(ClickableUtil.createComponent(" &a[Click here to view all emojis]", "/emoji list", "&7Click here to view the list of all available emojis."));
        player.sendMessage("");
    }
}