package us.alleypvp.practice.feature.queue.menu.button;

import us.alleypvp.practice.AlleyPractice;
import us.alleypvp.practice.library.menu.Button;
import us.alleypvp.practice.feature.arena.Arena;
import us.alleypvp.practice.feature.arena.ArenaService;
import us.alleypvp.practice.feature.kit.Kit;
import us.alleypvp.practice.common.item.ItemBuilder;
import us.alleypvp.practice.common.text.CC;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class BotButton extends Button {
    private final Kit kit;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(kit.getIcon())
                .name(kit.getName())
                .lore(
                        "&7Click to fight a bot!",
                        "",
                        "&7Kit: &b" + kit.getName(),
                        "",
                        "&7Click to start the fight!"
                )
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        Arena arena = AlleyPractice.getInstance().getService(ArenaService.class).getRandomArena(this.kit);
        if (arena == null) {
            player.sendMessage("No arena available.");
            return;
        }

        player.sendMessage(CC.translate("&cThis feature is not yet available."));
    }
}