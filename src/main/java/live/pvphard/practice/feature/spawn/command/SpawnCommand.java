package live.pvphard.practice.feature.spawn.command;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.common.PlayerUtil;
import live.pvphard.practice.core.locale.LocaleService;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.enums.ProfileState;
import live.pvphard.practice.feature.ffa.FFAMatch;
import live.pvphard.practice.feature.ffa.FFAState;
import live.pvphard.practice.feature.hotbar.HotbarService;
import live.pvphard.practice.feature.spawn.SpawnService;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

/**
 * @author Emmy
 * @project Hard
 * @date 29/04/2024 - 19:01
 */
public class SpawnCommand extends BaseCommand {
    @CommandData(
            name = "spawn",
            isAdminOnly = true,
            usage = "spawn",
            description = "Teleporta para o spawn."
    )
    @Override
    public void onCommand(CommandArgs args) {
        Player player = args.getPlayer();
        ProfileService profileService = this.plugin.getService(ProfileService.class);
        Profile profile = profileService.getProfile(player.getUniqueId());
        ProfileState state = profile.getState();

        switch (state) {
            case FFA:
                FFAMatch ffaMatch = profile.getFfaMatch();
                if (ffaMatch == null) return;

                if (ffaMatch.getGameFFAPlayer(player).getState() == FFAState.FIGHTING) {
                    ffaMatch.teleportToSafeZone(player);
                } else {
                    ffaMatch.leave(player);
                }
                break;
            case PLAYING:
                player.sendMessage(HardPractice.getInstance().getService(LocaleService.class).getString(GlobalMessagesLocaleImpl.ERROR_YOU_MUST_BE_IN_LOBBY));
            case SPECTATING:
                profile.getMatch().removeSpectator(player, false);
                break;
            default:
                this.sendToSpawn(player);
                break;
        }
    }

    /**
     * Sends the player to the spawn location and resets their state.
     *
     * @param player The player to send to spawn.
     */
    public void sendToSpawn(Player player) {
        PlayerUtil.reset(player, false, true);

        this.plugin.getService(SpawnService.class).teleportToSpawn(player);
        this.plugin.getService(HotbarService.class).applyHotbarItems(player);

        player.sendMessage(this.getString(GlobalMessagesLocaleImpl.SPAWN_TELEPORTED));
    }
}