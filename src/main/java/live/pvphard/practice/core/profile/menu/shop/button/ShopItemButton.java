package live.pvphard.practice.core.profile.menu.shop.button;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.common.item.ItemBuilder;
import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.locale.LocaleService;
import live.pvphard.practice.core.locale.internal.impl.SettingsLocaleImpl;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.feature.cosmetic.model.BaseCosmetic;
import live.pvphard.practice.library.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Remi
 * @project Hard
 * @date 6/23/2025
 */
@AllArgsConstructor
public class ShopItemButton extends Button {
    private final BaseCosmetic cosmetic;

    @Override
    public ItemStack getButtonItem(Player player) {
        boolean hasPermission = player.hasPermission(cosmetic.getPermission());

        List<String> lore = new ArrayList<>();
        lore.add(CC.MENU_BAR);
        lore.addAll(cosmetic.getDisplayLore());
        lore.add("");

        if (hasPermission) {
            lore.add("&aYou already own this item.");
        } else {
            lore.add(" &fPrice: &b$" + cosmetic.getPrice());
            lore.add("");
            lore.add("&aClick to purchase.");
        }
        lore.add(CC.MENU_BAR);

        return new ItemBuilder(cosmetic.getIcon())
                .name("&b&l" + cosmetic.getName())
                .lore(lore)
                .hideMeta()
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        Profile profile = HardPractice.getInstance().getService(ProfileService.class).getProfile(player.getUniqueId());
        LocaleService localeService = HardPractice.getInstance().getService(LocaleService.class);

        if (player.hasPermission(cosmetic.getPermission())) {
            player.sendMessage(localeService.getString(GlobalMessagesLocaleImpl.COSMETIC_ALREADY_OWNED));
            this.playFail(player);
            return;
        }

        if (profile.getProfileData().getCoins() < cosmetic.getPrice()) {
            player.sendMessage(localeService.getString(GlobalMessagesLocaleImpl.COSMETIC_PURCHASE_INSUFFICIENT_FUNDS));
            this.playFail(player);
            return;
        }

        profile.getProfileData().setCoins(profile.getProfileData().getCoins() - cosmetic.getPrice());

        String command = localeService.getString(SettingsLocaleImpl.GRANT_COSMETIC_PERMISSION_COMMAND)
                .replace("{player}", player.getName())
                .replace("{permission}", cosmetic.getPermission());

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);

        player.sendMessage(localeService.getString(GlobalMessagesLocaleImpl.COSMETIC_PURCHASE_SUCCESS).replace("{cosmetic}", cosmetic.getName()));
        this.playSuccess(player);
    }
}