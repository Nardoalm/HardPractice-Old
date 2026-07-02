package us.alleypvp.practice.core.profile.menu.shop;

import us.alleypvp.practice.AlleyPractice;
import us.alleypvp.practice.library.menu.Button;
import us.alleypvp.practice.library.menu.Menu;
import us.alleypvp.practice.library.menu.impl.BackButton;
import us.alleypvp.practice.feature.cosmetic.model.CosmeticType;
import us.alleypvp.practice.feature.cosmetic.internal.repository.BaseCosmeticRepository;
import us.alleypvp.practice.feature.cosmetic.CosmeticService;
import us.alleypvp.practice.core.profile.menu.shop.button.ShopItemButton;
import us.alleypvp.practice.common.text.StringUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Remi
 * @project Alley
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

        BaseCosmeticRepository<?> repository = AlleyPractice.getInstance().getService(CosmeticService.class).getRepository(cosmeticType);
        if (repository != null) {
            repository.getCosmetics().stream()
                    .filter(cosmetic -> cosmetic.getIcon() != null && cosmetic.getPrice() > 0)
                    .forEach(cosmetic -> buttons.put(cosmetic.getSlot(), new ShopItemButton(cosmetic)));
        }

        this.addBorder(buttons, 15, 5);

        return buttons;
    }
}
