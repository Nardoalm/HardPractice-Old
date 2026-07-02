package us.alleypvp.practice.feature.kit.menu;

import us.alleypvp.practice.AlleyPractice;
import us.alleypvp.practice.common.item.ItemBuilder;
import us.alleypvp.practice.common.text.CC;
import us.alleypvp.practice.core.locale.LocaleService;
import us.alleypvp.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import us.alleypvp.practice.feature.kit.Kit;
import us.alleypvp.practice.feature.kit.KitService;
import us.alleypvp.practice.library.menu.Button;
import us.alleypvp.practice.library.menu.pagination.PaginatedMenu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Emmy
 * @project Alley
 * @since 06/25/2025
 */
@AllArgsConstructor
public class KitPotionListMenu extends PaginatedMenu {
    private final Kit kit;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "&b&lPotions of " + this.kit.getDisplayName();
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        this.addGlassHeader(buttons, 15);

        return buttons;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        List<PotionEffect> potionEffects = this.kit.getPotionEffects();
        int index = 0;

        for (PotionEffect potionEffect : potionEffects) {
            buttons.put(index++, new KitPotionButton(this.kit, potionEffect));
        }

        return buttons;
    }

    @AllArgsConstructor
    private static class KitPotionButton extends Button {
        private final Kit kit;
        private final PotionEffect potionEffect;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.POTION)
                    .name("&b" + this.potionEffect.getType().getName())
                    .lore(
                            "&7Duration: &b" + this.potionEffect.getDuration() / 20 + " seconds",
                            "&7Amplifier: &b" + this.potionEffect.getAmplifier(),
                            "",
                            "&7Click to remove this potion effect."
                    )
                    .hideMeta()
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (clickType != ClickType.LEFT) return;

            this.kit.getPotionEffects().remove(this.potionEffect);
            AlleyPractice.getInstance().getService(KitService.class).saveKit(this.kit);

            LocaleService localeService = AlleyPractice.getInstance().getService(LocaleService.class);
            player.sendMessage(CC.translate(localeService.getString(GlobalMessagesLocaleImpl.KIT_POTION_EFFECT_REMOVED)
                    .replace("{potion-effect}", this.potionEffect.getType().getName())
                    .replace("{kit-name}", this.kit.getName()))
            );

            new KitPotionListMenu(this.kit).openMenu(player);
        }
    }
}