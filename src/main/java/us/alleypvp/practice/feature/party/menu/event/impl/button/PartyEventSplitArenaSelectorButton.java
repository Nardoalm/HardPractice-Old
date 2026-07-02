package us.alleypvp.practice.feature.party.menu.event.impl.button;

import us.alleypvp.practice.AlleyPractice;
import us.alleypvp.practice.common.item.ItemBuilder;
import us.alleypvp.practice.common.text.CC;
import us.alleypvp.practice.core.locale.LocaleService;
import us.alleypvp.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import us.alleypvp.practice.core.profile.Profile;
import us.alleypvp.practice.core.profile.ProfileService;
import us.alleypvp.practice.feature.arena.Arena;
import us.alleypvp.practice.feature.kit.Kit;
import us.alleypvp.practice.feature.party.Party;
import us.alleypvp.practice.feature.party.PartyService;
import us.alleypvp.practice.library.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

/**
 * @author Emmy
 * @project Alley
 * @since 16/06/2025
 */
@AllArgsConstructor
public class PartyEventSplitArenaSelectorButton extends Button {
    protected final AlleyPractice plugin = AlleyPractice.getInstance();
    private final Kit kit;
    private final Arena arena;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.PAPER)
                .name("&b&l" + this.arena.getName())
                .lore(
                        " &f◆ &bKit: &f" + this.kit.getDisplayName(),
                        "",
                        "&aClick to select!"
                )
                .durability(0)
                .hideMeta()
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        if (clickType != ClickType.LEFT) return;

        ProfileService profileService = AlleyPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService != null ? profileService.getProfile(player.getUniqueId()) : null;
        Party party = profile != null ? profile.getParty() : null;
        if (party == null) {
            player.closeInventory();
            player.sendMessage(AlleyPractice.getInstance().getService(LocaleService.class).getString(GlobalMessagesLocaleImpl.ERROR_YOU_NOT_IN_PARTY));
            return;
        }

        if (this.arena == null || !this.arena.isEnabled()) {
            player.closeInventory();
            player.sendMessage(CC.translate("&cEssa arena está indisponível agora."));
            return;
        }

        PartyService partyService = AlleyPractice.getInstance().getService(PartyService.class);
        partyService.startMatch(this.kit, this.arena, party);
    }
}
