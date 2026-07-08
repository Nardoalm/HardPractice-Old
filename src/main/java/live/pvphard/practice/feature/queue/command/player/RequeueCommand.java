package live.pvphard.practice.feature.queue.command.player;
 
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.enums.ProfileState;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.feature.queue.Queue;
import live.pvphard.practice.feature.queue.QueueService;
import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.profile.data.types.ProfileRankedKitData;
import org.bukkit.entity.Player;
 
/**
 * @author Antigravity
 * @project hard-practice
 * @date 08/07/2026
 */
public class RequeueCommand extends BaseCommand {
 
    @CommandData(
            name = "requeue",
            usage = "requeue",
            description = "Re-enter the last played match queue."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        ProfileService profileService = this.plugin.getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());
 
        if (profile.getState() != ProfileState.LOBBY) {
            player.sendMessage(CC.translate("&cYou can only use this command in the lobby."));
            return;
        }
 
        if (profile.getParty() != null && !profile.getParty().isLeader(player)) {
            player.sendMessage(CC.translate("&cYou must be the party leader to enter the queue."));
            return;
        }
 
        Kit lastKit = profile.getLastPlayedKit();
        if (lastKit == null) {
            player.sendMessage(CC.translate("&cYou have not played any matches recently in this session."));
            return;
        }
 
        boolean ranked = profile.isLastPlayedRanked();
        boolean duos = profile.isLastPlayedDuos();
 
        QueueService queueService = this.plugin.getService(QueueService.class);
        Queue matchingQueue = queueService.getQueues().stream()
                .filter(queue -> queue.getKit().equals(lastKit) && queue.isRanked() == ranked && queue.isDuos() == duos)
                .findFirst()
                .orElse(null);
 
        if (matchingQueue == null) {
            player.sendMessage(CC.translate("&cCould not find the corresponding queue for the last played match."));
            return;
        }
 
        int elo = 0;
        if (matchingQueue.isRanked()) {
            elo = 1000;
            if (profile.getProfileData() != null && profile.getProfileData().getRankedKitData() != null) {
                ProfileRankedKitData rankedData = profile.getProfileData().getRankedKitData().get(matchingQueue.getKit().getName());
                if (rankedData != null) {
                    elo = rankedData.getElo();
                }
            }
        }
 
        matchingQueue.addPlayer(player, elo);
    }
}
