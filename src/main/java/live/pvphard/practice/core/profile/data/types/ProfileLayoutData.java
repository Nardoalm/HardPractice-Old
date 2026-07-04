package live.pvphard.practice.core.profile.data.types;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.common.InventoryUtil;
import live.pvphard.practice.feature.kit.KitService;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.feature.layout.data.LayoutData;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Emmy
 * @project Hard
 * @since 02/05/2025
 */
@Getter
@Setter
public class ProfileLayoutData {
    private Map<String, List<LayoutData>> layouts;

    public ProfileLayoutData() {
        this.layouts = new LinkedHashMap<>();

        KitService kitService = HardPractice.getInstance().getService(KitService.class);
        if (kitService == null || kitService.getKits() == null) {
            return;
        }

        for (Kit kit : kitService.getKits()) {
            if (kit == null || kit.getName() == null || kit.getName().trim().isEmpty()) {
                continue;
            }

            List<LayoutData> defaultLayoutList = new ArrayList<>();
            defaultLayoutList.add(new LayoutData("Layout1", "Modelo 1", InventoryUtil.getEditableKitItems(kit)));
            this.layouts.put(kit.getName(), defaultLayoutList);
        }
    }

    /**
     * Adds a new layout to the list.
     *
     * @param name        the name of the layout.
     * @param displayName the display name of the layout.
     * @param items       the items in the layout.
     */
    public void addLayout(String kitName, String name, String displayName, ItemStack[] items) {
        if (kitName == null || kitName.trim().isEmpty()) {
            return;
        }

        if (this.layouts == null) {
            this.layouts = new LinkedHashMap<>();
        }

        List<LayoutData> existingLayouts = this.layouts.computeIfAbsent(kitName, key -> new ArrayList<>());
        String safeName = (name == null || name.trim().isEmpty()) ? "Layout" + (existingLayouts.size() + 1) : name;
        String safeDisplayName = (displayName == null || displayName.trim().isEmpty()) ? safeName : displayName;

        LayoutData newLayout = new LayoutData(safeName, safeDisplayName, InventoryUtil.cloneItemStackArray(items));

        existingLayouts.add(newLayout);
    }

    /**
     * Accessor method to get the layout by name.
     *
     * @param kitName    the name of the kit.
     * @param layoutName the name of the layout.
     * @return the layout model if found, null otherwise.
     */
    public LayoutData getLayout(String kitName, String layoutName) {
        if (this.layouts == null || kitName == null || layoutName == null) {
            return null;
        }

        List<LayoutData> layoutsByKit = this.layouts.get(kitName);
        if (layoutsByKit == null || layoutsByKit.isEmpty()) {
            return null;
        }

        for (LayoutData layout : layoutsByKit) {
            if (layout != null && layout.getName() != null && layout.getName().equalsIgnoreCase(layoutName)) {
                return layout;
            }
        }

        return null;
    }
}
