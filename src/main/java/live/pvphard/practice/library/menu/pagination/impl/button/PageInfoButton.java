package live.pvphard.practice.library.menu.pagination.impl.button;

import live.pvphard.practice.library.menu.Button;
import live.pvphard.practice.library.menu.pagination.PaginatedMenu;
import live.pvphard.practice.library.menu.pagination.impl.menu.ViewAllPagesMenu;
import live.pvphard.practice.common.item.ItemBuilder;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class PageInfoButton extends Button {
    private PaginatedMenu menu;

    @Override
    public ItemStack getButtonItem(Player player) {
        int pages = menu.getPages(player);

        return new ItemBuilder(Material.PAPER)
                .name(ChatColor.GOLD + "Informações da Página")
                .lore(
                        ChatColor.YELLOW + "Você está vendo a página #" + menu.getPage() + ".",
                        ChatColor.YELLOW + (pages == 1 ? "Existe 1 página." : "Existem " + pages + " páginas."),
                        "",
                        ChatColor.YELLOW + "Clique com o botão do meio",
                        ChatColor.YELLOW + "para ver todas as páginas."
                )
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        if (clickType == ClickType.RIGHT) {
            new ViewAllPagesMenu(this.menu).openMenu(player);
            this.playNeutral(player);
        }
    }

}
