package live.pvphard.practice.feature.match.listener.types;

import live.pvphard.practice.HardPractice;
import live.pvphard.practice.core.config.ConfigService;
import live.pvphard.practice.core.locale.LocaleService;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.feature.match.Match;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

public class MatchChatListener implements Listener {

    @EventHandler
    private void onCommandPreProcess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("kaos.match.commands.bypass")) {
            return;
        }

        ProfileService profileService = HardPractice.getInstance().getService(ProfileService.class);
        LocaleService localeService = HardPractice.getInstance().getService(LocaleService.class);
        ConfigService configService = HardPractice.getInstance().getService(ConfigService.class);

        Profile profile = profileService.getProfile(player.getUniqueId());
        if (profile == null) {
            return;
        }

        Match match = profile.getMatch();
        if (match == null) {
            return;
        }

        String commandInput = event.getMessage().toLowerCase();
        String command = commandInput.split(" ")[0].replace("/", "");

        List<String> blockedCommands = configService.getSettingsConfig().getStringList("game.blocked-commands");

        if (blockedCommands != null && blockedCommands.contains(command)) {
            player.sendMessage(localeService.getString(GlobalMessagesLocaleImpl.MATCH_COMMAND_BLOCKED));
            event.setCancelled(true);
        }
    }
}