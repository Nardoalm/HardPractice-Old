package live.pvphard.practice.feature.queue.menu.extra;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.library.menu.Button;
import live.pvphard.practice.library.menu.Menu;
import live.pvphard.practice.feature.kit.KitCategory;
import live.pvphard.practice.feature.queue.QueueService;
import live.pvphard.practice.feature.queue.Queue;
import live.pvphard.practice.feature.queue.QueueType;
import live.pvphard.practice.feature.queue.menu.button.UnrankedButton;
import live.pvphard.practice.feature.queue.menu.extra.button.QueueModeSwitcherButton;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Emmy
 * @project Hard
 * @since 01/05/2025
 */
@AllArgsConstructor
public class ExtraModesMenu extends Menu {
    private final QueueType queueType;

    @Override
    public String getTitle(Player player) {
        return "&b&l" + this.queueType.getMenuTitle();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(4, new QueueModeSwitcherButton(this.queueType, KitCategory.NORMAL));

        int slot = 10;
        for (Queue queue : HardPractice.getInstance().getService(QueueService.class).getQueues()) {
            if (shouldAddQueue(queue, queueType)) {
                slot = this.skipIfSlotCrossingBorder(slot);
                buttons.put(slot++, new UnrankedButton(queue));
            }
        }

        this.addGlass(buttons, 15);

        return buttons;
    }

    @Override
    public int getSize() {
        return 9 * 4;
    }

    private boolean shouldAddQueue(Queue queue, QueueType queueType) {
        if (queue.isRanked() || queue.getKit().getCategory() != KitCategory.EXTRA) {
            return false;
        }

        return (queueType == QueueType.DUOS) == queue.isDuos();
    }
}