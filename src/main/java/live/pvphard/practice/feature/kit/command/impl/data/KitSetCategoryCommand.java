package live.pvphard.practice.feature.kit.command.impl.data;

import live.pvphard.practice.common.text.CC;
import live.pvphard.practice.common.text.EnumFormatter;
import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.feature.kit.Kit;
import live.pvphard.practice.feature.kit.KitCategory;
import live.pvphard.practice.feature.kit.KitService;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.command.CommandSender;

/**
 * @author Emmy
 * @project Hard
 * @since 01/05/2025
 */
public class KitSetCategoryCommand extends BaseCommand {
    @CommandData(
            name = "kit.setcategory",
            isAdminOnly = true,
            inGameOnly = false,
            usage = "kit setcategory <kitName> <category>",
            description = "Define a categoria de um kit."
    )
    @Override
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (command.length() < 2) {
            command.sendUsage();
            return;
        }

        KitService kitService = this.plugin.getService(KitService.class);
        Kit kit = kitService.getKit(args[0]);
        if (kit == null) {
            sender.sendMessage(this.getString(GlobalMessagesLocaleImpl.KIT_NOT_FOUND));
            return;
        }

        KitCategory category;

        try {
            category = KitCategory.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage(EnumFormatter.outputAvailableValues(KitCategory.class));
            return;
        }

        kit.setCategory(category);
        kitService.saveKit(kit);
        sender.sendMessage(CC.translate(this.getString(GlobalMessagesLocaleImpl.KIT_CATEGORY_SET).replace("{kit-name}", kit.getName()).replace("{category}", category.getName())));
    }
}
