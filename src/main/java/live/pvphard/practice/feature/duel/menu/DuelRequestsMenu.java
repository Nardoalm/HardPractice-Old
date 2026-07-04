package live.pvphard.practice.feature.duel.menu;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.common.item.ItemBuilder;
import live.pvphard.practice.core.locale.LocaleService;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.feature.duel.DuelRequest;
import live.pvphard.practice.feature.duel.DuelRequestService;
import live.pvphard.practice.feature.server.ServerService;
import live.pvphard.practice.library.menu.Button;
import live.pvphard.practice.library.menu.pagination.PaginatedMenu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Emmy
 * @project Hard
 * @date 22/10/2024 - 18:18
 */
public class DuelRequestsMenu extends PaginatedMenu {
    protected final HardPractice plugin = HardPractice.getInstance();

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

        HardPractice.getInstance().getService(DuelRequestService.class).getDuelRequests()
                .stream()
                .filter(duelRequest -> !duelRequest.getSender().equals(player))
                .forEach(duelRequest -> buttons.put(buttons.size(), new DuelRequestsButton(duelRequest)));


        return buttons;
    }

    @AllArgsConstructor
    private static class DuelRequestsButton extends Button {
        protected final HardPractice plugin = HardPractice.getInstance();
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

            LocaleService localeService = HardPractice.getInstance().getService(LocaleService.class);

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

            if (HardPractice.getInstance().getService(ProfileService.class).getProfile(player.getUniqueId()).getMatch() != null) {
                player.sendMessage(this.plugin.getService(LocaleService.class).getString(GlobalMessagesLocaleImpl.ERROR_YOU_ALREADY_PLAYING_MATCH));
                return;
            }

            ServerService serverService = HardPractice.getInstance().getService(ServerService.class);
            if (!serverService.isQueueingAllowed()) {
                player.sendMessage(HardPractice.getInstance().getService(LocaleService.class).getString(GlobalMessagesLocaleImpl.QUEUE_TEMPORARILY_DISABLED));
                player.closeInventory();
                return;
            }

            HardPractice.getInstance().getService(DuelRequestService.class).acceptPendingRequest(this.duelRequest);
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