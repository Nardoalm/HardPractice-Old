package us.alleypvp.practice.feature.layout.menu.button;

import us.alleypvp.practice.AlleyPractice;
import us.alleypvp.practice.common.InventoryUtil;
import us.alleypvp.practice.common.constants.MessageConstant;
import us.alleypvp.practice.common.item.ItemBuilder;
import us.alleypvp.practice.common.text.CC;
import us.alleypvp.practice.common.text.Symbol;
import us.alleypvp.practice.core.profile.Profile;
import us.alleypvp.practice.core.profile.ProfileService;
import us.alleypvp.practice.core.profile.enums.ProfileState;
import us.alleypvp.practice.feature.kit.Kit;
import us.alleypvp.practice.feature.kit.setting.types.mode.KitSettingRaiding;
import us.alleypvp.practice.feature.layout.data.LayoutData;
import us.alleypvp.practice.feature.layout.menu.LayoutEditorMenu;
import us.alleypvp.practice.feature.layout.menu.LayoutSelectRoleKitMenu;
import us.alleypvp.practice.library.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

@AllArgsConstructor
public class LayoutButton extends Button {
    private final Kit kit;

    @Override
    public ItemStack getButtonItem(Player player) {
        if (this.kit == null) {
            return new ItemBuilder(Material.BARRIER)
                    .name("&cUnavailable kit")
                    .lore(CC.MENU_BAR, "&7Could not load this kit.", CC.MENU_BAR)
                    .hideMeta()
                    .build();
        }

        return new ItemBuilder(this.kit.getIcon())
                .name("&b&l" + this.kit.getDisplayName())
                .durability(this.kit.getDurability())
                .lore(
                        CC.MENU_BAR,
                        "&7Shift-Click: &c(Not implemented)",
                        " &7" + Symbol.SINGULAR_ARROW_R_2 + " More layouts",
                        "",
                        "&aClick to edit.",
                        CC.MENU_BAR
                )
                .hideMeta()
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        ProfileService profileService = AlleyPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService != null ? profileService.getProfile(player.getUniqueId()) : null;
        if (profile == null || this.kit == null) {
            player.sendMessage(CC.translate("&cCould not open this layout right now."));
            return;
        }

        if (profile.getState() == ProfileState.WAITING) {
            player.sendMessage(CC.translate("&cLeave the queue to edit the layout."));
            return;
        }

        if (clickType == ClickType.LEFT) {
            if (this.kit.isSettingEnabled(KitSettingRaiding.class)) {
                new LayoutSelectRoleKitMenu(this.kit).openMenu(player);
                return;
            }

            LayoutData layout = this.getOrCreateFirstLayout(profile, this.kit);
            if (layout == null) {
                player.sendMessage(CC.translate("&cNo layout found for this kit."));
                return;
            }
            new LayoutEditorMenu(this.kit, layout).openMenu(player);
        } else if (clickType == ClickType.SHIFT_LEFT) {
            player.sendMessage(MessageConstant.IN_DEVELOPMENT);
        }
    }

    private LayoutData getOrCreateFirstLayout(Profile profile, Kit kit) {
        if (profile == null
                || profile.getProfileData() == null
                || profile.getProfileData().getLayoutData() == null
                || profile.getProfileData().getLayoutData().getLayouts() == null
                || kit == null
                || kit.getName() == null) {
            return null;
        }

        List<LayoutData> layouts = profile.getProfileData().getLayoutData().getLayouts().get(kit.getName());
        if (layouts == null || layouts.isEmpty()) {
            profile.getProfileData().getLayoutData().addLayout(
                    kit.getName(),
                    "Layout1",
                    "Template 1",
                    InventoryUtil.getEditableKitItems(kit)
            );
            layouts = profile.getProfileData().getLayoutData().getLayouts().get(kit.getName());
        }

        return layouts.stream().filter(Objects::nonNull).findFirst().orElse(null);
    }
}