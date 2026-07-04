package live.pvphard.practice.feature.match.menu;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.common.item.ItemBuilder;
import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.locale.LocaleService;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.feature.match.Match;
import live.pvphard.practice.feature.match.MatchService;
import live.pvphard.practice.library.menu.Button;
import live.pvphard.practice.library.menu.MenuUtil;
import live.pvphard.practice.library.menu.pagination.PaginatedMenu;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CurrentMatchesMenu extends PaginatedMenu {

    @Override
    public int getSize() {
        return 54;
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "&b&lCurrent Matches (" + this.getDisplayableMatches().size() + ")";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final Map<Integer, Button> buttons = new ConcurrentHashMap<>();
        int slot = 0;

        for (Match match : this.getDisplayableMatches()) {
            buttons.put(slot++, new CurrentMatchButton(match));
        }

        return buttons;
    }

    private List<Match> getDisplayableMatches() {
        List<Match> matches = HardPractice.getInstance().getService(MatchService.class).getMatches();
        List<Match> displayableMatches = new ArrayList<>();

        for (Match match : matches) {
            if (match == null || match.getParticipants() == null || match.getParticipants().size() < 2) {
                continue;
            }

            if (match.getParticipants().get(0) == null || match.getParticipants().get(1) == null) {
                continue;
            }

            displayableMatches.add(match);
        }

        return displayableMatches;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        this.addBorder(buttons, 15, 6);
        buttons.put(4, new RefreshButton());
        buttons.put(49, new CloseButton());
        return buttons;
    }

    @Override
    public int getPreviousPageButtonSlot() {
        return 45;
    }

    @Override
    public int getNextPageButtonSlot() {
        return 53;
    }

    @Override
    public Button getPreviousPageButton(Player player) {
        return MenuUtil.hasPrevious(-1, this) ? new NavigationButton(this, -1) : null;
    }

    @Override
    public Button getNextPageButton(Player player) {
        return MenuUtil.hasNext(player, 1, this) ? new NavigationButton(this, 1) : null;
    }

    @RequiredArgsConstructor
    public static class CurrentMatchButton extends Button {
        private final Match match;

        @Override
        public ItemStack getButtonItem(Player player) {
            String queueName = match.getQueue() == null || match.getQueue().getQueueType() == null
                    ? "Private"
                    : String.valueOf(match.getQueue().getQueueType());
            String firstName = resolveParticipantName(0);
            String secondName = resolveParticipantName(1);
            String kitName = match.getKit() == null ? "Unknown" : match.getKit().getDisplayName();
            String arenaName = match.getArena() == null ? "Unknown" : match.getArena().getName();
            Material icon = match.getKit() == null || match.getKit().getIcon() == null ? Material.PAPER : match.getKit().getIcon();
            short durability = (short) (match.getKit() == null ? 0 : match.getKit().getDurability());

            return new ItemBuilder(icon).name("&b&l" + firstName + " &7vs &b&l" + secondName).durability(durability).hideMeta()
                    .lore(
                            CC.MENU_BAR,
                            "&fKit: &b" + kitName,
                            "&fArena: &b" + arenaName,
                            "&fQueue: &b" + queueName,
                            "&fSpectators: &b" + match.getSpectators().size(),
                            "",
                            "&aClick to spectate this match.",
                            CC.MENU_BAR
                    )
                    .hideMeta().build();
        }

        private String resolveParticipantName(int index) {
            if (match.getParticipants() == null || match.getParticipants().size() <= index || match.getParticipants().get(index) == null) {
                return "Unknown";
            }

            String conjoinedNames = match.getParticipants().get(index).getConjoinedNames();
            if (conjoinedNames == null || conjoinedNames.trim().isEmpty()) {
                return "Unknown";
            }

            return conjoinedNames;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            if (clickType != ClickType.LEFT) return;

            if (HardPractice.getInstance().getService(ProfileService.class).getProfile(player.getUniqueId()).getMatch() != null) {
                player.sendMessage(HardPractice.getInstance().getService(LocaleService.class).getString(GlobalMessagesLocaleImpl.ERROR_YOU_ALREADY_SPECTATING_MATCH));
                return;
            }

            match.addSpectator(player);
            this.playNeutral(player);
        }
    }

    @AllArgsConstructor
    public static class RefreshButton extends Button {
        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.CARPET)
                    .name("&b&lRefresh")
                    .lore(
                            CC.MENU_BAR,
                            "&7Reloads the list of matches",
                            "&7currently taking place.",
                            " ",
                            "&aClick to refresh.",
                            CC.MENU_BAR
                    )
                    .durability(2)
                    .build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            if (clickType != ClickType.LEFT) return;
            new CurrentMatchesMenu().openMenu(player);
            this.playNeutral(player);
        }
    }

    @AllArgsConstructor
    public static class NavigationButton extends Button {
        private final PaginatedMenu menu;
        private final int offset;

        @Override
        public ItemStack getButtonItem(Player player) {
            boolean next = this.offset > 0;

            return new ItemBuilder(Material.ARROW)
                    .name(next ? "&b&lNext" : "&b&lPrevious")
                    .lore(
                            CC.MENU_BAR,
                            "&fCurrent page: &b" + this.menu.getPage() + "/" + this.menu.getPages(player),
                            " ",
                            next ? "&aClick to go to the next page." : "&aClick to go back a page.",
                            CC.MENU_BAR
                    )
                    .build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            if (clickType != ClickType.LEFT) {
                return;
            }

            this.menu.modPage(player, this.offset);
            this.playNeutral(player);
        }
    }

    public static class CloseButton extends Button {
        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.BARRIER)
                    .name("&c&lClose")
                    .lore(
                            CC.MENU_BAR,
                            "&7Closes the current matches menu.",
                            " ",
                            "&cClick to close.",
                            CC.MENU_BAR
                    )
                    .build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            if (clickType != ClickType.LEFT) {
                return;
            }

            player.closeInventory();
            this.playNeutral(player);
        }
    }
}