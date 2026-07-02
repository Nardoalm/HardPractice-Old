package us.alleypvp.practice.feature.queue.menu;

import us.alleypvp.practice.AlleyPractice;
import us.alleypvp.practice.common.constants.MessageConstant;
import us.alleypvp.practice.common.item.ItemBuilder;
import us.alleypvp.practice.feature.ffa.menu.FFAMenu;
import us.alleypvp.practice.feature.queue.QueueService;
import us.alleypvp.practice.feature.queue.menu.sub.UnrankedMenu;
import us.alleypvp.practice.library.menu.Button;
import us.alleypvp.practice.library.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Emmy
 * @project Alley
 * @date 23/05/2024 - 01:28
 */
@AllArgsConstructor
public class QueuesMenuDefault extends Menu {
    protected final AlleyPractice plugin;

    public QueuesMenuDefault() {
        this.plugin = AlleyPractice.getInstance();
    }

    /**
     * Get the title of the menu.
     *
     * @param player the player to get the title for
     * @return the title of the menu
     */
    @Override
    public String getTitle(Player player) {
        return "&b&lUnranked Solo Queue";
    }

    /**
     * Get the buttons for the menu.
     *
     * @param player the player to get the buttons for
     * @return the buttons for the menu
     */
    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        QueueService queueService = AlleyPractice.getInstance().getService(QueueService.class);

        buttons.put(11, new QueuesButtonDefault("&b&lSolos", Material.DIAMOND_SWORD, 0, Arrays.asList(
                "&71v1 casuais sem",
                "&7penalidade por derrota.",
                "",
                "&bJogadores: &f" + queueService.getPlayerCountOfGameType("Unranked"),
                "",
                "&aClick to play!"
        )));

        buttons.put(13, new QueuesButtonDefault("&b&lBots", Material.GOLD_SWORD, 0, Arrays.asList(
                "&7Pratique contra bots",
                "&7para melhorar sua habilidade.",
                "",
                "&bJogadores: &f" + queueService.getPlayerCountOfGameType("Bots"),
                "",
                "&aClick to play!"
        )));

        buttons.put(15, new QueuesButtonDefault("&b&lFFA", Material.GOLD_AXE, 0, Arrays.asList(
                "&7Free for all com",
                "&7respawns infinitos.",
                "",
                "&bJogadores: &f" + queueService.getPlayerCountOfGameType("FFA"),
                "",
                "&aClick to play!"
        )));

        this.addGlass(buttons, 15);

        return buttons;
    }

    @Override
    public int getSize() {
        return 9 * 3;
    }

    @AllArgsConstructor
    public static class QueuesButtonDefault extends Button {
        private String displayName;
        private Material material;
        private int durability;
        private List<String> lore;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(this.material)
                    .name(this.displayName)
                    .durability(this.durability)
                    .lore(this.lore)
                    .hideMeta()
                    .build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
            if (clickType != ClickType.LEFT) return;

            switch (this.material) {
                case DIAMOND_SWORD:
                    new UnrankedMenu().openMenu(player);
                    break;
                case GOLD_AXE:
                    new FFAMenu().openMenu(player);
                    break;
                case GOLD_SWORD:
                    player.sendMessage(MessageConstant.IN_DEVELOPMENT);
                    break;
            }

            this.playNeutral(player);
        }
    }
}
