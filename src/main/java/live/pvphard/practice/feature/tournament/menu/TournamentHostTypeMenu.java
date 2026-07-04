package live.pvphard.practice.feature.tournament.menu;

import live.pvphard.practice.common.item.ItemBuilder;
import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.feature.tournament.model.TournamentType;
import live.pvphard.practice.library.menu.Button;
import live.pvphard.practice.library.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * @author Remi
 * @project hard-practice
 * @date 6/08/2025
 */
public class TournamentHostTypeMenu extends Menu {
    @Override
    public String getTitle(Player player) {
        return "&b&lSelect Tournament Type";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        buttons.put(10, new TypeButton(TournamentType.SOLO));
        buttons.put(12, new TypeButton(TournamentType.DUO));
        buttons.put(14, new TypeButton(TournamentType.THREE));
        buttons.put(16, new TypeButton(TournamentType.FOUR));
        this.addGlass(buttons, 15);
        return buttons;
    }

    @Override
    public int getSize() {
        return 9 * 3;
    }

    @AllArgsConstructor
    private static class TypeButton extends Button {
        private final TournamentType type;

        @Override
        public ItemStack getButtonItem(Player player) {
            Material material = type.getTeamSize() == 1 ? Material.IRON_SWORD : Material.DIAMOND_SWORD;

            return new ItemBuilder(material)
                    .name("&b&l" + type.getDisplayName() + " Tournament")
                    .lore(getLore())
                    .hideMeta()
                    .build();
        }

        private List<String> getLore() {
            List<String> lore = new ArrayList<>();
            lore.add(CC.MENU_BAR);
            lore.addAll(Arrays.asList(
                    "&fHost a tournament where players",
                    "&fcompete in teams of " + type.getTeamSize() + ".",
                    ""
            ));
            lore.addAll(Arrays.asList(
                    "&b│ &rTeam Size: &b" + type.getTeamSize(),
                    "&b│ &rMax Teams: &b" + type.getMaxTeams(),
                    "&b│ &rMin Teams: &b" + type.getMinTeams(),
                    ""
            ));
            lore.add("&aClick to select this type.");
            lore.add(CC.MENU_BAR);
            return lore;
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (clickType.isLeftClick()) {
                playNeutral(player);
                new TournamentHostKitMenu(type).openMenu(player);
            }
        }
    }
}