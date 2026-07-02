package us.alleypvp.practice.feature.duel.menu;

import us.alleypvp.practice.AlleyPractice;
import us.alleypvp.practice.common.item.ItemBuilder;
import us.alleypvp.practice.core.locale.LocaleService;
import us.alleypvp.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import us.alleypvp.practice.core.profile.ProfileService;
import us.alleypvp.practice.feature.duel.DuelRequest;
import us.alleypvp.practice.feature.duel.DuelRequestService;
import us.alleypvp.practice.feature.server.ServerService;
import us.alleypvp.practice.library.menu.Button;
import us.alleypvp.practice.library.menu.pagination.PaginatedMenu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Emmy
 * @project Alley
 * @date 22/10/2024 - 18:18
 */
public class DuelRequestsMenu extends PaginatedMenu {
    protected final AlleyPractice plugin = AlleyPractice.getInstance();

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "&b&lDuel Requests";
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        this.addGlassHeader(buttons, 15);

        buttons.put(4, new RefreshDuelRequestsButton());

        return buttons;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        AlleyPractice.getInstance().getService(DuelRequestService.class).getDuelRequests()
                .stream()
                .filter(duelRequest -> !duelRequest.getSender().equals(player))
                .forEach(duelRequest -> buttons.put(buttons.size(), new DuelRequestsButton(duelRequest)));


        return buttons;
    }

    @AllArgsConstructor
    private static class DuelRequestsButton extends Button {
        protected final AlleyPractice plugin = AlleyPractice.getInstance();
        private DuelRequest duelRequest;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.PAPER).name("&b&l" + this.duelRequest.getSender().getName()).durability(0).hideMeta()
                    .lore(
                            "&fKit: &f" + this.duelRequest.getKit().getDisplayName(),
                            "&fArena: &f" + this.duelRequest.getArena().getDisplayName(),
                            "",
                            "&fExpires in: &b" + this.duelRequest.getRemainingTimeFormatted(),
                            "",
                            "&aClick to accept!"
                    )
                    .hideMeta().build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (clickType != ClickType.LEFT) return;

            LocaleService localeService = AlleyPractice.getInstance().getService(LocaleService.class);

            if (this.duelRequest.hasExpired()) {
                player.sendMessage(localeService.getString(GlobalMessagesLocaleImpl.ERROR_DUEL_REQUESTS_EXPIRED));
                new DuelRequestsMenu().openMenu(player);
                return;
            }

            if (this.duelRequest.getArena() == null) {
                player.sendMessage(localeService.getString(GlobalMessagesLocaleImpl.ERROR_DUEL_REQUESTS_NO_ARENA));
                new DuelRequestsMenu().openMenu(player);
                return;
            }

            if (AlleyPractice.getInstance().getService(ProfileService.class).getProfile(player.getUniqueId()).getMatch() != null) {
                player.sendMessage(this.plugin.getService(LocaleService.class).getString(GlobalMessagesLocaleImpl.ERROR_YOU_ALREADY_PLAYING_MATCH));
                return;
            }

            ServerService serverService = AlleyPractice.getInstance().getService(ServerService.class);
            if (!serverService.isQueueingAllowed()) {
                player.sendMessage(AlleyPractice.getInstance().getService(LocaleService.class).getString(GlobalMessagesLocaleImpl.QUEUE_TEMPORARILY_DISABLED));
                player.closeInventory();
                return;
            }

            AlleyPractice.getInstance().getService(DuelRequestService.class).acceptPendingRequest(this.duelRequest);
            player.closeInventory();
        }
    }

    @AllArgsConstructor
    private static class RefreshDuelRequestsButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.EMERALD)
                    .name("&a&lRefresh")
                    .lore("&aClick to refresh duel requests.")
                    .hideMeta()
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (clickType != ClickType.LEFT) return;

            new DuelRequestsMenu().openMenu(player);
        }
    }
}