package live.pvphard.practice.feature.layout.menu;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.library.menu.Button;
import live.pvphard.practice.library.menu.Menu;
import live.pvphard.practice.feature.kit.KitCategory;
import live.pvphard.practice.feature.queue.QueueService;
import live.pvphard.practice.feature.queue.Queue;
import live.pvphard.practice.feature.layout.menu.button.LayoutButton;
import live.pvphard.practice.feature.layout.menu.button.LayoutModeSwitcherButton;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Emmy
 * @project Hard
 * @since 02/05/2025
 */
@AllArgsConstructor
public class LayoutMenu extends Menu {
    private KitCategory kitCategory;

    @Override
    public String getTitle(Player player) {
        return "&b&lLayout Editor";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        int slot = 10;

        for (Queue queue : HardPractice.getInstance().getService(QueueService.class).getQueues()) {
            if (!queue.isRanked() && !queue.isDuos() && queue.getKit().getCategory() == this.kitCategory && queue.getKit().isEditable()) {
                slot = this.skipIfSlotCrossingBorder(slot);
                buttons.put(slot++, new LayoutButton(queue.getKit()));
            }
        }

        if (this.kitCategory == KitCategory.NORMAL) {
            buttons.put(40, new LayoutModeSwitcherButton(KitCategory.EXTRA));
        } else {
            buttons.put(4, new LayoutModeSwitcherButton(KitCategory.NORMAL));
        }

        this.addGlass(buttons, 15);

        return buttons;
    }

    @Override
    public int getSize() {
        if (this.kitCategory == KitCategory.EXTRA) {
            return 9 * 4;
        }

        return 9 * 5;
    }
}