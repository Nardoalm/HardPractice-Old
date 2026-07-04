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
            description = "Mostra os comandos de FFA."
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
        sender.sendMessage(CC.translate("&b&lComandos de FFA &8(&7Página &f" + page + "&7/&f" + this.pages.length + "&8)"));
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
                    " &f◆ &b/ffa setup &8(&7ffaName&8) &8(&7arenaName&8) &8(&7maxPlayers&8) &8(&7menuSlot&8) &7| Configura uma nova partida de FFA",
                    " &f◆ &b/ffa toggle &8(&7ffaName&8) &7| Ativa ou desativa uma arena de FFA",
                    " &f◆ &b/ffa list &7| Lista as partidas atuais de FFA",
                    " &f◆ &b/ffa listplayers &8(&7ffaName&8) &7| Lista todos os jogadores do FFA",
            },
            {
                    " &f◆ &b/ffa maxplayers &8(&7ffaName&8) &8(&7amount&8) &7| Define o máximo de jogadores",
                    " &f◆ &b/ffa safezone &8(&7kitName&8) &8(&7pos1/pos2&8) &7| Define os limites da safezone",
                    " &f◆ &b/ffa setspawn &8(&7ffaName&8) &7| Define o spawn da arena de FFA",
                    " &f◆ &b/ffa setarena &8(&7ffaName&8) &7| Define a arena da partida de FFA",
                    " &f◆ &b/ffa setslot &8(&7ffaName&8) &8(&7slotNumber&8) &7| Define o slot do menu"
            },
            {
                    " &f◆ &b/ffa add &8(&7playerName&8) &8(&7ffaName&8) &7| Adiciona um jogador",
                    " &f◆ &b/ffa kick &8(&7playerName&8) &7| Expulsa um jogador"
            }
    };
}
