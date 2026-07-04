package live.pvphard.practice.feature.server.menu.button;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.library.menu.Button;
import live.pvphard.practice.feature.server.ServerService;
import live.pvphard.practice.common.item.ItemBuilder;
import live.pvphard.practice.common.text.CC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

/**
 * @author Emmy
 * @project Hard
 * @since 09/03/2025
 */
public class ServiceResetRebootButton extends Button {

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.EMERALD)
                .name("&a&lCancelar Reinício")
                .lore(
                        "&fIsso cancelará a preparação",
                        "&fpara um reinício, liberando",
                        "&fas filas novamente.",
                        "",
                        "&aClique para liberar as filas novamente!"
                )
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        if (clickType != ClickType.LEFT) return;

        if (!player.isOp()) {
            player.sendMessage(CC.translate("&cVocê não tem permissão para usar este botão."));
            return;
        }

        HardPractice.getInstance().getService(ServerService.class).setQueueingAllowed(true);
        Arrays.asList(
                "",
                "&a&lAS FILAS NÃO ESTÃO MAIS DESATIVADAS!",
                ""
        ).forEach(line -> Bukkit.broadcastMessage(CC.translate(line)));
    }
}
