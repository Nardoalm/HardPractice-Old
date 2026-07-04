package live.pvphard.practice.feature.layout.menu;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.common.InventoryUtil;
import live.pvphard.practice.feature.layout.menu.button.editor.*;
import live.pvphard.practice.library.menu.Button;
import live.pvphard.practice.library.menu.Menu;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.feature.layout.data.LayoutData;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.enums.ProfileState;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Emmy
 * @project Hard
 * @since 03/05/2025
 */
@AllArgsConstructor
public class LayoutEditorMenu extends Menu {
    protected final HardPractice plugin = HardPractice.getInstance();
    private final Kit kit;
    private final LayoutData layout;

    @Override
    public void onOpen(Player player) {
        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService != null ? profileService.getProfile(player.getUniqueId()) : null;
        if (profile != null) {
            profile.setState(ProfileState.EDITING);
        }

        if (this.hasLayoutItems(this.layout)) {
            player.getInventory().setContents(InventoryUtil.cloneItemStackArray(this.layout.getItems()));
        } else {
            player.getInventory().setContents(InventoryUtil.getEditableKitItems(this.kit));
        }

        player.updateInventory();
    }

    @Override
    public void onClose(Player player) {
        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService != null ? profileService.getProfile(player.getUniqueId()) : null;
        if (profile != null) {
            profile.setState(ProfileState.LOBBY);
        }
        super.onClose(player);
    }

    @Override
    public String getTitle(Player player) {
        String layoutName = this.layout != null && this.layout.getDisplayName() != null
                ? this.layout.getDisplayName()
                : "Layout";
        return "&b&lEditing " + layoutName;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        Profile profile = profileService != null ? profileService.getProfile(player.getUniqueId()) : null;

        buttons.put(11, new LayoutSaveButton(this.kit, this.layout));
        buttons.put(13, new LayoutResetItemsButton(this.kit));
        buttons.put(15, new LayoutCancelButton());

        /*
         * If kit isn't the first/default one stored in profile, add the following buttons:
         */
        LayoutData firstLayout = this.getFirstLayout(profile);
        if (firstLayout != null
                && this.layout != null
                && this.layout.getName() != null
                && !this.layout.getName().equals(firstLayout.getName())) {
            buttons.put(21, new LayoutDeleteButton(this.layout));
            buttons.put(23, new LayoutRenameButton(this.layout));
        }

        this.addGlass(buttons, 15);

        return buttons;
    }

    @Override
    public int getSize() {
        //if kit isn't the first/default one, return 9*4 because of the delete and rename buttons
        return 9 * 3;
    }

    @Override
    public boolean isUpdateAfterClick() {
        return false;
    }

    private boolean hasLayoutItems(LayoutData layoutData) {
        return layoutData != null && InventoryUtil.hasAnyItem(layoutData.getItems());
    }

    private LayoutData getFirstLayout(Profile profile) {
        if (profile == null
                || profile.getProfileData() == null
                || profile.getProfileData().getLayoutData() == null
                || profile.getProfileData().getLayoutData().getLayouts() == null
                || this.kit == null) {
            return null;
        }

        List<LayoutData> layouts = profile.getProfileData().getLayoutData().getLayouts().get(this.kit.getName());
        if (layouts == null || layouts.isEmpty()) {
            return null;
        }

        return layouts.stream().filter(Objects::nonNull).findFirst().orElse(null);
    }
}
