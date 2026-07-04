package live.pvphard.practice.feature.layout.menu;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.common.InventoryUtil;
import live.pvphard.practice.library.menu.Button;
import live.pvphard.practice.library.menu.Menu;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.feature.kit.raiding.BaseRaidingService;
import live.pvphard.practice.feature.layout.data.LayoutData;
import live.pvphard.practice.feature.match.model.BaseRaiderRole;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.common.item.ItemBuilder;
import live.pvphard.practice.common.text.CC;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Emmy
 * @project Hard
 * @since 29/06/2025
 */
@AllArgsConstructor
public class LayoutSelectRoleKitMenu extends Menu {
    private final Kit kit;

    @Override
    public String getTitle(Player player) {
        return "&b&lSelecione uma Função";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        BaseRaidingService baseRaidingService = HardPractice.getInstance().getService(BaseRaidingService.class);
        if (baseRaidingService == null || this.kit == null) {
            buttons.put(12, new RoleButton(BaseRaiderRole.RAIDER, null));
            buttons.put(14, new RoleButton(BaseRaiderRole.TRAPPER, null));
            this.addGlass(buttons, 15);
            return buttons;
        }

        Kit raiderKit = baseRaidingService.getRaidingKitByRole(this.kit, BaseRaiderRole.RAIDER);
        Kit trapperKit = baseRaidingService.getRaidingKitByRole(this.kit, BaseRaiderRole.TRAPPER);

        buttons.put(12, new RoleButton(BaseRaiderRole.RAIDER, raiderKit));
        buttons.put(14, new RoleButton(BaseRaiderRole.TRAPPER, trapperKit));

        this.addGlass(buttons, 15);

        return buttons;
    }

    @Override
    public int getSize() {
        return 3 * 9;
    }

    @AllArgsConstructor
    private static class RoleButton extends Button {
        private final BaseRaiderRole role;
        private final Kit kit;

        @Override
        public ItemStack getButtonItem(Player player) {
            if (this.kit == null) {
                return new ItemBuilder(Material.BARRIER)
                        .name("&cNenhum kit disponível")
                        .lore("&cAlgo deu errado!")
                        .build();
            }

            return new ItemBuilder(this.kit.getIcon())
                    .name("Kit " + this.role.getDisplayName() + " &7(" + this.kit.getDisplayName() + ")")
                    .lore(
                            CC.MENU_BAR,
                            "&aClique para editar.",
                            CC.MENU_BAR
                    )
                    .durability(this.kit.getDurability())
                    .hideMeta()
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (clickType != ClickType.LEFT) return;
            if (this.kit == null) {
                player.sendMessage(CC.translate("&c&lErro: nenhum kit encontrado para esta função."));
                player.closeInventory();
                return;
            }

            ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
            Profile profile = profileService != null ? profileService.getProfile(player.getUniqueId()) : null;
            if (profile == null
                    || profile.getProfileData() == null
                    || profile.getProfileData().getLayoutData() == null
                    || profile.getProfileData().getLayoutData().getLayouts() == null) {
                player.sendMessage(CC.translate("&c&lErro: o perfil de layout não está disponível agora."));
                player.closeInventory();
                return;
            }

            List<LayoutData> layouts = profile.getProfileData().getLayoutData().getLayouts().get(this.kit.getName());
            if (layouts == null || layouts.isEmpty()) {
                profile.getProfileData().getLayoutData().addLayout(this.kit.getName(), "Layout1", "Modelo 1", InventoryUtil.getEditableKitItems(this.kit));
                layouts = profile.getProfileData().getLayoutData().getLayouts().get(this.kit.getName());
            }

            LayoutData layout = layouts == null ? null : layouts.stream().filter(Objects::nonNull).findFirst().orElse(null);
            if (layout == null) {
                player.sendMessage(CC.translate("&c&lErro: nenhum layout encontrado para este kit!"));
                player.closeInventory();
                return;
            }

            new LayoutEditorMenu(this.kit, layout).openMenu(player);
        }
    }
}
