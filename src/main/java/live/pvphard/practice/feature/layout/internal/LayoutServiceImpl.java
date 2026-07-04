package live.pvphard.practice.feature.layout.internal;

import live.pvphard.practice.bootstrap.HardContext;
import live.pvphard.practice.feature.layout.LayoutService;
import live.pvphard.practice.library.menu.Menu;
import live.pvphard.practice.feature.kit.KitCategory;
import live.pvphard.practice.core.config.ConfigService;
import live.pvphard.practice.bootstrap.annotation.Service;
import live.pvphard.practice.feature.layout.data.LayoutData;
import live.pvphard.practice.feature.layout.menu.LayoutMenu;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.common.item.ItemBuilder;
import live.pvphard.practice.common.logger.Logger;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

/**
 * @author Emmy
 * @project Hard
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
    public void initialize(HardContext context) {
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