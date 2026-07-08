package live.pvphard.practice.feature.kit.command.impl.manage;

import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.feature.kit.KitService;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.command.CommandSender;

/**
 * @author Emmy
 * @project Hard
 * @since 11/04/2025
 */
public class KitToggleCommand extends BaseCommand {
    @CommandData(
            name = "kit.toggle",
            isAdminOnly = true,
            inGameOnly = false,
            usage = "kit toggle <kitName>",
            description = "Toggles the enabled status of a kit."
    )
    @Override
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (args.length < 1) {
            command.sendUsage();
            return;
        }

        String kitName = args[0];
        KitService kitService = this.plugin.getService(KitService.class);
        Kit kit = kitService.getKit(kitName);
        if (kit == null) {
            sender.sendMessage(CC.translate(this.getString(GlobalMessagesLocaleImpl.KIT_NOT_FOUND)));
            return;
        }

        if (kit.getIcon() == null) {
            sender.sendMessage(CC.translate("&cThis kit has no icon set. Define an icon before toggling."));
            return;
        }

        if (kit.getCategory() == null) {
            sender.sendMessage(CC.translate("&cThis kit has no category set. Define a category before toggling."));
            return;
        }

        if (kit.getKitSettings().isEmpty()) {
            sender.sendMessage(CC.translate("&cThis kit has no settings defined. Configure the kit options before toggling."));
            return;
        }

        kit.setEnabled(!kit.isEnabled());
        kitService.saveKit(kit);
        String status = kit.isEnabled() ? CC.translate("&aenabled") : CC.translate("&cdisabled");
        sender.sendMessage(CC.translate("&aYou " + status + " &athe kit &b" + kit.getName() + "&a."));
    }
}
