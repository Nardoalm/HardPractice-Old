package us.alleypvp.practice.feature.spawn.command;

import us.alleypvp.practice.AlleyPractice;
import us.alleypvp.practice.common.PlayerUtil;
import us.alleypvp.practice.core.locale.LocaleService;
import us.alleypvp.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import us.alleypvp.practice.core.profile.Profile;
import us.alleypvp.practice.core.profile.ProfileService;
import us.alleypvp.practice.core.profile.enums.ProfileState;
import us.alleypvp.practice.feature.ffa.FFAMatch;
import us.alleypvp.practice.feature.ffa.FFAState;
import us.alleypvp.practice.feature.hotbar.HotbarService;
import us.alleypvp.practice.feature.spawn.SpawnService;
import us.alleypvp.practice.library.command.BaseCommand;
import us.alleypvp.practice.library.command.CommandArgs;
import us.alleypvp.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

/**
 * @author Emmy
 * @project Alley
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
                player.sendMessage(AlleyPractice.getInstance().getService(LocaleService.class).getString(GlobalMessagesLocaleImpl.ERROR_YOU_MUST_BE_IN_LOBBY));
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