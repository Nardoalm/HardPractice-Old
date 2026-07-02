package us.alleypvp.practice.feature.title.menu;

import us.alleypvp.practice.AlleyPractice;
import us.alleypvp.practice.common.item.ItemBuilder;
import us.alleypvp.practice.common.text.CC;
import us.alleypvp.practice.core.profile.Profile;
import us.alleypvp.practice.core.profile.progress.PlayerProgress;
import us.alleypvp.practice.core.profile.progress.ProgressService;
import us.alleypvp.practice.feature.title.model.TitleRecord;
import us.alleypvp.practice.library.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

/**
 * @author Emmy
 * @project Alley
 * @since 22/04/2025
 */
@AllArgsConstructor
public class TitleButton extends Button {
    private final Profile profile;
    private final TitleRecord title;

    @Override
    public ItemStack getButtonItem(Player player) {

        if (!this.profile.getProfileData().getUnlockedTitles().contains(this.title.getKit().getName())) {
            return new ItemBuilder(Material.STAINED_GLASS_PANE)
                    .name("&b&l" + this.title.getKit().getName())
                    .lore(
                            this.progress()
                    )
                    .durability(14)
                    .hideMeta()
                    .build();
        }

        return new ItemBuilder(this.title.getKit().getIcon())
                .name("&b&l" + this.title.getKit().getName())
                .lore(
                        CC.MENU_BAR,
                        " &a&lDESBLOQUEADO",
                        "",
                        "&aClick to select!",
                        CC.MENU_BAR
                )
                .durability(this.title.getKit().getDurability())
                .hideMeta()
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        if (clickType != ClickType.LEFT) return;

        if (!this.profile.getProfileData().getUnlockedTitles().contains(this.title.getKit().getName())) {
            player.sendMessage(CC.translate("&cPara selecionar este título, você precisa desbloqueá-lo primeiro."));
            return;
        }

        if (this.profile.getProfileData().getSelectedTitle().equals(this.title.getKit().getName())) {
            player.sendMessage(CC.translate("&cVocê já está com este título selecionado."));
            return;
        }

        this.profile.getProfileData().setSelectedTitle(this.title.getKit().getName());
        player.sendMessage(CC.translate("&aVocê selecionou o título &b" + this.title.getKit().getName() + "&a."));
    }

    /**
     * Get the progress of the title.
     *
     * @return A list of strings representing the progress.
     */
    private List<String> progress() {
        PlayerProgress progress = AlleyPractice.getInstance().getService(ProgressService.class).calculateProgress(this.profile, this.title.getKit().getName());

        return Arrays.asList(
                CC.MENU_BAR,
                " &c&lBLOQUEADO",
                "",
                String.format(" &fDesbloqueie &b%s &fcom mais %d %s.",
                        progress.getNextRankName(),
                        progress.getWinsRequired(),
                        progress.getWinOrWins()
                ),
                "&f " + progress.getProgressBar(12, "■") + " &7" + progress.getProgressPercentage(),
                "",
                "&fRequer a divisão &c&l" + this.title.getRequiredDivision().getName().toUpperCase() + "&f.",
                CC.MENU_BAR
        );
    }
}
