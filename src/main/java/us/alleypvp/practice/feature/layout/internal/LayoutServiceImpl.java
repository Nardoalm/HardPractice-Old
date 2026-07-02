package us.alleypvp.practice.feature.layout.internal;

import us.alleypvp.practice.feature.layout.LayoutService;
import us.alleypvp.practice.library.menu.Menu;
import us.alleypvp.practice.feature.kit.KitCategory;
import us.alleypvp.practice.core.config.ConfigService;
import us.alleypvp.practice.bootstrap.KaosContext;
import us.alleypvp.practice.bootstrap.annotation.Service;
import us.alleypvp.practice.feature.layout.data.LayoutData;
import us.alleypvp.practice.feature.layout.menu.LayoutMenu;
import us.alleypvp.practice.core.profile.ProfileService;
import us.alleypvp.practice.core.profile.Profile;
import us.alleypvp.practice.common.item.ItemBuilder;
import us.alleypvp.practice.common.logger.Logger;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

/**
 * @author Emmy
 * @project Alley
 * @since 03/05/2025
 */
@Getter
@Service(provides = LayoutService.class, priority = 350)
public class LayoutServiceImpl implements LayoutService {
    private final ConfigService configService;
    private final ProfileService profileService;

    private Menu layoutMenu;

    /**
     * Constructor for DI.
     */
    public LayoutServiceImpl(ConfigService configService, ProfileService profileService) {
        this.configService = configService;
        this.profileService = profileService;
    }

    @Override
    public void initialize(KaosContext context) {
        this.layoutMenu = this.determineMenu();
    }

    private Menu determineMenu() {
        FileConfiguration config = this.configService.getMenusConfig();
        String menuType = config.getString("layout-menu.type", "DEFAULT");

        switch (menuType) {
            case "MODERN":
                Logger.error("Modern layout menu has not been implemented yet. Using classic menu.");
                return new LayoutMenu(KitCategory.NORMAL);
            case "DEFAULT":
                return new LayoutMenu(KitCategory.NORMAL);
        }

        Logger.error("Invalid layout menu type in config.yml. Using classic menu.");
        return new LayoutMenu(KitCategory.NORMAL);
    }

    @Override
    public ItemStack getLayoutBook(LayoutData layout) {
        String displayName = (layout != null && layout.getDisplayName() != null && !layout.getDisplayName().trim().isEmpty())
                ? layout.getDisplayName()
                : "&7Layout";

        return new ItemBuilder(Material.BOOK)
                .name(displayName)
                .lore("&7Click to select this layout.")
                .hideMeta().build();
    }

    @Override
    public void giveBooks(Player player, String kitName) {
        Profile profile = this.profileService.getProfile(player.getUniqueId());
        if (profile == null
                || profile.getProfileData() == null
                || profile.getProfileData().getLayoutData() == null
                || profile.getProfileData().getLayoutData().getLayouts() == null) {
            return;
        }

        List<LayoutData> layouts = profile.getProfileData().getLayoutData().getLayouts().get(kitName);
        if (layouts == null || layouts.isEmpty()) {
            return;
        }

        layouts.stream()
                .filter(Objects::nonNull)
                .forEach(layout -> player.getInventory().addItem(this.getLayoutBook(layout)));
    }
}