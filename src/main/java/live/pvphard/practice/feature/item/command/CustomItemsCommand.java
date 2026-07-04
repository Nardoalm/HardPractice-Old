package live.pvphard.practice.feature.item.command;

import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;

import java.util.Arrays;

/**
 * @author Emmy
 * @project hard-practice
 * @since 18/07/2025
 */
public class CustomItemsCommand extends BaseCommand {
    @CommandData(
            name = "customitems",
            aliases = {"kaositems", "specialitems"},
            usage = "customitems",
            description = "Lista de comandos para itens especiais",
            isAdminOnly = true
    )
    @Override
    public void onCommand(CommandArgs command) {
        Arrays.asList(
                "",
                "&b&lCustom Items Commands Help:",
                " &f◆ &b/customitems goldenhead &8(&7amount&8) &7| Gives you a golden head",
                ""
        ).forEach(line -> command.getPlayer().sendMessage(CC.translate(line)));
    }
}