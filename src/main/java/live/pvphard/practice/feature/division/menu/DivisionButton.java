package live.pvphard.practice.feature.division.menu;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.library.menu.Button;
import live.pvphard.practice.feature.division.Division;
import live.pvphard.practice.feature.title.menu.TitleMenu;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.common.item.ItemBuilder;
import live.pvphard.practice.common.text.CC;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

/**
 * @author Emmy
 * @project Hard
 * @date 25/01/2025
 */
@Getter
@AllArgsConstructor
public class DivisionButton extends Button {
    private final Division division;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(this.division.getIcon())
                .durability(this.division.getDurability())
                .name("&b&l" + this.division.getDisplayName() + " Division")
                .lore(
                        CC.MENU_BAR,
                        "&f&l● &bTiers: &f" + this.division.getTiers().size(),
                        "  &7▶ (" + this.division.getTiers().get(0).getRequiredWins() + " - " + this.division.getTotalWins() + " wins)",
                        "",
                        " &fFor each kit, you will have",
                        " &fa division based on your",
                        " &bunranked wins&f.",
                        "",
                        "&aClick to view your titles.",
                        CC.MENU_BAR
                )
                .hideMeta()
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        if (clickType != ClickType.LEFT) return;

        new TitleMenu(HardPractice.getInstance().getService(ProfileService.class).getProfile(player.getUniqueId())).openMenu(player);
    }
}