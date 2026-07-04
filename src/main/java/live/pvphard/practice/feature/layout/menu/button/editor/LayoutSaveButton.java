package live.pvphard.practice.feature.layout.menu.button.editor;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.common.InventoryUtil;
import live.pvphard.practice.library.menu.Button;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.feature.layout.LayoutService;
import live.pvphard.practice.feature.layout.data.LayoutData;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.common.item.ItemBuilder;
import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.library.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class LayoutSaveButton extends Button {
    private final Kit kit;
    private final LayoutData layout;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.WOOL)
                .name("&b&lSave")
                .durability(13)
                .lore(
                        CC.MENU_BAR,
                        "&7Saves changes and",
                        "&7returns to the main menu.",
                        "",
                        "&aClick to save.",
                        CC.MENU_BAR
                )
                .hideMeta()
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        if (clickType != ClickType.LEFT) return;
        if (this.kit == null || this.layout == null) {
            player.sendMessage(CC.translate("&cCould not save this layout right now."));
            return;
        }

        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService != null ? profileService.getProfile(player.getUniqueId()) : null;
        if (profile == null || profile.getProfileData() == null || profile.getProfileData().getLayoutData() == null) {
            player.sendMessage(CC.translate("&cCould not save this layout right now."));
            return;
        }

        LayoutData layout = profile.getProfileData().getLayoutData().getLayout(this.kit.getName(), this.layout.getName());
        if (layout == null) {
            player.sendMessage(CC.translate("&cLayout not found for this kit."));
            return;
        }

        layout.setDisplayName(this.layout.getDisplayName());
        layout.setItems(InventoryUtil.cloneItemStackArray(player.getInventory().getContents()));
        profile.save();

        LayoutService layoutService = HardPractice.getInstance().getService(LayoutService.class);
        Menu layoutMenu = layoutService != null ? layoutService.getLayoutMenu() : null;
        if (layoutMenu == null) {
            player.sendMessage(CC.translate("&cCould not return to the layout menu right now."));
            return;
        }

        layoutMenu.openMenu(player);
    }
}