package live.pvphard.practice.core.profile.menu.shop.button;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.library.menu.Button;
import live.pvphard.practice.feature.cosmetic.model.CosmeticType;
import live.pvphard.practice.feature.cosmetic.internal.repository.BaseCosmeticRepository;
import live.pvphard.practice.feature.cosmetic.CosmeticService;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.menu.shop.ShopCategoryMenu;
import live.pvphard.practice.common.item.ItemBuilder;
import live.pvphard.practice.common.text.StringUtil;
import live.pvphard.practice.common.text.CC;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
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
@RequiredArgsConstructor
public class ShopCategoryButton extends Button {
    private final CosmeticType cosmeticType;
    private final Material icon;

    @Override
    public ItemStack getButtonItem(Player player) {
        String friendlyName = StringUtil.formatEnumName(cosmeticType);

        int totalCount = 0;
        int ownedCount = 0;
        int balance = HardPractice.getInstance().getService(ProfileService.class).getProfile(player.getUniqueId()).getProfileData().getCoins();

        BaseCosmeticRepository<?> repository = HardPractice.getInstance().getService(CosmeticService.class).getRepository(cosmeticType);
        if (repository != null) {
            totalCount = repository.getCosmetics().size();
            ownedCount = (int) repository.getCosmetics().stream()
                    .filter(c -> player.hasPermission(c.getPermission()))
                    .count();
        }

        int percentage = (totalCount == 0) ? 0 : (int) (((double) ownedCount / totalCount) * 100);

        String description = cosmeticType.getDescription();

        List<String> lore = new ArrayList<>();
        lore.add(CC.MENU_BAR);
        lore.add(String.format("&7%s", description));
        lore.add("");
        lore.add(String.format("&b│ &fUnlocked: &b%d/%d &7(%d%%)", ownedCount, totalCount, percentage));
        lore.add(String.format("&b│ &fBalance: &b$%d", balance));
        lore.add("");
        lore.add("&aClick to view.");
        lore.add(CC.MENU_BAR);

        return new ItemBuilder(this.icon)
                .name("&b&l" + friendlyName + "s")
                .lore(lore)
                .hideMeta()
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        this.playNeutral(player);
        new ShopCategoryMenu(cosmeticType).openMenu(player);
    }
}