package live.pvphard.practice.feature.party.menu.event;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.common.item.ItemBuilder;
import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.feature.party.Party;
import live.pvphard.practice.feature.party.menu.event.impl.PartyEventFFAMenu;
import live.pvphard.practice.feature.party.menu.event.impl.PartyEventSplitMenu;
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

@AllArgsConstructor
public class PartyEventMenu extends Menu {
    @Override
    public String getTitle(Player player) {
        return "&b&lParty Events";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(11, new PartyEventButton(
                Material.DIAMOND_SWORD, 0,
                "&b&lTeam Split",
                Arrays.asList(
                        CC.MENU_BAR,
                        "&7Splits the party into",
                        "&72 teams to fight",
                        "&7against each other.",
                        "",
                        "&aClick to select a kit.",
                        CC.MENU_BAR
                )
        ));

        buttons.put(13, new PartyEventButton(
                Material.GOLD_AXE, 0,
                "&b&lFree for All",
                Arrays.asList(
                        CC.MENU_BAR,
                        "&7All players fight",
                        "&7each other.",
                        "",
                        "&aClick to select a kit.",
                        CC.MENU_BAR
                )
        ));

        buttons.put(15, new PartyEventButton(
                Material.INK_SACK, 14,
                "&cBest of 3 Sumo",
                Arrays.asList(
                        CC.MENU_BAR,
                        "&7This event has not",
                        "&7been implemented yet.",
                        "",
                        "&c&mClick to start the event.",
                        CC.MENU_BAR
                )
        ));

        this.addGlass(buttons, 15);

        return buttons;
    }

    @Override
    public int getSize() {
        return 3 * 9;
    }

    @AllArgsConstructor
    private static class PartyEventButton extends Button {
        private Material material;
        private int durability;
        private String name;
        private List<String> lore;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(material)
                    .name(name)
                    .lore(lore)
                    .durability(durability)
                    .hideMeta()
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (clickType != ClickType.LEFT) return;

            ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
            if (profileService == null || profileService.getProfile(player.getUniqueId()) == null) {
                player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_YOU_NOT_IN_PARTY));
                return;
            }

            Party party = profileService.getProfile(player.getUniqueId()).getParty();
            if (party == null) {
                player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_YOU_NOT_IN_PARTY));
                return;
            }

            if (!party.isLeader(player)) {
                player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_YOU_NOT_PARTY_LEADER));
                return;
            }

            if (party.getMembers().size() < 2) {
                player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_YOU_PARTY_NEED_TWO_PLAYERS));
                return;
            }

            switch (material) {
                case DIAMOND_SWORD:
                    new PartyEventSplitMenu().openMenu(player);
                    break;
                case GOLD_AXE:
                    new PartyEventFFAMenu().openMenu(player);
                    break;
                case REDSTONE:
                    // Start best of 3 sumo event
                    break;
            }
        }
    }
}