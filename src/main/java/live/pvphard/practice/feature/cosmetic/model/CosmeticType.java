package live.pvphard.practice.feature.cosmetic.model;

import live.pvphard.practice.feature.cosmetic.internal.repository.impl.suit.BaseSuit;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

@Getter
public enum CosmeticType {
    KILL_EFFECT("killeffect", "Shows a special effect when killing a player.", (cosmetic, player) -> {
    }),
    SOUND_EFFECT("soundeffect", "Plays a custom sound when you get a kill.", (cosmetic, player) -> {
    }),
    PROJECTILE_TRAIL("projectiletrail", "Leaves a particle trail on your projectiles.", (cosmetic, player) -> {
    }),
    KILL_MESSAGE("killmessage", "Sends custom messages when you die.", (cosmetic, player) -> {
    }),
    SUIT("suit", "Wear a stylish suit to show off your style.", (cosmetic, player) -> {
        if (cosmetic instanceof BaseSuit) {
            ((BaseSuit) cosmetic).onSelect(player);
        }
    }),
    CLOAK("cloak", "Wear a cloak to show off your style.", (cosmetic, player) -> {
    }),
    TRAIL("trail", "Changes the block beneath your feet as you walk.", (cosmetic, player) -> {
    });

    private final String permissionKey;
    private final String description;
    private final BiConsumer<BaseCosmetic, Player> selectionHandler;

    CosmeticType(String permissionKey, String description, BiConsumer<BaseCosmetic, Player> selectionHandler) {
        this.permissionKey = permissionKey;
        this.description = description;
        this.selectionHandler = selectionHandler;
    }

    public void handleSelection(BaseCosmetic cosmetic, Player player) {
        selectionHandler.accept(cosmetic, player);
    }

    public static CosmeticType fromString(String input) {
        if (input == null) {
            return null;
        }
        String sanitizedInput = input.replace("-", "").replace("_", "").toUpperCase();

        for (CosmeticType type : values()) {
            String sanitizedEnumName = type.name().replace("_", "");
            if (sanitizedEnumName.equals(sanitizedInput)) {
                return type;
            }

            String sanitizedPermissionKey = type.permissionKey.replace("-", "").replace("_", "").toUpperCase();
            if (sanitizedPermissionKey.equals(sanitizedInput)) {
                return type;
            }
        }
        return null;
    }
}