package live.pvphard.practice.feature.level.menu;

import live.pvphard.practice.library.menu.Button;
import live.pvphard.practice.feature.level.data.LevelData;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.common.item.ItemBuilder;
import live.pvphard.practice.common.text.CC;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Emmy
 * @project Hard
 * @since 22/04/2025
 */
@AllArgsConstructor
public class LevelButton extends Button {
    private final Profile profile;
    private final LevelData level;

    @Override
    public ItemStack getButtonItem(Player player) {
        if (this.profile.getProfileData().getElo() >= this.level.getMinElo()) {
            return new ItemBuilder(this.level.getMaterial())
                    .name(this.level.getDisplayName())
                    .lore(
                            CC.MENU_BAR,
                            "&a&lDESBLOQUEADO",
                            CC.MENU_BAR
                    )
                    .durability(this.level.getDurability())
                    .hideMeta()
                    .build();
        }

        int requiredElo = this.level.getMinElo() - this.profile.getProfileData().getElo();

        return new ItemBuilder(Material.STAINED_GLASS_PANE)
                .name(this.level.getDisplayName())
                .lore(
                        CC.MENU_BAR,
                        " &c&lBLOQUEADO",
                        "",
                        " &fDesbloqueie com mais &b" + requiredElo + " &fde Elo!",
                        " &fElo Atual: &b" + this.profile.getProfileData().getElo(),
                        CC.MENU_BAR
                )
                .durability(14)
                .hideMeta()
                .build();
    }
}
