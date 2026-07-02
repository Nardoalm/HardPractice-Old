package us.alleypvp.practice.feature.tournament.menu;

import us.alleypvp.practice.AlleyPractice;
import us.alleypvp.practice.common.item.ItemBuilder;
import us.alleypvp.practice.common.text.CC;
import us.alleypvp.practice.feature.kit.Kit;
import us.alleypvp.practice.feature.kit.KitService;
import us.alleypvp.practice.feature.tournament.TournamentService;
import us.alleypvp.practice.feature.tournament.model.TournamentType;
import us.alleypvp.practice.library.menu.Button;
import us.alleypvp.practice.library.menu.pagination.PaginatedMenu;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * @author Remi
 * @project alley-practice
 * @date 6/08/2025
 */
@AllArgsConstructor
public class TournamentHostKitMenu extends PaginatedMenu {
    private final TournamentType tournamentType;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "&bSelect a Kit";
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
        KitService kitService = AlleyPractice.getInstance().getService(KitService.class);

        int slot = 0;
        for (Kit kit : kitService.getKits()) {
            if (kit.isEnabled()) {
                slot = this.validateSlot(slot);
                buttons.put(slot++, new KitButton(kit, tournamentType));
            }
        }

        this.addGlassToAvoidedSlots(buttons);

        return buttons;
    }

    @Override
    public int getSize() {
        return 9 * 5;
    }

    @AllArgsConstructor
    private static class KitButton extends Button {
        private final Kit kit;
        private final TournamentType type;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(kit.getIcon())
                    .name(kit.getMenuTitle())
                    .durability(kit.getDurability())
                    .hideMeta()
                    .lore(getStyledLore())
                    .hideMeta()
                    .build();
        }

        private List<String> getStyledLore() {
            List<String> lore = new ArrayList<>();
            lore.add(CC.MENU_BAR);
            if (kit.getDescription() != null && !kit.getDescription().isEmpty()) {
                lore.addAll(Arrays.asList(
                        "&7" + kit.getDescription(),
                        ""
                ));
            }
            lore.addAll(Arrays.asList(
                    "&b│ &rClick to host a " + type.getDisplayName() + " tournament",
                    "&b│ &rwith the " + kit.getDisplayName() + " kit.",
                    ""
            ));
            lore.add("&aClick to host tournament.");
            lore.add(CC.MENU_BAR);
            return lore;
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (clickType.isLeftClick()) {
                player.closeInventory();
                TournamentService tournamentService = AlleyPractice.getInstance().getService(TournamentService.class);
                tournamentService.hostTournament(player, type, kit);
                playSuccess(player);
            }
        }
    }
}
