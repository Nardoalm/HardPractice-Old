package live.pvphard.practice.feature.kit.setting;

import live.pvphard.practice.feature.kit.setting.annotation.KitSettingData;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * @author Remi
 * @project Hard
 * @date 5/21/2024
 */
@Getter
@Setter
public class KitSetting {
    private final String name;
    private final String description;
    private boolean enabled;

    public KitSetting() {
        KitSettingData data = getClass().getAnnotation(KitSettingData.class);
        this.name = Objects.requireNonNull(data).name();
        this.description = data.description();
        this.enabled = data.enabled();
    }
}