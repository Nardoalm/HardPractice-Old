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
 * @project Hard
 * @date 25/05/2024 - 23:35
 */

public class ToggleScoreboardCommand extends BaseCommand {
    @CommandData(
            name = "togglescoreboard",
            aliases = {"tsb", "togglesb"},
            cooldown = 1,
            usage = "togglescoreboard",
            description = "Ativa ou desativa a scoreboard"
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        Profile profile = this.getProfile(player.getUniqueId());
        profile.getProfileData().getSettingData().setScoreboardEnabled(!profile.getProfileData().getSettingData().isScoreboardEnabled());

        player.sendMessage(CC.translate(this.getString(GlobalMessagesLocaleImpl.PROFILE_TOGGLED_SCOREBOARD)
                .replace("{status}", profile.getProfileData().getSettingData().isScoreboardEnabled() ? "&aenable" : "&cdisable"))
        );
    }
}
