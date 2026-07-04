package live.pvphard.practice.feature.cosmetic.menu;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.library.menu.Button;
import live.pvphard.practice.library.menu.Menu;
import live.pvphard.practice.library.menu.impl.BackButton;
import live.pvphard.practice.feature.cosmetic.model.CosmeticType;
import live.pvphard.practice.feature.cosmetic.menu.button.CosmeticButton;
import live.pvphard.practice.feature.cosmetic.internal.repository.BaseCosmeticRepository;
import live.pvphard.practice.feature.cosmetic.CosmeticService;
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
public class CosmeticTypeMenu extends Menu {

    private final CosmeticType cosmeticType;

    @Override
    public String getTitle(Player player) {
        String friendlyName = StringUtil.formatEnumName(cosmeticType);
        return "&b&l" + friendlyName;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();
        buttons.put(0, new BackButton(new CosmeticsMenu()));

        BaseCosmeticRepository<?> repository = HardPractice.getInstance().getService(CosmeticService.class).getRepository(this.cosmeticType);
        if (repository != null) {
            repository.getCosmetics().stream()
                    .filter(cosmetic -> cosmetic.getIcon() != null)
                    .forEach(cosmetic -> buttons.put(cosmetic.getSlot(), new CosmeticButton(cosmetic)));
        }

        this.addBorder(buttons, 15, 5);
        return buttons;
    }
}