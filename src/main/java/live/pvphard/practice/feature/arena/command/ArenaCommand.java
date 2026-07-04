package live.pvphard.practice.feature.arena.command;

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
 * @date 02/05/2024 - 19:02
 */
public class ArenaCommand extends BaseCommand {
    @CompleterData(name = "arena")
    public List<String> arenaCompleter(CommandArgs command) {
        List<String> completion = new ArrayList<>();

        if (command.getArgs().length == 1 && command.getPlayer().hasPermission(this.getAdminPermission())) {
            completion.addAll(Arrays.asList(
                    "create", "delete", "list", "kitlist", "setcuboid", "setcenter",
                    "setspawn", "removekit", "addkit", "teleport", "toggle", "tool",
                    "setdisplayname", "setheightlimit", "setvoidlevel", "setportal", "eventadd", "eventremove",
                    "view", "test", "paste", "saveall", "save"
            ));
        }

        return completion;
    }

    @CommandData(
            name = "arena",
            aliases = "arena.help",
            isAdminOnly = true,
            inGameOnly = false,
            usage = "arena help <page>",
            description = "Mostra uma lista de comandos de arena"
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

        if (page > pages.length || page < 1) {
            sender.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_NO_MORE_PAGES_AVAILABLE)
                    .replace("{input}", String.valueOf(page))
                    .replace("{max-pages}", String.valueOf(pages.length))
            );
            return;
        }

        sender.sendMessage("");
        sender.sendMessage(CC.translate("&b&lComandos de Arena &8(&7Página &f" + page + "&7/&f" + pages.length + "&8)"));
        for (String string : pages[page - 1]) {
            sender.sendMessage(CC.translate(string));
        }
        sender.sendMessage("");

        if (sender instanceof Player) {
            Player player = (Player) sender;
            ClickableUtil.sendPageNavigation(player, page, pages.length, "/arena", false, true);
        }
    }

    private final String[][] pages = {
            {
                    " &f◆ &b/arena list &7| Lista todas as arenas",
                    " &f◆ &b/arena create &8(&7arenaName&8) &7| Cria uma arena",
                    " &f◆ &b/arena delete &8(&7arenaName&8) &7| Exclui uma arena",
                    " &f◆ &b/arena toggle &8(&7arenaName&8) &7| Ativa ou desativa uma arena",
                    " &f◆ &b/arena view &8(&7arenaName&8) &7| Mostra informações da arena",
                    " &f◆ &b/arena teleport &8(&7arenaName&8) &7| Teleporta até uma arena",
                    " &f◆ &b/arena tool &7| Pega a ferramenta de seleção de arena"
            },
            {
                    " &f◆ &b/arena paste &8(&7arenaName&8) &7| Cola a schematic na sua localização",
                    " &f◆ &b/arena test &8(&7arenaName&8) &7| Testa/depura uma arena"
            },
            {
                    " &f◆ &b/arena setdisplayname &8(&7arenaName&8) &8(&7displayname&8) &7| Define o nome de exibição",
                    " &f◆ &b/arena setcenter &8(&7arenaName&8) &7| Define a posição central",
                    " &f◆ &b/arena setcuboid &8(&7arenaName&8) &7| Define a posição mínima e máxima",
                    " &f◆ &b/arena setspawn &8(&7arenaName&8) &8<&7pos1/pos2&8> &7| Define os spawns",
                    " &f◆ &b/arena setportal &8(&7arenaName&8) &8<&71/2&8> &7| Define os portais",
                    " &f◆ &b/arena setheightlimit &8(&7arenaName&8) &7| Define o limite de altura",
                    " &f◆ &b/arena setvoidlevel &8(&7arenaName&8) &7| Define o nível do void"
            },
            {
                    " &f◆ &b/arena kitlist &8(&7arenaName&8) &7| Lista os kits da arena",
                    " &f◆ &b/arena addkit &8(&7arenaName&8) &8(&7kitName&8) &7| Adiciona um kit à arena",
                    " &f◆ &b/arena removekit &8(&7arenaName&8) &8(&7kitName&8) &7| Remove um kit da arena"
            },
            {
                    " &f◆ &b/arena eventadd &8(&7arenaName&8) &8(&7eventKey&8) &7| Vincula um evento à arena",
                    " &f◆ &b/arena eventremove &8(&7arenaName&8) &8(&7eventKey&8) &7| Remove um evento da arena"
            },
            {
                    " &f◆ &b/arena save &8(&7arenaName&8) &7| Salva uma arena",
                    " &f◆ &b/arena saveall &7| Salva todas as arenas"
            },
    };
}
