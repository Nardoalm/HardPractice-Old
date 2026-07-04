package live.pvphard.practice.feature.server.menu;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.library.menu.Button;
import live.pvphard.practice.library.menu.Menu;
import live.pvphard.practice.feature.server.ServerService;
import live.pvphard.practice.feature.server.menu.button.ServiceClearChatButton;
import live.pvphard.practice.feature.server.menu.button.ServiceClearLagButton;
import live.pvphard.practice.feature.server.menu.button.ServicePrepareRebootButton;
import live.pvphard.practice.feature.server.menu.button.ServiceResetRebootButton;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Emmy
 * @project Hard
 * @since 09/03/2025
 */
public class ServiceMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "&c&lService Menu";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(11, new ServiceClearChatButton());

        if (HardPractice.getInstance().getService(ServerService.class).isQueueingAllowed()) {
            buttons.put(13, new ServicePrepareRebootButton());
        } else {
            buttons.put(13, new ServiceResetRebootButton());
        }

        buttons.put(15, new ServiceClearLagButton());

        this.addGlass(buttons, 15);

        return buttons;
    }

    @Override
    public int getSize() {
        return 9 * 3;
    }
}
