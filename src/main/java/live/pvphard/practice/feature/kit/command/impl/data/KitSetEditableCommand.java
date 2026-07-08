package live.pvphard.practice.feature.kit.command.impl.data;

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
 * @since 04/05/2025
 */
public class KitSetEditableCommand extends BaseCommand {
    @CommandData(
            name = "kit.seteditable",
            isAdminOnly = true,
            inGameOnly = false,
            usage = "kit seteditable <name> <true/false>",
            description = "Sets whether a kit is editable or not."
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

        boolean editable;
        try {
            editable = Boolean.parseBoolean(args[1]);
        } catch (Exception exception) {
            sender.sendMessage(CC.translate("&cInvalid value for editable! Use true or false."));
            return;
        }

        kit.setEditable(editable);
        kitService.saveKit(kit);
        sender.sendMessage(CC.translate(this.getString(GlobalMessagesLocaleImpl.KIT_SET_EDITABLE)
                .replace("{kit-name}", kit.getName())
                .replace("{editable}", String.valueOf(editable))));
    }
}
