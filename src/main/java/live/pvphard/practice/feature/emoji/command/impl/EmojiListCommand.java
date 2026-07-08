package live.pvphard.practice.feature.emoji.command.impl;

import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import live.pvphard.practice.feature.emoji.EmojiType;
import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.common.text.Symbol;
import net.md_5.bungee.api.chat.*;
import org.bukkit.entity.Player;

/**
 * @author Emmy
 * @project Hard
 * @date 22/01/2025 - 21:52
 */
public class EmojiListCommand extends BaseCommand {
    @CommandData(name = "emoji.list", aliases = "el", permission = "kaos.donator.chat.symbol", usage = "emoji list", description = "Lists all available emojis for chat use")
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        player.sendMessage("");
        player.sendMessage(CC.translate("&b&lEmojis: &7(Alias: &f/el&7)"));
        for (EmojiType emoji : EmojiType.values()) {
            TextComponent emojiComponent = new TextComponent(CC.translate(" " + emoji.getIdentifier() + " &7" + Symbol.ARROW_R + " &f" + emoji.getFormat()));
            emojiComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, emoji.getIdentifier()));

            String hoverText = "&7Click to insert &f" + emoji.getFormat() + " &7into your chat input.";
            BaseComponent[] hoverComponent = new ComponentBuilder(CC.translate(hoverText)).create();
            emojiComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponent));

            player.spigot().sendMessage(emojiComponent);
        }
        player.sendMessage("");
    }
}
