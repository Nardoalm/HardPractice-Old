package live.pvphard.practice.core.profile.command.player.setting.toggle;

import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.data.types.ProfileSettingData;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

public class TogglePingRangeCommand extends BaseCommand {

    @CommandData(
            name = "togglepingrange",
            cooldown = 1,
            usage = "togglepingrange",
            description = "Toggles the matchmaking ping difference range limit."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        if (player == null) {
            return;
        }

        Profile profile = this.getProfile(player.getUniqueId());
        if (profile == null || profile.getProfileData() == null) {
            player.sendMessage(CC.translate("&cYour profile has not been loaded yet."));
            return;
        }

        ProfileSettingData settingData = profile.getProfileData().getSettingData();
        if (settingData == null) {
            player.sendMessage(CC.translate("&cYour settings are currently unavailable."));
            return;
        }

        settingData.cyclePingRange();
        player.sendMessage(CC.translate("&aMatchmaking ping range: &b" + settingData.getPingRangeDisplay() + "&a."));
    }
}