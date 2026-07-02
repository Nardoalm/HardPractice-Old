package us.alleypvp.practice.core.profile.data.command.ranked.impl;

import us.alleypvp.practice.common.text.CC;
import us.alleypvp.practice.core.profile.Profile;
import us.alleypvp.practice.core.profile.ProfileService;
import us.alleypvp.practice.feature.hotbar.HotbarService;
import us.alleypvp.practice.library.command.BaseCommand;
import us.alleypvp.practice.library.command.CommandArgs;
import us.alleypvp.practice.library.command.annotation.CommandData;
import us.alleypvp.practice.library.menu.Menu;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Opens the ranked queue menu with appropriate ban validation.
 *
 * @author ysubz
 * @project Alley
 */
public class RankedMenuCommand extends BaseCommand {

    @CommandData(
            name = "ranked.menu",
            aliases = {"rankedmenu", "rqueue"},
            usage = "ranked menu",
            description = "Opens the ranked matchmaking queue menu."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        if (player == null) {
            return;
        }

        ProfileService profileService = this.plugin.getService(ProfileService.class);
        HotbarService hotbarService = this.plugin.getService(HotbarService.class);

        if (profileService == null || hotbarService == null) {
            player.sendMessage(CC.translate("&cServices are currently unavailable. Please try again later."));
            return;
        }

        Profile profile = profileService.getProfile(player.getUniqueId());
        if (profile == null || profile.getProfileData() == null) {
            player.sendMessage(CC.translate("&cYour profile data could not be loaded."));
            return;
        }

        // Enforcement of the ranked ban state based on your RankedBanListCommand logic
        if (profile.getProfileData().isRankedBanned()) {
            player.sendMessage(CC.translate("&cYou are currently banned from competitive ranked matches."));
            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0f, 1.0f);
            return;
        }

        try {
            // Resolves and opens RANKED_MENU natively using the implemented HotbarService provider
            Menu rankedMenu = hotbarService.getMenuInstanceFromName("RANKED_MENU", player);
            if (rankedMenu != null) {
                rankedMenu.openMenu(player);
                player.playSound(player.getLocation(), Sound.CLICK, 1.0f, 1.0f);
            } else {
                player.sendMessage(CC.translate("&cThe competitive menu configuration is missing."));
            }
        } catch (Exception exception) {
            player.sendMessage(CC.translate("&cAn error occurred while trying to process the interface request."));
            exception.printStackTrace();
        }
    }
}