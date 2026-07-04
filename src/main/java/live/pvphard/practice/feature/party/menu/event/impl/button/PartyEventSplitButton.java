package live.pvphard.practice.feature.party.menu.event.impl.button;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.common.item.ItemBuilder;
import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.locale.LocaleService;
import live.pvphard.practice.core.locale.internal.impl.SettingsLocaleImpl;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.feature.arena.Arena;
import live.pvphard.practice.feature.arena.ArenaService;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.feature.party.Party;
import live.pvphard.practice.feature.party.PartyService;
import live.pvphard.practice.feature.party.menu.event.impl.PartyEventSplitArenaSelectorMenu;
import live.pvphard.practice.library.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

/**
 * @author Emmy
 * @project Hard
 * @since 16/06/2025
 */
@AllArgsConstructor
public class PartyEventSplitButton extends Button {
    protected final HardPractice plugin = HardPractice.getInstance();
    private final Kit kit;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(this.kit.getIcon())
                .name("&b&l" + this.kit.getDisplayName())
                .lore(
                        CC.MENU_BAR,
                        "&aClick to select!",
                        CC.MENU_BAR
                )
                .durability(this.kit.getDurability())
                .hideMeta()
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        if (clickType != ClickType.LEFT) return;

        if (player.hasPermission(this.plugin.getService(LocaleService.class).getString(SettingsLocaleImpl.PERMISSION_DONATOR_PARTY_ARENA_SELECTOR))) {
            new PartyEventSplitArenaSelectorMenu(this.kit).openMenu(player);
            return;
        }

        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService != null ? profileService.getProfile(player.getUniqueId()) : null;
        Party party = profile != null ? profile.getParty() : null;
        if (party == null) {
            player.closeInventory();
            player.sendMessage(HardPractice.getInstance().getService(LocaleService.class).getString(GlobalMessagesLocaleImpl.ERROR_YOU_NOT_IN_PARTY));
            return;
        }

        Arena arena = HardPractice.getInstance().getService(ArenaService.class).getRandomArena(this.kit);
        if (arena == null) {
            player.closeInventory();
            player.sendMessage(CC.translate("&cNenhuma arena disponível para este kit no momento."));
            return;
        }
        HardPractice.getInstance().getService(PartyService.class).startMatch(this.kit, arena, party);
    }
}
