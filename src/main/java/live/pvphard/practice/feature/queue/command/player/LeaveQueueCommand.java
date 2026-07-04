package live.pvphard.practice.feature.queue.command.player;

import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.enums.ProfileState;
import live.pvphard.practice.common.text.CC;
import org.bukkit.entity.Player;

/**
 * @author Remi
 * @project Hard
 * @date 5/21/2024
 */
public class LeaveQueueCommand extends BaseCommand {
    @CommandData(
            name = "leavequeue",
            usage = "leavequeue",
            description = "Sai da fila atual."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        ProfileService profileService = this.plugin.getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());
        if (!profile.getState().equals(ProfileState.WAITING)) {
            player.sendMessage(CC.translate("&cVocê não está em nenhuma fila."));
            return;
        }

        if (profile.getParty() != null && !profile.getParty().isLeader(player)) {
            player.sendMessage(CC.translate("&cVocê precisa ser o líder da party para sair da fila."));
            return;
        }

        if (profile.getQueueProfile() == null || profile.getQueueProfile().getQueue() == null) {
            player.sendMessage(CC.translate("&cVocê não está em nenhuma fila."));
            return;
        }

        profile.getQueueProfile().getQueue().removePlayer(profile.getQueueProfile());
    }
}
