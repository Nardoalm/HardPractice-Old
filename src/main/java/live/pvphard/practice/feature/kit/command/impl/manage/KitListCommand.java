package live.pvphard.practice.feature.kit.command.impl.manage;

import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.feature.kit.KitService;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.command.CommandSender;

/**
 * @author Emmy
 * @project Hard
 * @date 28/04/2024 - 22:07
 */
public class KitListCommand extends BaseCommand {
    @CommandData(
            name = "kit.list",
            aliases = {"kits"},
            isAdminOnly = true,
            usage = "kit list",
            description = "Sends a list of all kits."
    )
    @Override
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        KitService kitService = this.plugin.getService(KitService.class);

        sender.sendMessage("");
        sender.sendMessage(CC.translate("     &b&lKit List &f(" + kitService.getKits().size() + "&f)"));
        if (kitService.getKits().isEmpty()) {
            sender.sendMessage(CC.translate("      &f◆ &cNo kits available."));
        }
        kitService.getKits().forEach(kit -> sender.sendMessage(CC.translate("      &f◆ &b" + kit.getDisplayName() + " &f(" + (kit.isEnabled() ? "&aEnabled" : "&cDisabled") + "&f)")));
        sender.sendMessage("");
    }
}
