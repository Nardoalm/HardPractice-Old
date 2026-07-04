package live.pvphard.practice.feature.party.menu.event.impl;

import live.pvphard.practice.library.menu.Button;
import live.pvphard.practice.library.menu.Menu;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Emmy
 * @project Hard
 * @date 08/10/2024 - 18:39
 */
public class PartyEventFFAMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "&c&lStill in development";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        this.addGlass(buttons, 15);

        return buttons;
    }

    @Override
    public int getSize() {
        return 3 * 9;
    }
}