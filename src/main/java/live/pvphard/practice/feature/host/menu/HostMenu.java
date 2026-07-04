package live.pvphard.practice.feature.host.menu;

import live.pvphard.practice.common.item.ItemBuilder;
import live.pvphard.practice.feature.event.menu.EventMenu;
import live.pvphard.practice.feature.tournament.menu.TournamentHostTypeMenu;
import live.pvphard.practice.library.menu.Button;
import live.pvphard.practice.library.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HostMenu extends Menu {
    @Override
    public String getTitle(Player player) {
        return "&b&lHost Event";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(11, new HostButton("&b&lEvent", new ItemStack(Material.EMPTY_MAP), Arrays.asList(
                "&fHost an event with",
                "&fdifferent implementations.",
                "",
                " &f◆ &bTypes: &7Sumo, Brackets, Gulag, LMS",
                " &f◆ &7Knockout, OITC, Parkour, Dropper",
                " &f◆ &7SkyWars, Spleef, Stoplight, 4Corners",
                " &f◆ &7Thimble and TNT Tag",
                "",
                "&aClick to host!"
        )));

        buttons.put(15, new HostButton("&b&lTournament", new ItemStack(Material.BOW), Arrays.asList(
                "&fHost a tournament to",
                "&fcompete in a series",
                "&fof duels until you win.",
                "",
                "&aClick to host!"
        )));

        this.addGlass(buttons, 15);
        return buttons;
    }

    @AllArgsConstructor
    public static class HostButton extends Button {
        private String displayName;
        private ItemStack itemStack;
        private List<String> lore;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(this.itemStack)
                    .name(this.displayName)
                    .lore(this.lore)
                    .hideMeta()
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (clickType != ClickType.LEFT) {
                return;
            }

            switch (this.itemStack.getType()) {
                case BOW:
                    new TournamentHostTypeMenu().openMenu(player);
                    break;
                case EMPTY_MAP:
                    new EventMenu().openMenu(player);
                    break;
            }

            this.playNeutral(player);
        }
    }

    @Override
    public int getSize() {
        return 9 * 3;
    }
}