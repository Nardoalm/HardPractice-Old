package live.pvphard.practice.feature.cosmetic.command.impl.admin;

import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.common.text.StringUtil;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.core.profile.Profile;
import live.pvphard.practice.core.profile.ProfileService;
import live.pvphard.practice.core.profile.data.types.ProfileCosmeticData;
import live.pvphard.practice.feature.cosmetic.model.CosmeticType;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @author Remi
 * @project Hard
 * @date 6/1/2024
 */
public class CosmeticGetSelectedCommand extends BaseCommand {
    @CommandData(
            name = "cosmetic.getselected",
            aliases = {"cosmetic.get"},
            isAdminOnly = true,
            usage = "cosmetic get <player>",
            description = "Gets the selected cosmetics of a player."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length != 1) {
            command.sendUsage();
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.ERROR_INVALID_PLAYER));
            return;
        }

        Profile profile = this.plugin.getService(ProfileService.class).getProfile(target.getUniqueId());
        player.sendMessage(CC.translate("     &b&lSelected cosmetics of " + target.getName()));

        ProfileCosmeticData cosmeticData = profile.getProfileData().getCosmeticData();

        for (CosmeticType type : CosmeticType.values()) {
            String selectedName = cosmeticData.getSelected(type);

            String friendlyTypeName = StringUtil.formatEnumName(type);

            player.sendMessage(CC.translate(String.format("      &f◆ &b%s: &f%s", friendlyTypeName, selectedName)));
        }
    }
}
