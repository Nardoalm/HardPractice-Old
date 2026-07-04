package live.pvphard.practice.core.profile.command.player.setting.worldtime;

import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

/**
 * @author Emmy
 * @project Hard
 * @date 13/10/2024 - 10:25
 */
public class ToggleWorldTimeCommand extends BaseCommand {
    @CommandData(
            name = "toggleworldtime",
            cooldown = 1,
            usage = "toggleworldtime",
            description = "Alterna entre suas configurações de horário do mundo."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        Profile profile = this.getProfile(player.getUniqueId());

        switch (profile.getProfileData().getSettingData().getWorldTime()) {
            case DEFAULT:
                profile.getProfileData().getSettingData().setTimeDay(player);
                player.sendMessage(this.getString(GlobalMessagesLocaleImpl.PROFILE_WORLD_TIME_SET).replace("{time}", profile.getProfileData().getSettingData().getTime().toLowerCase()));
                break;
            case DAY:
                profile.getProfileData().getSettingData().setTimeSunset(player);
                player.sendMessage(this.getString(GlobalMessagesLocaleImpl.PROFILE_WORLD_TIME_SET).replace("{time}", profile.getProfileData().getSettingData().getTime().toLowerCase()));
                break;
            case SUNSET:
                profile.getProfileData().getSettingData().setTimeNight(player);
                player.sendMessage(this.getString(GlobalMessagesLocaleImpl.PROFILE_WORLD_TIME_SET).replace("{time}", profile.getProfileData().getSettingData().getTime().toLowerCase()));
                break;
            case NIGHT:
                profile.getProfileData().getSettingData().setTimeDefault(player);
                player.sendMessage(this.getString(GlobalMessagesLocaleImpl.PROFILE_WORLD_TIME_SET).replace("{time}", profile.getProfileData().getSettingData().getTime().toLowerCase()));
                break;
        }
    }
}
