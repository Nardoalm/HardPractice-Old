package live.pvphard.practice.feature.queue.menu.sub;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.library.menu.Button;
import live.pvphard.practice.library.menu.Menu;
import live.pvphard.practice.feature.queue.QueueService;
import live.pvphard.practice.feature.queue.Queue;
import live.pvphard.practice.feature.queue.menu.button.RankedButton;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Emmy
 * @project Hard
 * @date 23/05/2024 - 01:28
 */
public class RankedMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "&bRanked Queue";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        int slot = 10;

        for (Queue queue : HardPractice.getInstance().getService(QueueService.class).getQueues()) {
            if (queue.isRanked()) {
                slot = this.skipIfSlotCrossingBorder(slot);
                buttons.put(slot++, new RankedButton(queue));
            }
        }

        this.addBorder(buttons, 15, 5);

        return buttons;
    }


    @Override
    public int getSize() {
        return 9 * 5;
    }
}
