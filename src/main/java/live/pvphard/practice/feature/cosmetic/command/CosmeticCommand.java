package live.pvphard.practice.feature.cosmetic.command;

import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

/**
 * @author Remi
 * @project Hard
 * @date 6/1/2024
 */
public class CosmeticCommand extends BaseCommand {
    @CommandData(
            name = "cosmetic",
            isAdminOnly = true,
            usage = "cosmetic <list|get|set> <player> <cosmetic>",
            description = "Gerencie cosméticos."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        player.sendMessage(" ");
        player.sendMessage(CC.translate("&b&lAjuda dos Comandos de Cosméticos:"));
        player.sendMessage(CC.translate(" &f◆ &b/cosmetic list &7| Lista todos os cosméticos"));
        player.sendMessage(CC.translate(" &f◆ &b/cosmetic get &8(&7player&8)  &7| Mostra os cosméticos selecionados"));
        player.sendMessage(CC.translate(" &f◆ &b/cosmetic set &8(&7player&8) &8(&7cosmetic&8)  &7| Define o cosmético ativo"));
        player.sendMessage("");

    }
}
