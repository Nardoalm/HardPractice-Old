package live.pvphard.practice.feature.division.menu;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.library.menu.Button;
import live.pvphard.practice.library.menu.Menu;
import live.pvphard.practice.feature.division.Division;
import live.pvphard.practice.feature.division.DivisionService;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Emmy
 * @project Hard
 * @since 25/01/2025
 */
public class DivisionsMenu extends Menu {
    @Override
    public String getTitle(Player player) {
        return "&b&lDivisões";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        int slot = 10;
        for (Division division : HardPractice.getInstance().getService(DivisionService.class).getDivisions()) {
            buttons.put(slot++, new DivisionButton(division));
            if (slot == 17 || slot == 26 || slot == 35 || slot == 44 || slot == 53) {
                slot += 2;
            }
        }

        this.addBorder(buttons, 15, 6);

        return buttons;
    }

    @Override
    public int getSize() {
        return 9 * 6;
    }
}
