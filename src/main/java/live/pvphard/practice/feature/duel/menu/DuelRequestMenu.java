package live.pvphard.practice.feature.duel.menu;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.common.item.ItemBuilder;
import live.pvphard.practice.core.locale.LocaleService;
import live.pvphard.practice.core.locale.internal.impl.SettingsLocaleImpl;
import live.pvphard.practice.feature.duel.DuelRequestService;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.feature.queue.Queue;
import live.pvphard.practice.feature.queue.QueueService;
import live.pvphard.practice.library.menu.Button;
import live.pvphard.practice.library.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Emmy
 * @project Hard
 * @date 10/17/2024 - 20:11
 */
@AllArgsConstructor
public class DuelRequestMenu extends Menu {
    protected final HardPractice plugin = HardPractice.getInstance();
    private final Player targetPlayer;

    @Override
    public String getTitle(Player player) {
        return "&b&lDuel " + this.targetPlayer.getName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        int slot = 10;
        for (Queue queue : HardPractice.getInstance().getService(QueueService.class).getQueues()) {
            if (!queue.isRanked() && !queue.isDuos() && queue.getKit().isEnabled()) {
                slot = this.skipIfSlotCrossingBorder(slot);
                buttons.put(slot++, new DuelButton(this.targetPlayer, queue.getKit()));
            }
        }

        this.addGlass(buttons, 15);

        return buttons;
    }

    @Override
    public int getSize() {
        return 9 * 6;
    }

    @AllArgsConstructor
    private static class DuelButton extends Button {
        protected final HardPractice plugin = HardPractice.getInstance();
        private Player targetPlayer;
        private Kit kit;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(this.kit.getIcon())
                    .name(this.kit.getMenuTitle())
                    .lore(
                            "",
                            "&aClick to select!"
                    )
                    .durability(this.kit.getDurability())
                    .hideMeta()
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (clickType != ClickType.LEFT) return;

            if (player.hasPermission(this.plugin.getService(LocaleService.class).getString(SettingsLocaleImpl.PERMISSION_DONATOR_DUEL_ARENA_SELECTOR))) {
                new DuelArenaSelectorMenu(this.targetPlayer, this.kit).openMenu(player);
                return;
            }

            player.closeInventory();

            HardPractice.getInstance().getService(DuelRequestService.class).createAndSendRequest(player, this.targetPlayer, this.kit, null);
        }
    }
}