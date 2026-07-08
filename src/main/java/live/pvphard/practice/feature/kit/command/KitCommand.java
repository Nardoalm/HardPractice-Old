package live.pvphard.practice.feature.kit.command;

import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.common.text.ClickableUtil;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import live.pvphard.practice.library.command.annotation.CompleterData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Emmy
 * @project Hard
 * @date 28/04/2024 - 21:58
 */
public class KitCommand extends BaseCommand {
    @CompleterData(name = "kit")
    public List<String> kitCompleter(CommandArgs command) {
        List<String> completion = new ArrayList<>();
        if (command.getArgs().length == 1 && command.getPlayer().hasPermission(this.getAdminPermission())) {
            completion.addAll(Arrays.asList(
                    "list", "create", "delete", "toggle", "view", "settings", "viewsettings",
                    "setsetting", "setcategory", "setdescription", "setdisclaimer", "setdisplayname",
                    "seteditable", "seticon", "setinv", "getinv", "addpotion", "clearpotions", "removepotion",
                    "saveall", "save", "setraidingrolekit",
                    "removeraidingrolekit", "setmenutitle", "setprofile", "resetlayouts"
            ));
        }

        return completion;
    }

    @CommandData(
            name = "kit",
            aliases = "kit.help",
            isAdminOnly = true,
            inGameOnly = false,
            usage = "kit help <page>",
            description = "Shows all kit commands."
    )
    @Override
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();
        int page = 1;

        if (args.length > 0) {
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_INVALID_PAGE_NUMBER).replace("{input}", args[0]));
            }
        }

        if (page > this.pages.length || page < 1) {
            sender.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_NO_MORE_PAGES_AVAILABLE)
                    .replace("{input}", String.valueOf(page))
                    .replace("{max-pages}", String.valueOf(pages.length))
            );
            return;
        }

        sender.sendMessage("");
        sender.sendMessage(CC.translate("&b&lKit Commands &8(&7Page &f" + page + "&7/&f" + this.pages.length + "&8)"));
        for (String string : this.pages[page - 1]) {
            sender.sendMessage(CC.translate(string));
        }

        sender.sendMessage("");

        if (sender instanceof Player) {
            Player player = (Player) sender;
            ClickableUtil.sendPageNavigation(player, page, this.pages.length, "/kit", false, true);
        }
    }

    private final String[][] pages = {
            {
                    " &f◆ &b/kit list &7| View all kits",
                    " &f◆ &b/kit create &8(&7kitName&8) &7| Create a kit",
                    " &f◆ &b/kit delete &8(&7kitName&8) &7| Delete a kit",
                    " &f◆ &b/kit toggle &8(&7kitName&8) &7| Toggle a kit",
                    " &f◆ &b/kit view &8(&7kitName&8) &7| View a kit",
                    "",
                    "&fUse &b/kithelper &ffor other useful commands."
            },
            {
                    " &f◆ &b/kit settings &7| View all kit settings",
                    " &f◆ &b/kit viewsettings &8(&7kitName&8) &7| View settings of a kit",
                    " &f◆ &b/kit setsetting &8(&7kitName&8) &8(&7setting&8) &8(&7enabled&8) &7| Set a kit setting"
            },
            {
                    " &f◆ &b/kit setcategory &8(&7kitName&8) &8(&7category&8) &7| Set kit category",
                    " &f◆ &b/kit setdescription &8(&7kitName&8) &8(&7description&8) &7| Set kit description",
                    " &f◆ &b/kit setdisclaimer &8(&7kitName&8) &8(&7disclaimer&8) &7| Set kit disclaimer",
                    " &f◆ &b/kit setdisplayname &8(&7kitName&8) &8(&7displayname&8) &7| Set display name",
                    " &f◆ &b/kit setmenutitle &8(&7kitName&8) &8(&7title&8) &7| Set menu title",
                    " &f◆ &b/kit seteditable &8(&7kitName&8) &8(&7true/false&8) &7| Set if the kit is editable",
                    " &f◆ &b/kit setprofile &8(&7kitName&8) &8(&7profileName&8) &7| Set kit kb profile",
                    " &f◆ &b/kit seticon &8(&7kitName&8) &7| Set kit icon"
            },
            {
                    " &f◆ &b/kit setinv &8(&7kitName&8) &7| Set kit inventory",
                    " &f◆ &b/kit getinv &8(&7kitName&8) &7| Get kit inventory"
            },
            {
                    " &f◆ &b/kit addpotion &8(&7kitName&8) &7| Set kit potion effects",
                    " &f◆ &b/kit removepotion &8(&7kitName&8) &7| Remove a kit potion effect",
                    " &f◆ &b/kit clearpotions &8(&7kitName&8) &7| Clear all kit potion effects"
            },
            {
                    " &f◆ &b/kit setraidingrolekit &8(&7kitName&8) &8(&7role&8) &8(&7roleKitName&8) &7| Set raiding role kit",
                    " &f◆ &b/kit removeraidingrolekit &8(&7kitName&8) &8(&7role&8) &8(&7roleKitName&8) &7| Remove raiding role kit"
            },
            {
                    " &f◆ &b/kit resetlayouts &8(&7kitName&8) &7| Reset all profile layouts",
                    " &f◆ &b/kit saveall &7| Save all kits",
                    " &f◆ &b/kit save &8(&7kitName&8) &7| Save a kit"
            }
    };
}
