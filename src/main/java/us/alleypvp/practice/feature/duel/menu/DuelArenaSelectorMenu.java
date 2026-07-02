package us.alleypvp.practice.feature.duel.menu;

import us.alleypvp.practice.AlleyPractice;
import us.alleypvp.practice.common.item.ItemBuilder;
import us.alleypvp.practice.feature.arena.Arena;
import us.alleypvp.practice.feature.arena.ArenaService;
import us.alleypvp.practice.feature.duel.DuelRequestService;
import us.alleypvp.practice.feature.kit.Kit;
import us.alleypvp.practice.library.menu.Button;
import us.alleypvp.practice.library.menu.pagination.PaginatedMenu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Emmy
 * @project Alley
 * @date 10/17/2024 - 22:13
 */
@AllArgsConstructor
public class DuelArenaSelectorMenu extends PaginatedMenu {
    private Player targetPlayer;
    private Kit kit;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "&b&lSelect an arena";
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        this.addGlassHeader(buttons, 15);

        return buttons;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        int slot = 0;

        for (Arena arena : AlleyPractice.getInstance().getService(ArenaService.class).getArenas()) {
            if (arena.getKits().contains(this.kit.getName()) && arena.isEnabled()) {
                buttons.put(slot++, new DuelArenaSelectorButton(this.targetPlayer, this.kit, arena));
            }
        }

        return buttons;
    }

    @Override
    public int getSize() {
        return 9 * 6;
    }

    @AllArgsConstructor
    private static class DuelArenaSelectorButton extends Button {
        private Player targetPlayer;
        private Kit kit;
        private Arena arena;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.PAPER)
                    .name("&b&l" + this.arena.getDisplayName())
                    .lore(
                            " &f◆ &bTarget: &f" + this.targetPlayer.getName(),
                            " &f◆ &bKit: &f" + this.kit.getDisplayName(),
                            "",
                            "&aClick to send the request!"
                    )
                    .durability(0)
                    .hideMeta()
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();

            AlleyPractice.getInstance().getService(DuelRequestService.class).createAndSendRequest(player, this.targetPlayer, this.kit, this.arena);
        }
    }
}