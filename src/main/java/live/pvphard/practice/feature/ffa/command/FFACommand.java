package live.pvphard.practice.feature.ffa.command;

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
 * @project hard-practice
 * @since 25/07/2025
 */
public class FFACommand extends BaseCommand {
    @CompleterData(name = "ffa")
    public List<String> ffaCompleter(CommandArgs command) {
        List<String> completion = new ArrayList<>();

        if (!command.getPlayer().hasPermission(this.getAdminPermission())) {
            return completion;
        }

        if (command.getArgs().length == 1) {
            completion.addAll(Arrays.asList(
                    "maxplayers", "setsafezone", "setarena", "setslot", "setspawn",
                    "list", "setup", "toggle", "add", "kick", "listplayers"
            ));
        }

        return completion;
    }

    @CommandData(
            name = "ffa",
            aliases = "ffa.help",
            isAdminOnly = true,
            inGameOnly = false,
            usage = "ffa help <page>",
            description = "Shows all FFA commands."
    )
    @Override
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();
        int page = 1;

        if (args.length > 0) {
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException exception) {
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
        sender.sendMessage(CC.translate("&b&lFFA Commands &8(&7Page " + page + "&7/" + this.pages.length + "&8)"));
        for (String string : this.pages[page - 1]) {
            sender.sendMessage(CC.translate(string));
        }
        sender.sendMessage("");

        if (sender instanceof Player) {
            Player player = (Player) sender;
            ClickableUtil.sendPageNavigation(player, page, this.pages.length, "/ffa", false, true);
        }
    }

    private final String[][] pages = {
            {
                    " &f◆ &b/ffa setup &8(&7ffaName&8) &8(&7arenaName&8) &8(&7maxPlayers&8) &8(&7menuSlot&8) &7| Configures a new FFA match",
                    " &f◆ &b/ffa toggle &8(&7ffaName&8) &7| Enables or disables an FFA arena",
                    " &f◆ &b/ffa list &7| Lists current FFA matches",
                    " &f◆ &b/ffa listplayers &8(&7ffaName&8) &7| Lists all players in the FFA",
            },
            {
                    " &f◆ &b/ffa maxplayers &8(&7ffaName&8) &8(&7amount&8) &7| Sets the maximum players for FFA",
                    " &f◆ &b/ffa safezone &8(&7kitName&8) &8(&7pos1/pos2&8) &7| Sets the safezone boundaries",
                    " &f◆ &b/ffa setspawn &8(&7ffaName&8) &7| Sets the FFA arena spawn",
                    " &f◆ &b/ffa setarena &8(&7ffaName&8) &7| Sets the FFA match arena",
                    " &f◆ &b/ffa setslot &8(&7ffaName&8) &8(&7slotNumber&8) &7| Sets the menu slot"
            },
            {
                    " &f◆ &b/ffa add &8(&7playerName&8) &8(&7ffaName&8) &7| Adds a player to FFA",
                    " &f◆ &b/ffa kick &8(&7playerName&8) &7| Kicks a player from FFA"
            }
    };
}
