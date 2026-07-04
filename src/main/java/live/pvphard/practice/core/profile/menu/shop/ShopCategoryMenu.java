package live.pvphard.practice.core.profile.menu.shop;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.library.menu.Button;
import live.pvphard.practice.library.menu.Menu;
import live.pvphard.practice.library.menu.impl.BackButton;
import live.pvphard.practice.feature.cosmetic.model.CosmeticType;
import live.pvphard.practice.feature.cosmetic.internal.repository.BaseCosmeticRepository;
import live.pvphard.practice.feature.cosmetic.CosmeticService;
import live.pvphard.practice.core.profile.menu.shop.button.ShopItemButton;
import live.pvphard.practice.common.text.StringUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Remi
 * @project Hard
 * @date 6/23/2025
 */
@RequiredArgsConstructor
public class ShopCategoryMenu extends Menu {

    private final CosmeticType cosmeticType;

    @Override
    public String getTitle(Player player) {
        return "&b&lShop - " + StringUtil.formatEnumName(cosmeticType) + "s";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        buttons.put(0, new BackButton(new ShopMenu()));

        BaseCosmeticRepository<?> repository = HardPractice.getInstance().getService(CosmeticService.class).getRepository(cosmeticType);
        if (repository != null) {
            repository.getCosmetics().stream()
                    .filter(cosmetic -> cosmetic.getIcon() != null && cosmetic.getPrice() > 0)
                    .forEach(cosmetic -> buttons.put(cosmetic.getSlot(), new ShopItemButton(cosmetic)));
        }

        this.addBorder(buttons, 15, 5);

        return buttons;
    }
}
