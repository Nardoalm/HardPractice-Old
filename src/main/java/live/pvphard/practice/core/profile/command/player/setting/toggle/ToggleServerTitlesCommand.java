package live.pvphard.practice.core.profile.command.player.setting.toggle;

import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

/**
 * @author Emmy
 * @project hard-practice
 * @since 19/07/2025
 */
public class ToggleServerTitlesCommand extends BaseCommand {
    @CommandData(
            name = "toggleservertitles",
            cooldown = 1,
            usage = "toggleservertitles",
            description = "Ativa ou desativa os títulos do servidor"
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        Profile profile = this.getProfile(player.getUniqueId());
        profile.getProfileData().getSettingData().setServerTitles(!profile.getProfileData().getSettingData().isServerTitles());

        player.sendMessage(CC.translate(this.getString(GlobalMessagesLocaleImpl.PROFILE_TOGGLED_SERVER_TITLES)
                .replace("{status}", profile.getProfileData().getSettingData().isServerTitles() ? "&aenable" : "&cdisable"))
        );
    }
}
