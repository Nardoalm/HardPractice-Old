package us.alleypvp.practice.core.profile.menu.setting;

import us.alleypvp.practice.AlleyPractice;
import us.alleypvp.practice.library.menu.Button;
import us.alleypvp.practice.library.menu.Menu;
import us.alleypvp.practice.core.profile.ProfileService;
import us.alleypvp.practice.core.profile.Profile;
import us.alleypvp.practice.core.profile.data.types.ProfileSettingData;
import us.alleypvp.practice.core.profile.menu.setting.button.PracticeSettingsButton;
import us.alleypvp.practice.core.profile.menu.setting.enums.PracticeSettingType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Emmy
 * @project Alley
 * @since 21/04/2025
 */
public class PracticeSettingsMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "&b&lPractice Settings";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        Profile profile = AlleyPractice.getInstance().getService(ProfileService.class).getProfile(player.getUniqueId());
        ProfileSettingData settings = profile.getProfileData().getSettingData();

        for (PracticeSettingType type : PracticeSettingType.values()) {
            buttons.put(type.slot, new PracticeSettingsButton(
                    type,
                    type.displayName,
                    type.material,
                    type.durability,
                    type.loreProvider.apply(settings)
            ));
        }

        this.addBorder(buttons, 15, 5);

        return buttons;
    }

    @Override
    public int getSize() {
        return 9 * 5;
    }
}
