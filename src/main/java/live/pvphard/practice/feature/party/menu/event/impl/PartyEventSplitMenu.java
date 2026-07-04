package live.pvphard.practice.feature.party.menu.event.impl;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.library.menu.Button;
import live.pvphard.practice.library.menu.Menu;
import live.pvphard.practice.feature.queue.QueueService;
import live.pvphard.practice.feature.queue.Queue;
import live.pvphard.practice.feature.party.menu.event.impl.button.PartyEventSplitButton;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Emmy
 * @project Hard
 * @date 08/10/2024 - 18:38
 */
public class PartyEventSplitMenu extends Menu {
    protected final HardPractice plugin = HardPractice.getInstance();

    @Override
    public String getTitle(Player player) {
        return "&b&lSelect a kit";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        int slot = 10;
        for (Queue queue : HardPractice.getInstance().getService(QueueService.class).getQueues()) {
            if (!queue.isRanked() && !queue.isDuos() && queue.getKit().isEnabled()) {
                slot = this.skipIfSlotCrossingBorder(slot);
                buttons.put(slot++, new PartyEventSplitButton(queue.getKit()));
            }
        }

        this.addGlass(buttons, 15);

        return buttons;
    }

    @Override
    public int getSize() {
        return 9 * 6;
    }
}