package us.alleypvp.practice.feature.party.menu.event.impl.button;

import us.alleypvp.practice.AlleyPractice;
import us.alleypvp.practice.common.item.ItemBuilder;
import us.alleypvp.practice.common.text.CC;
import us.alleypvp.practice.core.locale.LocaleService;
import us.alleypvp.practice.core.locale.internal.impl.SettingsLocaleImpl;
import us.alleypvp.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import us.alleypvp.practice.core.profile.Profile;
import us.alleypvp.practice.core.profile.ProfileService;
import us.alleypvp.practice.feature.arena.Arena;
import us.alleypvp.practice.feature.arena.ArenaService;
import us.alleypvp.practice.feature.kit.Kit;
import us.alleypvp.practice.feature.party.Party;
import us.alleypvp.practice.feature.party.PartyService;
import us.alleypvp.practice.feature.party.menu.event.impl.PartyEventSplitArenaSelectorMenu;
import us.alleypvp.practice.library.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

/**
 * @author Emmy
 * @project Alley
 * @since 16/06/2025
 */
@AllArgsConstructor
public class PartyEventSplitButton extends Button {
    protected final AlleyPractice plugin = AlleyPractice.getInstance();
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

        ProfileService profileService = AlleyPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService != null ? profileService.getProfile(player.getUniqueId()) : null;
        Party party = profile != null ? profile.getParty() : null;
        if (party == null) {
            player.closeInventory();
            player.sendMessage(AlleyPractice.getInstance().getService(LocaleService.class).getString(GlobalMessagesLocaleImpl.ERROR_YOU_NOT_IN_PARTY));
            return;
        }

        Arena arena = AlleyPractice.getInstance().getService(ArenaService.class).getRandomArena(this.kit);
        if (arena == null) {
            player.closeInventory();
            player.sendMessage(CC.translate("&cNenhuma arena disponível para este kit no momento."));
            return;
        }
        AlleyPractice.getInstance().getService(PartyService.class).startMatch(this.kit, arena, party);
    }
}
