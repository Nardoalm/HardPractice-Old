package live.pvphard.practice.feature.hotbar.command.impl;

import live.pvphard.practice.core.locale.internal.impl.message.GlobalMessagesLocaleImpl;
import live.pvphard.practice.feature.hotbar.HotbarItem;
import live.pvphard.practice.feature.hotbar.HotbarService;
import live.pvphard.practice.library.command.BaseCommand;
import live.pvphard.practice.library.command.CommandArgs;
import live.pvphard.practice.library.command.annotation.CommandData;
import org.bukkit.entity.Player;

/**
 * @author Emmy
 * @project hard-practice
 * @since 26/07/2025
 */
public class HotbarDeleteCommand extends BaseCommand {
    @CommandData(
            name = "hotbar.delete",
            isAdminOnly = true,
            usage = "hotbar delete <name>",
            description = "Exclui um item de hotbar salvo."
    )
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length < 1) {
            command.sendUsage();
            return;
        }

        HotbarService hotbarService = this.plugin.getService(HotbarService.class);

        String name = args[0];

        HotbarItem hotbarItem = hotbarService.getHotbarItem(name);
        if (hotbarItem == null) {
            player.sendMessage(this.getString(GlobalMessagesLocaleImpl.HOTBAR_NOT_FOUND).replace("{hotbar-name}", name));
            return;
        }

        hotbarService.deleteHotbarItem(hotbarItem);
        player.sendMessage(this.getString(GlobalMessagesLocaleImpl.HOTBAR_DELETED_ITEM).replace("{hotbar-name}", name));
    }
}
