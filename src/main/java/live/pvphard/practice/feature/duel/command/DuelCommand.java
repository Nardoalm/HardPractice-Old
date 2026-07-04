package live.pvphard.practice.feature.duel.command;

import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.feature.duel.DuelRequestService;
import live.pvphard.practice.feature.duel.menu.DuelRequestMenu;
import live.pvphard.practice.feature.server.ServerService;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

/**
 * @author Emmy
 * @project Hard
 * @date 17/10/2024 - 20:09
 */
public class DuelCommand extends BaseCommand {
    @CommandData(
            name = "duel",
            usage = "duel <player>",
            description = "Envia um pedido de duelo para um jogador"
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length < 1) {
            command.sendUsage();
            return;
        }

        Player target = player.getServer().getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_INVALID_PLAYER).replace("{input}", args[0]));
            return;
        }

        if (target == player) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_DUEL_REQUESTS_CANT_DUEL_SELF));
            return;
        }

        DuelRequestService duelRequestService = this.plugin.getService(DuelRequestService.class);
        if (duelRequestService.getDuelRequest(player, target) != null) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_DUEL_REQUESTS_ALREADY_PENDING));
            return;
        }

        ServerService serverService = this.plugin.getService(ServerService.class);
        if (!serverService.isQueueingAllowed()) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.QUEUE_TEMPORARILY_DISABLED));
            player.closeInventory();
            return;
        }

        Profile targetProfile = this.plugin.getService(ProfileService.class).getProfile(target.getUniqueId());
        if (!targetProfile.getProfileData().getSettingData().isReceiveDuelRequestsEnabled()) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_DUEL_REQUESTS_REQUESTS_DISABLED_PLAYER)
                    .replace("{name-color}", String.valueOf(targetProfile.getNameColor()))
                    .replace("{player}", target.getName())
            );
            return;
        }

        if (targetProfile.isBusy()) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_PLAYER_IS_BUSY)
                    .replace("{name-color}", String.valueOf(targetProfile.getNameColor()))
                    .replace("{player}", target.getName())
            );
            return;
        }

        new DuelRequestMenu(target).openMenu(player);
    }
}