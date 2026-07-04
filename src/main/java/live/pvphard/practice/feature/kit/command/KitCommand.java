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
            description = "Mostra todos os comandos de kit."
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
        sender.sendMessage(CC.translate("&b&lComandos de Kit &8(&7Página &f" + page + "&7/&f" + this.pages.length + "&8)"));
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
                    " &f◆ &b/kit list &7| Ver todos os kits",
                    " &f◆ &b/kit create &8(&7kitName&8) &7| Criar um kit",
                    " &f◆ &b/kit delete &8(&7kitName&8) &7| Excluir um kit",
                    " &f◆ &b/kit toggle &8(&7kitName&8) &7| Alternar um kit",
                    " &f◆ &b/kit view &8(&7kitName&8) &7| Ver um kit",
                    "",
                    "&fUse &b/kithelper &fpara outros comandos úteis."
            },
            {
                    " &f◆ &b/kit settings &7| Ver todas as configurações de kit",
                    " &f◆ &b/kit viewsettings &8(&7kitName&8) &7| Ver as configurações de um kit",
                    " &f◆ &b/kit setsetting &8(&7kitName&8) &8(&7setting&8) &8(&7enabled&8) &7| Definir configuração do kit"
            },
            {
                    " &f◆ &b/kit setcategory &8(&7kitName&8) &8(&7category&8) &7| Definir categoria do kit",
                    " &f◆ &b/kit setdescription &8(&7kitName&8) &8(&7description&8) &7| Definir descrição do kit",
                    " &f◆ &b/kit setdisclaimer &8(&7kitName&8) &8(&7disclaimer&8) &7| Definir aviso do kit",
                    " &f◆ &b/kit setdisplayname &8(&7kitName&8) &8(&7displayname&8) &7| Definir nome de exibição",
                    " &f◆ &b/kit setmenutitle &8(&7kitName&8) &8(&7title&8) &7| Definir título do menu",
                    " &f◆ &b/kit seteditable &8(&7kitName&8) &8(&7true/false&8) &7| Definir se o kit é editável",
                    " &f◆ &b/kit setprofile &8(&7kitName&8) &8(&7profileName&8) &7| Definir perfil de kb do kit",
                    " &f◆ &b/kit seticon &8(&7kitName&8) &7| Definir ícone do kit"
            },
            {
                    " &f◆ &b/kit setinv &8(&7kitName&8) &7| Definir inventário do kit",
                    " &f◆ &b/kit getinv &8(&7kitName&8) &7| Pegar inventário do kit"
            },
            {
                    " &f◆ &b/kit addpotion &8(&7kitName&8) &7| Definir efeitos de poção do kit",
                    " &f◆ &b/kit removepotion &8(&7kitName&8) &7| Remover efeito de poção do kit",
                    " &f◆ &b/kit clearpotions &8(&7kitName&8) &7| Limpar efeitos de poção do kit"
            },
            {
                    " &f◆ &b/kit setraidingrolekit &8(&7kitName&8) &8(&7role&8) &8(&7roleKitName&8) &7| Definir kit de função de raiding",
                    " &f◆ &b/kit removeraidingrolekit &8(&7kitName&8) &8(&7role&8) &8(&7roleKitName&8) &7| Remover kit de função de raiding"
            },
            {
                    " &f◆ &b/kit resetlayouts &8(&7kitName&8) &7| Resetar todos os layouts do perfil",
                    " &f◆ &b/kit saveall &7| Salvar todos os kits",
                    " &f◆ &b/kit save &8(&7kitName&8) &7| Salvar um kit"
            }
    };
}
