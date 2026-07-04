package live.pvphard.practice.feature.division.command;

import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.feature.division.menu.DivisionsMenu;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

/**
 * @author Remi
 * @project Hard
 * @date 6/2/2024
 */
public class DivisionCommand extends BaseCommand {
    @CommandData(
            name = "division",
            usage = "division",
            description = "Envia uma lista de comandos para gerenciar divisões"
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        //TODO: make page based

        if (player.hasPermission("kaos.admin")) {
            player.sendMessage(" ");
            player.sendMessage(CC.translate("&b&lAjuda dos Comandos de Divisão:"));
            player.sendMessage(CC.translate(" &f◆ &b/division create &8(&7divisionName&8) &8(&7requiredWins&8) &7| Criar uma divisão"));
            player.sendMessage(CC.translate(" &f◆ &b/division delete &8(&7divisionName&8) &7| Excluir uma divisão"));
            player.sendMessage(CC.translate(" &f◆ &b/division view &8(&7divisionName&8) &7| Ver informações da divisão"));
            player.sendMessage(CC.translate(" &f◆ &b/division setwins &8(&7divisionName&8) &8(&7requiredWins&8) &8(&7tier&8) &7| Definir vitórias necessárias de um tier"));
            player.sendMessage(CC.translate(" &f◆ &b/division seticon &8(&7divisionName&8) &7| Definir ícone da divisão"));
            player.sendMessage(CC.translate(" &f◆ &b/division setdisplayname &8(&7divisionName&8) &8(&7displayName&8) &7| Definir nome de exibição da divisão"));
            player.sendMessage(CC.translate(" &f◆ &b/division setdescription &8(&7divisionName&8) &8(&7description&8) &7| Definir descrição da divisão"));
            player.sendMessage(CC.translate(" &f◆ &b/division menu &7| Abrir o menu de divisão"));
            player.sendMessage(CC.translate(" &f◆ &b/division list &7| Ver todas as divisões"));
            player.sendMessage("");
            return;
        }

        new DivisionsMenu().openMenu(player);
    }
}
