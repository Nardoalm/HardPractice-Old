package live.pvphard.practice.feature.kit.command.impl.data;

import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.feature.kit.KitService;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * @author Emmy
 * @project Hard
 * @date 28/04/2024 - 22:46
 */
public class KitSetDisplayNameCommand extends BaseCommand {
    @CommandData(
            name = "kit.displayname",
            aliases = "kit.setdisplayname",
            isAdminOnly = true,
            usage = "kit displayname <kitName> <displayName>",
            description = "Sets the display name of a kit."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (command.length() < 2) {
            command.sendUsage();
            return;
        }

        KitService kitService = this.plugin.getService(KitService.class);
        Kit kit = kitService.getKit(args[0]);
        if (kit == null) {
            player.sendMessage(CC.translate(this.getString(GlobalMessagesLocaleImpl.KIT_NOT_FOUND)));
            return;
        }

        String displayName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        kit.setDisplayName(displayName);
        this.plugin.getService(KitService.class).saveKit(kit);
        player.sendMessage(CC.translate(this.getString(GlobalMessagesLocaleImpl.KIT_DISPLAYNAME_SET)).replace("{kit-name}", kit.getName()).replace("{display-name}", displayName));
    }
}